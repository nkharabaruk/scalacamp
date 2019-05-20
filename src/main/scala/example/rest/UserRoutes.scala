package example.rest

import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import example.domain.NewUser
import example.repository.UserRepositoryFuture

class UserRoutes(userRepository: UserRepositoryFuture) extends JsonSupport {

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
          complete(userRepository.registerUser(user.username, user.address, user.email))
        }
      } ~
        get {
          parameters("id".as[Long]) { id =>
            complete {
              userRepository.getById(id)
            }
          }
        } ~
        get {
          complete(userRepository.getAll)
        }
    }
}
