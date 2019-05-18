package example.repository

import java.util.concurrent.atomic.AtomicLong
import cats.Id
import example.domain.User
import example.schema.UserTable
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

class UserRepositoryId(db: Database) extends UserRepository[Id] {

  private val users = TableQuery[UserTable]
  private var storage: Map[Long, User] = Map()
  private val id = new AtomicLong(0)

  override def registerUser(username: String, address: Option[String], email: String): Id[User] = {
    val userId = id.incrementAndGet()
    val user = User(userId, username, address, email)
    storage = storage + (userId -> user)
    user
  }
  override def getById(id: Long): Id[Option[User]] = storage.get(id)
  override def getAll: Id[Seq[User]] = storage.values.toList
  override def getByUsername(username: String): Id[Option[User]] = storage.values.find(user => user.username == username)
}
