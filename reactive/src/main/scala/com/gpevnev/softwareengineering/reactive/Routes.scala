package com.gpevnev.softwareengineering.reactive

import akka.NotUsed
import akka.http.scaladsl.common.{EntityStreamingSupport, JsonEntityStreamingSupport}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.scaladsl.Source
import com.gpevnev.softwareengineering.reactive.Domain.{Currency, Price, Product, User}
import com.gpevnev.softwareengineering.reactive.persist.{ProductPersist, UserPersist}
import spray.json.RootJsonFormat

import scala.concurrent.ExecutionContext

object JsonProtocol
  extends SprayJsonSupport
    with spray.json.DefaultJsonProtocol {
  implicit val currencyFormat = jsonFormat1(Currency.apply)
  implicit val priceFormat: RootJsonFormat[Price] = jsonFormat2(Price.apply)
  implicit val productFormat: RootJsonFormat[Product] = jsonFormat2(Product.apply)
}

class Routes(userPersist: UserPersist,
             productPersist: ProductPersist)
            (implicit ec: ExecutionContext) {
  implicit val jsonStreamingSupport: JsonEntityStreamingSupport =
    EntityStreamingSupport.json()

  import JsonProtocol._

  val route: Route = {
    post {
      path("new_user") {
        parameters("name", "currency") { (name, currencyStr) =>
          Currency.fromString(currencyStr)
            .fold(reject) { currency =>
              complete(userPersist.addUser(User(name, currency)))
            }
        }
      } ~ path("new_product") {
        parameters("name", "currency", "price_amount".as[Int]) {
          (name, currencyStr, priceAmount) =>
            Currency.fromString(currencyStr)
              .fold(reject) { currency =>
                complete(productPersist.addProduct(
                  Product(name, Price(priceAmount, currency))
                ))
              }
        }
      }
    } ~ get {
      path("products") {
        parameters("username") { username =>
          complete {
            Source.fromFuture(
              userPersist.getUser(username)
            ).flatMapConcat { user =>
              productPersist
                .listProducts()
                .filter(_.price.currency == user.currency)
            }
          }
        }
      }
    }
  }

}
