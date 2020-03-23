package com.gpevnev.softwareengineering.reactive.persist

import akka.actor.ActorSystem
import akka.stream.Materializer
import akka.stream.alpakka.mongodb.scaladsl.{MongoSink, MongoSource}
import akka.stream.scaladsl.Source
import akka.{Done, NotUsed}
import com.gpevnev.softwareengineering.reactive.Domain._
import com.mongodb.reactivestreams.client.{MongoClient, MongoCollection, MongoDatabase}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.{ExecutionContext, Future}

object ProductPersist {
  def apply(db: MongoDatabase)
           (implicit ec: ExecutionContext,
            as: ActorSystem,
            mat: Materializer): ProductPersist = {

    val codecRegistry = fromRegistries(
      fromProviders(
        classOf[Product],
        classOf[Price],
        classOf[Currency]
      ),
      DEFAULT_CODEC_REGISTRY
    )

    val collection =
      db.getCollection("product", classOf[Product])
        .withCodecRegistry(codecRegistry)

    new ProductPersist(collection)
  }
}

class ProductPersist(productCollection: MongoCollection[Product])
                 (implicit ec: ExecutionContext,
                  as: ActorSystem,
                  mat: Materializer) {
  def addProduct(user: Product): Future[Done] = {
    Source.single(user)
      .runWith(MongoSink.insertOne(productCollection))
  }

  def listProducts(): Source[Product, NotUsed] = {
    MongoSource(productCollection.find(classOf[Product]))
  }

}
