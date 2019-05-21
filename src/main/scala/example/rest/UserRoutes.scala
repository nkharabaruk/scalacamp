package example.rest

import akka.http.scaladsl.server.Directives._
import example.domain.{NewUser, User}
import example.service.UserServiceFuture
import example.utils.Retrier
import scala.concurrent.duration._

/**
  * Controller for routing between application endpoints.
  * @param userService injected User Service to use business logic operations from.
  */
class UserRoutes(userService: UserServiceFuture) extends JsonSupport {

  val routes =
    pathPrefix("users") {
      post {
        entity(as[NewUser]) { user =>
          complete(
            new Retrier().retry(
              () => userService.registerUser(user.username, user.address, user.email),
              (a: Either[String, User]) => a.isRight,
              List(1.second, 2.seconds, 5.seconds)
            )
          )
        }
      } ~
      get {
        parameters("id".as[Long]) { id =>
          complete {
            userService.getById(id)
          }
        }
      } ~
      get {
        complete(userService.getAll)
      }
    }
}
