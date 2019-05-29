package example.repository

import example.domain.{NewUser, User}

/**
  * Manages operations with User table in database.
  * @tparam F specific type of return data.
  */
trait UserRepository[F[_]] {
  def registerUser(user: NewUser): F[User] = registerUser(user.username, user.address, user.email)
  def registerUser(username: String, address: Option[String], email: String): F[User]
  def getById(id: Long): F[Option[User]]
  def getByUsername(username: String): F[Option[User]]
  def getAll: F[Seq[User]]
}
