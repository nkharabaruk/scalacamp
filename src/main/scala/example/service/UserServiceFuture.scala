package example.service

import example.repository.UserRepository
import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._

class UserServiceFuture(repository: UserRepository[Future])
                       (implicit ec: ExecutionContext) extends UserService[Future](repository)
