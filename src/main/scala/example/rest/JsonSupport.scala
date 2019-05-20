package example.rest

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import example.domain.{NewUser, User}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val newUserFormat: RootJsonFormat[NewUser] = jsonFormat3(NewUser)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat4(User)
}
