package example

import org.scalatest.{FlatSpec, Matchers}
import cats.implicits.catsStdInstancesForFuture
import cats.Id
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

class ServiceTest extends FlatSpec with Matchers {

  "The user repository id method calls" should "return valid result" in {
    val userRepositoryId = new UserRepositoryId()

    val johnUsername = "John Smith"
    val johnId = 1

    val registeredUser = userRepositoryId.registerUser(johnUsername)
    registeredUser.id shouldEqual johnId
    registeredUser.username shouldEqual johnUsername

    val retrievedByUsername = userRepositoryId.getByUsername(johnUsername).get
    retrievedByUsername.id shouldEqual johnId
    retrievedByUsername.username shouldEqual johnUsername

    val retrievedById = userRepositoryId.getById(johnId).get
    retrievedById.id shouldEqual johnId
    retrievedById.username shouldEqual johnUsername

    val breadUsername = "Bread Pitt"
    val breadId = 2
    userRepositoryId.registerUser(breadUsername).id shouldEqual breadId

    userRepositoryId.getById(3) shouldBe None
    userRepositoryId.getByUsername("Random user name") shouldBe None

    val john2Username = "John Smith"
    val john2Id = 3

    userRepositoryId.registerUser(john2Username).id shouldEqual john2Id
  }

  "The user repository future method calls" should "return valid result" in {
    val userRepositoryFuture = new UserRepositoryFuture()

    val johnUsername = "John Smith"
    val johnId = 1

    val registeredUser = Await.result(userRepositoryFuture.registerUser(johnUsername), 2.seconds)
    registeredUser.id shouldEqual johnId
    registeredUser.username shouldEqual johnUsername

    val retrievedByUsername = Await.result(userRepositoryFuture.getByUsername(johnUsername), 2.seconds).get
    retrievedByUsername.id shouldEqual johnId
    retrievedByUsername.username shouldEqual johnUsername

    val retrievedById = Await.result(userRepositoryFuture.getById(johnId), 2.seconds).get
    retrievedById.id shouldEqual johnId
    retrievedById.username shouldEqual johnUsername

    val breadUsername = "Bread Pitt"
    val breadId = 2
    Await.result(userRepositoryFuture.registerUser(breadUsername), 2.seconds).id shouldEqual breadId

    Await.result(userRepositoryFuture.getById(3), 2.seconds) shouldBe None
    Await.result(userRepositoryFuture.getByUsername("Random user name"), 2.seconds) shouldBe None

    val john2Username = "John Smith"
    val john2Id = 3

    Await.result(userRepositoryFuture.registerUser(john2Username), 2.seconds).id shouldEqual john2Id
  }

  "The iot device repository id method calls" should "return valid result" in {
    val iotDeviceRepositoryId = new IotDeviceRepositoryId()

    val userId = 1
    val serialNumber = "EA2700"
    val iotDeviceId = 1

    val iotDevice = iotDeviceRepositoryId.registerDevice(userId, serialNumber)
    iotDevice.id shouldEqual iotDeviceId
    iotDevice.sn shouldEqual serialNumber
    iotDevice.userId shouldEqual userId

    val retrievedById = iotDeviceRepositoryId.getById(iotDeviceId).get
    retrievedById.id shouldEqual iotDeviceId
    retrievedById.sn shouldEqual serialNumber
    retrievedById.userId shouldEqual userId

    val retrievedBySerialNumber = iotDeviceRepositoryId.getBySn(serialNumber).get
    retrievedBySerialNumber.id shouldEqual iotDeviceId
    retrievedBySerialNumber.sn shouldEqual serialNumber
    retrievedBySerialNumber.userId shouldEqual userId

    val retrievedByUser = iotDeviceRepositoryId.getByUser(userId).head
    retrievedByUser.id shouldEqual iotDeviceId
    retrievedByUser.sn shouldEqual serialNumber
    retrievedByUser.userId shouldEqual userId

    iotDeviceRepositoryId.getById(2) shouldEqual None
    iotDeviceRepositoryId.getBySn("Random serial number") shouldEqual None
    iotDeviceRepositoryId.getByUser(2) shouldEqual List.empty

    val anotherSN = "BB0012"
    val anotherIotDeviceId = 2
    val anotherIotDevice = iotDeviceRepositoryId.registerDevice(userId,anotherSN)
    anotherIotDevice.id shouldEqual anotherIotDeviceId

    val allRetrievedByUser = iotDeviceRepositoryId.getByUser(userId)
    allRetrievedByUser.size shouldEqual 2
    allRetrievedByUser.head shouldEqual iotDevice
    allRetrievedByUser.tail.head shouldEqual anotherIotDevice

    val oneMoreIotDeviceId = 3
    // iot device with same user and serial number can be registered
    iotDeviceRepositoryId.registerDevice(userId, serialNumber).id shouldEqual oneMoreIotDeviceId
  }

  "The iot device repository future method calls" should "return valid result" in {
    val iotDeviceRepositoryFuture = new IotDeviceRepositoryFuture()

    val userId = 1
    val serialNumber = "EA2700"
    val iotDeviceId = 1

    val iotDevice = Await.result(iotDeviceRepositoryFuture.registerDevice(userId, serialNumber), 2.seconds)
    iotDevice.id shouldEqual iotDeviceId
    iotDevice.sn shouldEqual serialNumber
    iotDevice.userId shouldEqual userId

    val retrievedById = Await.result(iotDeviceRepositoryFuture.getById(iotDeviceId), 2.seconds).get
    retrievedById.id shouldEqual iotDeviceId
    retrievedById.sn shouldEqual serialNumber
    retrievedById.userId shouldEqual userId

    val retrievedBySerialNumber = Await.result(iotDeviceRepositoryFuture.getBySn(serialNumber), 2.seconds).get
    retrievedBySerialNumber.id shouldEqual iotDeviceId
    retrievedBySerialNumber.sn shouldEqual serialNumber
    retrievedBySerialNumber.userId shouldEqual userId

    val retrievedByUser = Await.result(iotDeviceRepositoryFuture.getByUser(userId), 2.seconds).head
    retrievedByUser.id shouldEqual iotDeviceId
    retrievedByUser.sn shouldEqual serialNumber
    retrievedByUser.userId shouldEqual userId

    Await.result(iotDeviceRepositoryFuture.getById(2), 2.seconds) shouldEqual None
    Await.result(iotDeviceRepositoryFuture.getBySn("Random serial number"), 2.seconds) shouldEqual None
    Await.result(iotDeviceRepositoryFuture.getByUser(2), 2.seconds) shouldEqual List.empty

    val anotherSN = "BB0012"
    val anotherIotDeviceId = 2
    val anotherIotDevice = Await.result(iotDeviceRepositoryFuture.registerDevice(userId,anotherSN), 2.seconds)
    anotherIotDevice.id shouldEqual anotherIotDeviceId

    val allRetrievedByUser = Await.result(iotDeviceRepositoryFuture.getByUser(userId), 2.seconds)
    allRetrievedByUser.size shouldEqual 2
    allRetrievedByUser.head shouldEqual iotDevice
    allRetrievedByUser.tail.head shouldEqual anotherIotDevice

    val oneMoreIotDeviceId = 3
    // iot device with same user and serial number can be registered
    Await.result(iotDeviceRepositoryFuture.registerDevice(userId, serialNumber), 2.seconds).id shouldEqual oneMoreIotDeviceId
  }

  "The user service id method calls" should "return valid result" in {
    val userRepositoryId = new UserRepositoryId()
    val userService = new UserService[Id](userRepositoryId)

    val username = "John Smith"
    val userId = 1

    val registeredUser = userService.registerUser(username)
    registeredUser.isRight shouldEqual true
    val existingUser = registeredUser.right.get
    existingUser.id shouldEqual userId
    existingUser.username shouldEqual username

    val nonRegisteredUser = userService.registerUser(username)
    nonRegisteredUser.isLeft shouldEqual true
    nonRegisteredUser shouldEqual Left(s"example.User $existingUser already exists.")

    val retrievedByUsername = userService.getByUsername(username).get
    retrievedByUsername.id shouldEqual userId
    retrievedByUsername.username shouldEqual username

    val retrievedById = userService.getById(userId).get
    retrievedById.id shouldEqual userId
    retrievedById.username shouldEqual username

    userService.getByUsername("Random user name") shouldEqual None
    userService.getById(2) shouldEqual None
  }

  "The user service future method calls" should "return valid result" in {
    val userRepositoryFuture = new UserRepositoryFuture()
    val userService = new UserService[Future](userRepositoryFuture)

    val username = "John Smith"
    val userId = 1

    val registeredUser = Await.result(userService.registerUser(username), 2.seconds)
    registeredUser.isRight shouldEqual true
    val existingUser = registeredUser.right.get
    existingUser.id shouldEqual userId
    existingUser.username shouldEqual username

    val nonRegisteredUser = Await.result(userService.registerUser(username), 2.seconds)
    nonRegisteredUser.isLeft shouldEqual true
    nonRegisteredUser shouldEqual Left(s"example.User $existingUser already exists.")

    val retrievedByUsername = Await.result(userService.getByUsername(username), 2.seconds).get
    retrievedByUsername.id shouldEqual userId
    retrievedByUsername.username shouldEqual username

    val retrievedById = Await.result(userService.getById(userId), 2.seconds).get
    retrievedById.id shouldEqual userId
    retrievedById.username shouldEqual username

    Await.result(userService.getByUsername("Random user name"), 2.seconds) shouldEqual None
    Await.result(userService.getById(2), 2.seconds) shouldEqual None
  }

  "The iot device service id method calls" should "return valid result" in {
    val iotDeviceRepositoryId = new IotDeviceRepositoryId()
    val userRepositoryId = new UserRepositoryId()
    val iotDeviceService = new IotDeviceService[Id](iotDeviceRepositoryId, userRepositoryId)
    val userService = new UserService[Id](userRepositoryId)

    val username = "John Smith"
    val userId = 1
    val iotDeviceId = 1
    val serialNumber = "EA2700"

    val nonRegisteredDevice = iotDeviceService.registerDevice(userId, serialNumber)
    nonRegisteredDevice.isLeft shouldEqual true
    nonRegisteredDevice shouldEqual Left(s"User with id $userId not found.")

    val registeredUser = userService.registerUser(username)
    registeredUser.isRight shouldEqual true
    val existingUser = registeredUser.right.get
    existingUser.id shouldEqual userId
    existingUser.username shouldEqual username

    val registeredDevice = iotDeviceService.registerDevice(userId, serialNumber)
    registeredDevice.isRight shouldEqual true
    val existingDevice = registeredDevice.right.get
    existingDevice.id shouldEqual iotDeviceId
    existingDevice.sn shouldEqual serialNumber
    existingDevice.userId shouldEqual userId

    val nonRegisteredDevice1 = iotDeviceService.registerDevice(userId, serialNumber)
    nonRegisteredDevice1.isLeft shouldEqual true
    nonRegisteredDevice1 shouldEqual Left(s"Serial number $serialNumber already exists.")

    val nonRegisteredDevice2 = iotDeviceService.registerDevice(2, serialNumber)
    nonRegisteredDevice2.isLeft shouldEqual true
    nonRegisteredDevice2 shouldEqual Left(s"Serial number $serialNumber already exists.")
  }

  "The iot device service future method calls" should "return valid result" in {
    val iotDeviceRepositoryFuture = new IotDeviceRepositoryFuture()
    val userRepositoryFuture = new UserRepositoryFuture()
    val iotDeviceService = new IotDeviceService[Future](iotDeviceRepositoryFuture, userRepositoryFuture)
    val userService = new UserService[Future](userRepositoryFuture)

    val username = "John Smith"
    val userId = 1
    val iotDeviceId = 1
    val serialNumber = "EA2700"

    val nonRegisteredDevice = Await.result(iotDeviceService.registerDevice(userId, serialNumber), 2.seconds)
    nonRegisteredDevice.isLeft shouldEqual true
    nonRegisteredDevice shouldEqual Left(s"User with id $userId not found.")

    val registeredUser = Await.result(userService.registerUser(username), 2.seconds)
    registeredUser.isRight shouldEqual true
    val existingUser = registeredUser.right.get
    existingUser.id shouldEqual userId
    existingUser.username shouldEqual username

    val registeredDevice = Await.result(iotDeviceService.registerDevice(userId, serialNumber), 2.seconds)
    registeredDevice.isRight shouldEqual true
    val existingDevice = registeredDevice.right.get
    existingDevice.id shouldEqual iotDeviceId
    existingDevice.sn shouldEqual serialNumber
    existingDevice.userId shouldEqual userId

    val nonRegisteredDevice1 = Await.result(iotDeviceService.registerDevice(userId, serialNumber), 2.seconds)
    nonRegisteredDevice1.isLeft shouldEqual true
    nonRegisteredDevice1 shouldEqual Left(s"Serial number $serialNumber already exists.")

    val nonRegisteredDevice2 = Await.result(iotDeviceService.registerDevice(2, serialNumber), 2.seconds)
    nonRegisteredDevice2.isLeft shouldEqual true
    nonRegisteredDevice2 shouldEqual Left(s"Serial number $serialNumber already exists.")
  }
}
