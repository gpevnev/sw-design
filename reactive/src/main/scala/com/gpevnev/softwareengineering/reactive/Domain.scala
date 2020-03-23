package com.gpevnev.softwareengineering.reactive

object Domain {

  case class Currency(name: String)

  object Currency {
    val Usd: Currency = Currency("USD")
    val Eur: Currency = Currency("EUR")
    val Rub: Currency = Currency("RUB")

    def fromString(s: String): Option[Currency] = {
      s match {
        case "USD" => Some(Currency.Usd)
        case "EUR" => Some(Currency.Eur)
        case "RUB" => Some(Currency.Rub)
        case _ => None
      }
    }
  }


  case class Price(amount: Int, currency: Currency)

  case class Product(name: String, price: Price)

  case class User(handle: String, currency: Currency)
}
