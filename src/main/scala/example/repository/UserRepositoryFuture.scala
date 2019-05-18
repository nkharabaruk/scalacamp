package example.repository

import java.util.concurrent.atomic.AtomicLong
import example.domain.User
import example.schema.UserTable
import scala.concurrent.{Await, ExecutionContext, Future}
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

class UserRepositoryFuture(db: Database)(implicit val ec: ExecutionContext) extends UserRepository[Future] {

  lazy val users = TableQuery[UserTable]

  private val id = new AtomicLong(0)

  override def registerUser(username: String, address: Option[String], email: String): Future[User] = Future {
    val userId = id.incrementAndGet()
    val user = User(userId, username, address, email)
    db.run(users.insertOrUpdate(user))
    user
  }
  override def getById(id: Long): Future[Option[User]] = db.run(users.filter(_.id === id).result.headOption)
  override def getAll: Future[Seq[User]] = db.run(users.result)
  override def getByUsername(username: String): Future[Option[User]] = db.run(users.filter(_.username === username).result.headOption)
}
