package example.repository

import java.util.concurrent.atomic.AtomicLong
import example.domain.User
import scala.concurrent.{ExecutionContext, Future}

class UserRepositoryFuture(implicit val ec: ExecutionContext) extends UserRepository[Future] {
  private var storage: Map[Long, User] = Map()
  private val id = new AtomicLong(0)

  override def registerUser(username: String): Future[User] = Future {
    val userId = id.incrementAndGet()
    val user = User(userId, username)
    storage = storage + (userId -> user)
    user
  }
  override def getById(id: Long): Future[Option[User]] = Future {storage.get(id)}
  override def getByUsername(username: String): Future[Option[User]] = Future {storage.values.find(user => user.username == username)}
}
