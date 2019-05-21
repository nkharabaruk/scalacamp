package example.service

import example.repository.UserRepository
import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._

/**
  * Future implementation of the User Service.
  * @param repository the repository of the user.
  * @param ec the scope of execution context used by the service.
  */
class UserServiceFuture(repository: UserRepository[Future])
                       (implicit ec: ExecutionContext) extends UserService[Future](repository)
