package example.rest

import akka.actor.ActorSystem
import akka.http.scaladsl.model.ContentTypes
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import example.domain.User
import example.repository.UserRepositoryFuture
import example.service.UserServiceFuture
import org.scalatest.{Matchers, WordSpec}
import slick.jdbc.H2Profile.api._
import spray.json._
import scala.concurrent.Await
import scala.concurrent.duration._

class UserRoutesTest extends WordSpec with Matchers with ScalatestRouteTest with JsonSupport {

  private val db = Database.forConfig("scalacamp")
  private val userRepository = new UserRepositoryFuture(db)
  Await.result(db.run(userRepository.users.schema.create), 1.second)
  private val user1 = User(1, "Homer Simpson", Option("Springfield"), "h_simpson@email.com")
  userRepository.registerUser(user1.username, user1.address, user1.email)
  private val user2 = User(2, "Rick Sanchez", Option.empty, "rick_sanchez@email.com")
  private val userService = new UserServiceFuture(userRepository)
  private val userRoutes = new UserRoutes(userService).routes
  private val userJson: JsValue = user2.toJson
  // for test with retry
  implicit def default(implicit system: ActorSystem) = RouteTestTimeout(10.seconds)

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

  "The service" should {
    "return message that the user already exists" in {
      Post("/users").withEntity(ContentTypes.`application/json`, userJson.toString) ~> userRoutes
      Post("/users").withEntity(ContentTypes.`application/json`, userJson.toString) ~> userRoutes ~> check {
        responseAs[String] shouldEqual s"example.User ${user2.toString} already exists."
      }
    }
  }

  "The service" should {
    "return message about incorrect fields" in {
      val emptyUserJson: JsValue = User(1, "", Option.empty, "").toJson
      Post("/users").withEntity(ContentTypes.`application/json`, emptyUserJson.toString) ~> userRoutes ~> check {
        responseAs[String] shouldEqual "Value should not be empty. "
      }
    }
  }
}
