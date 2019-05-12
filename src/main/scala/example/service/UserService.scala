package example.service

import cats.Monad
import example.domain.User
import example.repository.UserRepository
import cats.implicits._

class UserService[F[_]](repository: UserRepository[F])
                       (implicit monad: Monad[F]) {

  def registerUser(username: String): F[Either[String, User]] = {
    // .flatMap syntax works because of import cats.implicits._
    // so flatMap function is added to F[_] through implicit conversions
    // The implicit monad param knows how to flatmap and map over your F.
    repository.getByUsername(username).flatMap({
      case Some(user) =>
        monad.pure(Left(s"example.User $user already exists."))
      case None =>
        // .map syntax works because of import cats.implicits._
        // so map function is added to F[_] through implicit conversions
        repository.registerUser(username).map(Right(_))
    })
  }
  def getByUsername(username: String): F[Option[User]] = repository.getByUsername(username)
  def getById(id: Long): F[Option[User]] = repository.getById(id)
}
