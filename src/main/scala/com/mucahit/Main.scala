package com.mucahit

import akka.actor.ActorSystem
import com.mucahit.user.service.UserServiceActor

object Main extends App {

  val actorSystem: ActorSystem = ActorSystem("Main")
  actorSystem.actorOf(UserServiceActor.props, UserServiceActor.getClass.getSimpleName)

}



