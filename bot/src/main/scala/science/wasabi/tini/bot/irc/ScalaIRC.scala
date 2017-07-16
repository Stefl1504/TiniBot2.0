/**
  * Created by Stefl1504 on 10.07.2017.
  */

import org.conbere.irc._
import science.wasabi.tini.bot.commands.webstream.Webstream
import Messages._
import akka.typed._
import akka.typed.scaladsl.Actor

class TwitchBot( val serverName:String
             , val nickName:String
             , val userName:String
             , val password:String
             , val realName:String
             , val rooms:List[Room])
  extends ClassicBot {

  def postPrivateMsg(arg:String){
    sender ! PrivMsg("" , Room("#forgecastdota"), "/host" + arg);
  }

  def receive = onConnect orElse defaultResponse



}

/*def twitchIRCActor (twitchBot: TwitchBot("irc.twitch.tv", "test", "test", "none", "tini_bot", List(Room("#forgecastdota")))): Behavior[String] = Actor.immutable{
  (ctx, output)
}*/