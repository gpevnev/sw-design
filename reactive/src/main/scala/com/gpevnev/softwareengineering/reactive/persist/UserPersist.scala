package com.gpevnev.softwareengineering.reactive.persist

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.mongodb.scaladsl.{MongoSink, MongoSource}
import akka.stream.scaladsl.{Sink, Source}
import akka.{Done, NotUsed}
import com.gpevnev.softwareengineering.reactive.Domain._
import com.mongodb.reactivestreams.client.{MongoClient, MongoCollection, MongoDatabase}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.{ExecutionContext, Future}

object UserPersist {
  def apply(db: MongoDatabase)
           (implicit ec: ExecutionContext,
           as: ActorSystem,
           mat: Materializer): UserPersist = {

    val codecRegistry = fromRegistries(
      fromProviders(
        classOf[User],
        classOf[Currency]
        ),
      DEFAULT_CODEC_REGISTRY
      )

    val collection =
      db.getCollection("user", classOf[User])
        .withCodecRegistry(codecRegistry)

    new UserPersist(collection)
  }
}

class UserPersist(userCollection: MongoCollection[User])
                 (implicit ec: ExecutionContext,
                  as: ActorSystem,
                  mat: Materializer) {
  def addUser(user: User): Future[Done] = {
    Source.single(user)
      .runWith(MongoSink.insertOne(userCollection))
  }

  def getUser(username: String): Future[User] = {
    listUsers()
      .filter(_.handle == username)
      .runWith(Sink.head)
  }

  def listUsers(): Source[User, NotUsed] = {
    MongoSource[User](userCollection.find[User](classOf[User]))
  }

}
