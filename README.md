## Akka-Persistence-Protobuf-Schema-Evolution

This project includes 3 functionalities together:

1. Using [Akka Persistence](https://doc.akka.io/docs/akka/2.5/scala/persistence.html) as EventSourcing 
2. Using [Protocol Buffers](https://developers.google.com/protocol-buffers/) within Akka Persistence([ScalaPB](https://github.com/scalapb/ScalaPB) used)
3. Simple example of [Schema Evolution](https://doc.akka.io/docs/akka/2.5/scala/persistence-schema-evolution.html)


### Schema Evolution with Protocol Buffers using Akka Persistence

####Problem Definition
Suppose that you have a UserEntity class that contains only `name`
and your journal has already stored some events based on this entity;

```scala
case class UserEntity(id: Long)
case class UserCreatedEvent(id: Long)
```
but you need to add another field to this entity, so Event Message should also be changed so;
```scala
case class UserEntity(id: Long, name: String)
case class UserCreatedEvent(id: Long, name: String)
```
and you might want to remove `id` field of entity.
```scala
case class UserEntity(name: String)
case class UserCreatedEvent(name: String)
```

So the thing you should do is to transform these event messages before you use, you can do it simply by creating a
custom serializer.

####Preparation
1. Create .proto messages
2. Create custom serializer

######Proto messages
```proto
syntax = "proto3";
message UserCreatedEventv1 {
    string name = 1;
}
message UserCreatedEventv2 {
    string name = 1;
    int64 id = 2;
}
message UserCreatedEventv3 {
    int64 id = 2;
}
```
ScalaPB will [generate](https://scalapb.github.io/generated-code.html) Protocol Buffers classes when you compile project bu sbt


######CustomSerializer

I simply versioned manifest by
```scala
  final val v1: String = "1"
  final val v2: String = "2"
  final val v3: String = "3"
  final val currentVersion: String = v3
  override def manifest(o: AnyRef): String = s"${o.getClass.getName}|$currentVersion"
```

When a message is being serialized by our Serializer, it will has this string manifest that created by `manifest` method
Also when a message is being deserialized by our Serializer we will understand the message type and manifet by this manifest


Serializing messages

This is very simple with ScalaPB, only thing you should do is to transform your custom messages into the generated
class by ScalaPB

```scala
  override def toBinary(o: AnyRef): Array[Byte] = o match {
        // these two lines are commented due to schema evolution
        //case UserCreatedEvent(name: String)     => protobuf.UserCreatedEventv1(name).toByteArray
        //case UserCreatedEvent(name, id)         => protobuf.UserCreatedEventv2(name, id).toByteArray
          case UserCreatedEvent(id)               => protobuf.UserCreatedEventv3(id).toByteArray
  }
  
```

Deserializing messages

Our messages are serialized using manifest and toBinary methods, so when a message is serialized, we can 
consider it as two parts: manifest and bytes, so we are going to use both of these parameters when we deserialize messages.

```scala
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

```
If incoming message has `v1` in its manifest, we are using `protobuf.UserCreatedEventv1` class to deserialize it.

Since our last custom event has only `id` field and `protobuf.UserCreatedEventv1` has only `name` field, we transform the message by using
an invalid identifier(magic number) like -1 as `UserCreatedEvent(-1)`

`v2` and `v3` conversions are very like to `v1` example.
