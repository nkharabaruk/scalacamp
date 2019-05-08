package example

import cats.{Id, Monad}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Repository and Service implementation using tagless final pattern.
  * The idea is to make it easier to test our database layer, using Scala’s higher kinded types to abstract
  * the Future type constructor away from our traits under test.
  * Intro to tagless final: https://www.basementcrowd.com/2019/01/17/an-introduction-to-tagless-final-in-scala/.
  * The similar task example https://github.com/LvivScalaClub/cats-playground/blob/master/src/main/scala/BookRepository.scala
  */

case class User(id: Long, username: String)
case class IotDevice(id: Long, userId: Long, sn: String)

// NOTE: This import bring into the scope implicits that allow you to call .map and .flatMap on the type F[_]
// and also bring you typeclasses that know how to flatmap (Monad) and map (Functor) over your higher-kinded type.
import cats.implicits._

trait UserRepository[F[_]] {
  def registerUser(username: String): F[User]
  def getById(id: Long): F[Option[User]]
  def getByUsername(username: String): F[Option[User]]
}

class UserRepositoryId extends UserRepository[Id] {
  private var storage: Map[Long, User] = Map()

  override def registerUser(username: String): Id[User] = {
    val existing = getByUsername(username)
    existing.getOrElse {
      val id = if(storage.nonEmpty) {storage.keys.last + 1} else 1
      val user = User(id, username)
      storage = storage + (id -> user)
      user
    }
  }
  override def getById(id: Long): Id[Option[User]] = storage.get(id)
  override def getByUsername(username: String): Id[Option[User]] = storage.values.find(user => user.username == username)
}

class UserRepositoryFuture extends UserRepository[Future] {
  private var storage: Map[Long, User] = Map()

  override def registerUser(username: String): Future[User] =
    getByUsername(username).map(existing => existing.getOrElse {
      val id = if(storage.nonEmpty) {storage.keys.last + 1} else 1
      val user = User(id, username)
      storage = storage + (id -> user)
      user
    })
  override def getById(id: Long): Future[Option[User]] = Future.successful{storage.get(id)}
  override def getByUsername(username: String): Future[Option[User]] = Future.successful{storage.values.find(user => user.username == username)}
}

trait IotDeviceRepository[F[_]] {
  def registerDevice(userId: Long, serialNumber: String): F[IotDevice]
  def getById(id: Long): F[Option[IotDevice]]
  def getBySn(sn: String): F[Option[IotDevice]]
  def getByUser(userId: Long): F[Seq[IotDevice]]
}

class IotDeviceRepositoryId extends IotDeviceRepository[Id] {
  private var storage: Map[Long, IotDevice] = Map()

  override def registerDevice(userId: Long, serialNumber: String): Id[IotDevice] = {
    val id = if(storage.nonEmpty) {storage.keys.last + 1} else 1
    val iotDevice = IotDevice(id, userId, serialNumber)
    storage = storage + (id -> iotDevice)
    iotDevice
  }
  override def getById(id: Long): Id[Option[IotDevice]] = storage.get(id)
  override def getBySn(sn: String): Id[Option[IotDevice]] = storage.values.find(device => device.sn == sn)
  override def getByUser(userId: Long): Id[Seq[IotDevice]] = storage.values.filter(device => device.userId == userId).toSeq
}

class IotDeviceRepositoryFuture extends IotDeviceRepository[Future] {
  private var storage: Map[Long, IotDevice] = Map()

  override def registerDevice(userId: Long, serialNumber: String): Future[IotDevice] = Future.successful{
    val id = if(storage.nonEmpty) {storage.keys.last + 1} else 1
    val iotDevice = IotDevice(id, userId, serialNumber)
    storage = storage + (id -> iotDevice)
    iotDevice
  }
  override def getById(id: Long): Future[Option[IotDevice]] = Future.successful{storage.get(id)}
  override def getBySn(sn: String): Future[Option[IotDevice]] = Future.successful{storage.values.find(device => device.sn == sn)}
  override def getByUser(userId: Long): Future[Seq[IotDevice]] = Future.successful{storage.values.filter(device => device.userId == userId).toSeq}
}

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

class IotDeviceService[F[_]](repository: IotDeviceRepository[F],
                             userRepository: UserRepository[F])
                            (implicit monad: Monad[F]) {
  // the register should fail with Left if the user doesn't exist or the sn already exists.
  def registerDevice(userId: Long, sn: String): F[Either[String, IotDevice]] = {
//    userRepository.getById(userId)
//      .flatMap(user => repository.getBySn(sn)
//        .flatMap(device =>
//          if (user.isDefined && device.isEmpty)
//            repository.registerDevice(userId, sn).map(Right(_))
//          else
//            monad.pure(Left("User doesn't exist or device serial number is already present."))))

        for {
          userOption <- userRepository.getById(userId)
          deviceOption <- repository.getBySn(sn)
        } yield {
          (if (userOption.isDefined && deviceOption.isEmpty)
            repository.registerDevice(userId, sn).map(Right(_))
          else
            monad.pure(Left("User doesn't exist or device serial number is already present.")))
            .asInstanceOf[Either[String, IotDevice]]
        }
  }
}

// task1: implement in-memory Repository with Id monad.
// task2: implement in-memory Repository with Future monad
// example https://github.com/LvivScalaClub/cats-playground/blob/master/src/main/scala/BookRepository.scala
// task3: unit tests