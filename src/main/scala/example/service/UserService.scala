package example.service

import cats.Monad
import example.domain.User
import example.repository.UserRepository
import cats.implicits._

class UserService[F[_]](repository: UserRepository[F])
                       (implicit monad: Monad[F]) {

  def registerUser(username: String, address: Option[String], email: String): F[Either[String, User]] = {
    repository.getByUsername(username).flatMap({
      case Some(user) =>
        monad.pure(Left(s"example.User $user already exists."))
      case None =>
        repository.registerUser(username, address, email).map(Right(_))
    })
  }
  def getByUsername(username: String): F[Option[User]] = repository.getByUsername(username)
  def getById(id: Long): F[Option[User]] = repository.getById(id)
}
