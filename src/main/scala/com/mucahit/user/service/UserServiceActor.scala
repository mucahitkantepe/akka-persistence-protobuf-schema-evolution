package com.mucahit.user.service

import akka.actor.{ActorLogging, Props, Timers}
import akka.persistence.{PersistentActor, RecoveryCompleted}
import com.mucahit.user.model.{CreateUserCommand, UserCreatedEvent, UserEntity}

class UserServiceActor extends PersistentActor with ActorLogging with Timers {

  import UserServiceActor._

  implicit var registry: UserRegistry = Set.empty

  override def receiveRecover: Receive = {

    case event: UserCreatedEvent => registry = insert(event.id)

    case x: RecoveryCompleted => log.debug(s"Recovery Completed, current registry: $registry")

  }

  override def receiveCommand: Receive = {

    case cmd: CreateUserCommand => persist(cmd.getEvent) { userCreatedEvent =>
      log.debug(s"Persisted: $userCreatedEvent")
      registry = insert(userCreatedEvent.id)
    }

  }

  override def persistenceId: String = "UserService"

  override def preStart(): Unit = {
    log.info("UserServiceActor started.")
  }


}

object UserServiceActor {

  type UserRegistry = Set[UserEntity]

  def props = Props(new UserServiceActor)

  def insert(id: Long)(implicit registry: UserRegistry): UserRegistry = registry + UserEntity(id)

}
