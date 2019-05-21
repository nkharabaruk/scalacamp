package example.rest

import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import example.domain.User
import example.repository.UserRepositoryFuture
import example.service.UserServiceFuture
import org.scalatest.{Matchers, WordSpec}
import slick.jdbc.H2Profile.api._
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration._


class UserRoutesTest extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  val db = Database.forConfig("scalacamp")
  val userRepository = new UserRepositoryFuture(db)
  Await.result(db.run(userRepository.users.schema.create), 1.second)
  val user1 = User(1, "Homer Simpson", Option("Springfield"), "h_simpson@email.com")
  userRepository.registerUser(user1.username, user1.address, user1.email)
  val user2 = User(2, "Rick Sanchez", Option.empty, "rick_sanchez@email.com")
  val userRoutes = new UserRoutes(new UserServiceFuture(userRepository)).routes

  val userJson: JsValue = user2.toJson


  "The service" should {
    "return predefined users" in {
      Get("/users") ~> userRoutes ~> check {
        responseAs[String] shouldEqual "[{\"address\":\"Springfield\",\"email\":\"h_simpson@email.com\",\"id\":1,\"username\":\"Homer Simpson\"}]"
      }
    }
  }

  "The service" should {
    "return first user" in {
      Get("/users?id=1") ~> userRoutes ~> check {
        responseAs[String] shouldEqual "{\"address\":\"Springfield\",\"email\":\"h_simpson@email.com\",\"id\":1,\"username\":\"Homer Simpson\"}"
      }
    }
  }

  "The service" should {
    "create new user" in {
      Post("/users").withEntity(ContentTypes.`application/json`, userJson.toString) ~> userRoutes ~> check {
        responseAs[String] shouldEqual "{\"email\":\"rick_sanchez@email.com\",\"id\":2,\"username\":\"Rick Sanchez\"}"
      }
    }
  }
}
