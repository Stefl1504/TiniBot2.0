package science.wasabi.tini.bot.commands.webstream

import science.wasabi.tini.bot.discord.ingestion.JdaIngestionActor._
import science.wasabi.tini.bot.discord.ingestion.JdaIngestionActor
import science.wasabi.tini.bot.discord.wrapper.DiscordMessage
import science.wasabi.tini.bot.commands.Command
import science.wasabi.tini.config.Config
import scala.util.matching.Regex
import upickle.default._
import akka.typed._
import akka.typed.scaladsl.Actor

object Webstream{

  implicit val config = Config.conf

  //RAW (!cast\s+(-(ana|obs|coc)\s+@\w+#\d*\s*|@\w+#\d+\s*){1,4}(\w+\s+vs\s+\w+)?)
  final val regCommandExp = "!cast\\s+(-(ana|obs|coc)\\s+<@\\d+>*\\s*|<@\\d+>\\s*){1,4}(\\w+\\s+vs\\s+\\w+)?".r
  //RAW (-(cas|ana|obs|coc))?\s*@\w+#\d+
  final val regCasterExp = "(-(cas|ana|obs|coc))?\\s*<@\\d+>".r

  final val regRoleExp = "-(cas|ana|obs|coc)".r

  final val roleLibrary = Map("-cas" -> 2, "-ana" -> 1, "-obs" -> 3,"-coc" -> 4)

  trait Event
  case class Cast() extends Event
  case class Host() extends Event

  object CastCommand extends Command{override def prefix: String = "!cast"}
  object HostCommand extends Command{override def prefix: String = "!host"}
  object CastHelpCommand extends Command{override def prefix: String = "!cast help"}


  /**
    *
    * @param message a discord message to parse
    * @param sendToServer send to specified server
    * @param sendToTwitch not yet Implemented, default= false
    * @return
    */
  def parseCast(message: DiscordMessage) = {
    regCommandExp.findFirstMatchIn(message.content) match {
      case Some(_) =>
        val casterMap = regCasterExp.findAllMatchIn(message.content).map(caster => {
          regRoleExp.findFirstMatchIn(caster.toString) match {
            case Some(x) =>
              x.after.toString.trim().drop(2).takeWhile(c => c != '>') -> Seq(roleLibrary(x.toString), 150490123478009777l)
            case None =>
              caster.toString.trim().drop(2).takeWhile(c => c != '>') -> Seq(2, 150490123478009777l)
          }
        }).toMap
        val pickledData = DataPackets.pickleCasters(casterMap)
        if (config.serverSend) {

        }
        if (config.twitchChannelSend){

        }

        "The Data was pickled as follows: " + write(pickledData)
      case None =>
        "!cast command malformed"
    }
  }

  def castHelp() = {
    "`!cast @mention` is the minimal command, you can place up to 4 mentions, each mention can have a precending " +
      " role modifier: `-ana` for Analyst `-cas` for Caster(default) `-coc` for Co-Caster and `-obs` for Observer," +
      "a `team1 vs team2` modifier can be put at the end of the line."
  }

  def webstreamActor(api: ActorRef[JdaCommands]): Behavior[DiscordMessage] = Actor.immutable {
    (ctx, message) => message.content match{
      case CastHelpCommand(args) =>
        api ! SendMessage(message.createUserReply(castHelp()))
        Actor.same
      case CastCommand(args) =>
        api ! SendMessage(message.createReply(parseCast(message)))
        Actor.same
    }
  }
}
