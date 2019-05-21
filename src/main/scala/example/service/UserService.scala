package example.service

import cats.Monad
import cats.implicits._
import example.domain.User
import example.repository.UserRepository
import example.validation.Validator._

/**
  * Manages operations with User repository adding additional business logic.
  * @param repository the repository of the user.
  * @param monad allows composition of dependent effectful functions
  * @tparam F specific type of return data.
  */
class UserService[F[_]](repository: UserRepository[F])
                       (implicit monad: Monad[F]) {
  def registerUser(username: String, address: Option[String], email: String): F[Either[String, User]] = {
    val errorMessage = validateFields(username, address, email)
    if (errorMessage.nonEmpty) { return monad.pure(Left(errorMessage)) }
    repository.getByUsername(username).flatMap({
      case Some(user) =>
        monad.pure(Left(s"example.User $user already exists."))
      case None =>
        repository.registerUser(username, address, email).map(Right(_))
    })
  }
  def getByUsername(username: String): F[Option[User]] = repository.getByUsername(username)
  def getById(id: Long): F[Option[User]] = repository.getById(id)
  def getAll: F[Seq[User]] = repository.getAll

  private def validateFields(username: String, address: Option[String], email: String): String = {
    var errorMessage = ""
    if ((username validate).isLeft) { errorMessage = (username validate).left.get }
    if (address.isDefined && (address.get validate).isLeft) { errorMessage = (address.get validate).left.get }
    if ((email validate).isLeft) { errorMessage = (email validate).left.get }
    errorMessage
  }
}
