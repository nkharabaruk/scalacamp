package example.rest

import akka.http.scaladsl.server.Directives._
import example.domain.NewUser
import example.repository.UserRepositoryFuture

class UserRoutes(userRepository: UserRepositoryFuture) extends JsonSupport {

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
