package example.repository

import java.util.concurrent.atomic.AtomicLong
import cats.Id
import example.domain.User

class UserRepositoryId extends UserRepository[Id] {
  private var storage: Map[Long, User] = Map()
  private val id = new AtomicLong(0)

  override def registerUser(username: String, address: Option[String], email: String): Id[User] = {
    val userId = id.incrementAndGet()
    val user = User(userId, username, address, email)
    storage = storage + (userId -> user)
    user
  }
  override def getById(id: Long): Id[Option[User]] = storage.get(id)
  override def getByUsername(username: String): Id[Option[User]] = storage.values.find(user => user.username == username)
}
