package com.gpevnev.softwareengineering.reactive

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{HttpApp, Route}
import akka.stream.{ActorMaterializer, Materializer}
import com.gpevnev.softwareengineering.reactive.persist.{ProductPersist, UserPersist}
import com.mongodb.reactivestreams.client.{MongoClient, MongoClients}

import scala.concurrent.ExecutionContextExecutor

object StoreHttpApp extends HttpApp {
  implicit val as: ActorSystem = ActorSystem("test")
  implicit val mat: Materializer = ActorMaterializer()
  implicit val ec: ExecutionContextExecutor = as.dispatcher

  val mongoClient: MongoClient = {
    val uri = "mongodb://root:example@localhost:27017"
    MongoClients.create(uri)
  }

  val db = mongoClient.getDatabase("reactive_test")


  val userPersist = UserPersist(db)
  val productPersist = ProductPersist(db)

  val routes1 = new Routes(userPersist, productPersist)

  override def routes: Route = routes1.route
}

object Main extends App {
  StoreHttpApp.startServer("127.0.0.1", 8080)
}