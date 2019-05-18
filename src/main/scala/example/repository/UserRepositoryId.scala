package example.repository

import java.util.concurrent.atomic.AtomicLong
import scala.concurrent.duration._
import cats.Id
import example.domain.User
import example.schema.UserTable
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery
import scala.concurrent.Await

class UserRepositoryId(db: Database) extends UserRepository[Id] {

  lazy val users = TableQuery[UserTable]
  private var storage: Map[Long, User] = Map()
  private val id = new AtomicLong(0)

  override def registerUser(username: String, address: Option[String], email: String): Id[User] = {
    val userId = id.incrementAndGet()
    val user = User(userId, username, address, email)
    db.run(users.insertOrUpdate(user))
    user
  }
  override def getById(id: Long): Id[Option[User]] = {
    Await.result(db.run(users.filter(_.id === id).result.headOption), 2.seconds)
  }
  override def getAll: Id[Seq[User]] = {
    Await.result(db.run(users.result), 2.seconds)
  }
  override def getByUsername(username: String): Id[Option[User]] = {
    Await.result(db.run(users.filter(_.username === username).result.headOption), 2.seconds)
  }
}
