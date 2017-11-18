package com.mucahit.user.serialization

import java.io.NotSerializableException

import akka.serialization.SerializerWithStringManifest
import com.mucahit.user.model.UserCreatedEvent
import com.trueaccord.scalapb.GeneratedMessageCompanion
import com.mucahit.protobuf

class UserServiceSerializer extends SerializerWithStringManifest {


  // this mut be identicle
  override def identifier: Int = 70000

  // when your messages serializes it serializes like (manifest,bytes)
  override def manifest(o: AnyRef): String = s"${o.getClass.getName}|$currentVersion"

  final val v1: String = "1"
  final val v2: String = "2"
  final val v3: String = "3"

  final val currentVersion: String = v3

  final val UserCreatedEventManifest: String = classOf[UserCreatedEvent].getName

  // serializing message into bytes using ScalaPB generated class' toByteArray function
  override def toBinary(o: AnyRef): Array[Byte] = o match {
    //    case UserCreatedEvent(name: String)     => protobuf.UserCreatedEventv1(name).toByteArray
    //    case UserCreatedEvent(name, id)         => protobuf.UserCreatedEventv2(name, id).toByteArray
    case UserCreatedEvent(id) => protobuf.UserCreatedEventv3(id).toByteArray
  }

  // deserializes message by matching manifest and version
  override def fromBinary(bytes: Array[Byte], manifest: String): AnyRef = {

    implicit val bytes_ : Array[Byte] = bytes

    manifest.split('|').toList match {
      case UserCreatedEventManifest :: `v1` :: Nil => fromBytes(protobuf.UserCreatedEventv1) { msg => UserCreatedEvent(-1) }
      case UserCreatedEventManifest :: `v2` :: Nil => fromBytes(protobuf.UserCreatedEventv2) { msg => UserCreatedEvent(msg.id) }
      case UserCreatedEventManifest :: `v3` :: Nil => fromBytes(protobuf.UserCreatedEventv3) { msg => UserCreatedEvent(msg.id) }
      case _                                       => throw new NotSerializableException(s"Unable to handle manifest: [[$manifest]], currentVersion: [[$currentVersion]] ")

    }

  }

  // this is a little helper method for converting any generated message to our custom classes easily
  def fromBytes[T <: com.trueaccord.scalapb.GeneratedMessage with com.trueaccord.scalapb.Message[T]]
  (msg: GeneratedMessageCompanion[T])(handler: T ⇒ AnyRef)(implicit bytes: Array[Byte]): AnyRef = handler(msg.parseFrom(bytes))
}
