package com.mucahit.user.model


sealed trait UserServiceMessage extends Serializable

sealed trait UserServiceEvent extends UserServiceMessage

sealed trait UserServiceCommand extends UserServiceMessage

case class UserEntity(id: Long)

case class CreateUserCommand(id: Long) extends UserServiceCommand {
  def getEvent: UserCreatedEvent = UserCreatedEvent(id)
}

case class UserCreatedEvent(id: Long) extends UserServiceEvent
