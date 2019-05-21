package example.rest

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import example.domain.{NewUser, User}
import example.service.UserServiceFuture
import example.utils.Retrier

import scala.concurrent.duration._

class UserRoutes(userService: UserServiceFuture) extends JsonSupport {

  implicit def myExceptionHandler: ExceptionHandler =
    ExceptionHandler {
      case e: Throwable =>
        println(e.getMessage)
        println(e.getStackTrace)
        complete(HttpResponse(StatusCodes.BadRequest, entity = e.getMessage))
    }

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
        }
      //        } ~
      //        get {
      //          complete(userService.getAll)
      //        }
    }
}
