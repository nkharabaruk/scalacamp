package example

import org.scalatest.{AsyncFlatSpec, Matchers}
import cats.implicits.catsStdInstancesForFuture
import cats.Id
import scala.concurrent.Future

class ServiceTest extends AsyncFlatSpec with Matchers {

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

    userRepositoryFuture.registerUser(johnUsername).map { registeredUser =>
      registeredUser.id shouldEqual johnId
      registeredUser.username shouldEqual johnUsername
    }
    userRepositoryFuture.getByUsername(johnUsername).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual johnId
      retrievedByUsername.get.username shouldEqual johnUsername
    }

    userRepositoryFuture.getById(johnId).map { retrievedById =>
      retrievedById.get.id shouldEqual johnId
      retrievedById.get.username shouldEqual johnUsername
    }
    userRepositoryFuture.registerUser("Bread Pitt").map { registeredUser =>
      registeredUser.id shouldEqual 2
    }
    userRepositoryFuture.getById(3).map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
    userRepositoryFuture.getByUsername("Random user name").map { nonRetrievedUser =>
      nonRetrievedUser shouldBe None
    }
    userRepositoryFuture.registerUser("John Smith").map { registredWithSameName =>
      registredWithSameName.id shouldEqual 3
    }
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

    iotDeviceRepositoryFuture.registerDevice(userId, serialNumber).map { iotDevice =>
      iotDevice.id shouldEqual iotDeviceId
      iotDevice.sn shouldEqual serialNumber
      iotDevice.userId shouldEqual userId
    }
    iotDeviceRepositoryFuture.getById(iotDeviceId).map { retrievedById =>
      retrievedById.get.id shouldEqual iotDeviceId
      retrievedById.get.sn shouldEqual serialNumber
      retrievedById.get.userId shouldEqual userId
    }
    iotDeviceRepositoryFuture.getBySn(serialNumber).map { retrievedBySerialNumber =>
      retrievedBySerialNumber.get.id shouldEqual iotDeviceId
      retrievedBySerialNumber.get.sn shouldEqual serialNumber
      retrievedBySerialNumber.get.userId shouldEqual userId
    }
    iotDeviceRepositoryFuture.getByUser(userId).map { retrievedByUser =>
      retrievedByUser.head.id shouldEqual iotDeviceId
      retrievedByUser.head.sn shouldEqual serialNumber
      retrievedByUser.head.userId shouldEqual userId
    }
    iotDeviceRepositoryFuture.getById(2).map { nonRegisteredIotDevice =>
     nonRegisteredIotDevice shouldEqual None
    }
    iotDeviceRepositoryFuture.getBySn("Random serial number").map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual None
    }
    iotDeviceRepositoryFuture.getByUser(2).map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual List.empty
    }

    val anotherSerialNumber = "BB0012"
    val anotherIotDeviceId = 2
    iotDeviceRepositoryFuture.registerDevice(userId,anotherSerialNumber).map { anotherIotDevice =>
      anotherIotDevice.id shouldEqual anotherIotDeviceId
    }
    iotDeviceRepositoryFuture.getByUser(userId).map { allRetrievedByUser =>
      allRetrievedByUser.size shouldEqual 2
      allRetrievedByUser.head shouldEqual IotDevice(iotDeviceId, userId, serialNumber)
      allRetrievedByUser.tail.head shouldEqual IotDevice(anotherIotDeviceId, userId, anotherSerialNumber)
    }
    iotDeviceRepositoryFuture.registerDevice(userId, serialNumber).map { oneMoreIotDevice =>
      oneMoreIotDevice.id shouldEqual 3
    }
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

    userService.registerUser(username).map { registeredUser =>
      registeredUser.isRight shouldEqual true
      val existingUser = registeredUser.right.get
      existingUser.id shouldEqual userId
      existingUser.username shouldEqual username
    }
    userService.registerUser(username).map { nonRegisteredUser =>
      nonRegisteredUser.isLeft shouldEqual true
      nonRegisteredUser shouldEqual Left(s"example.User ${User(userId, username)} already exists.")
    }
    userService.getByUsername(username).map { retrievedByUsername =>
      retrievedByUsername.get.id shouldEqual userId
      retrievedByUsername.get.username shouldEqual username
    }
    userService.getById(userId).map { retrievedById =>
      retrievedById.get.id shouldEqual userId
      retrievedById.get.username shouldEqual username
    }

    userService.getByUsername("Random user name").map { nonRegisteredUser =>
      nonRegisteredUser shouldEqual None
    }
    userService.getById(2).map { nonRegisteredUser =>
      nonRegisteredUser shouldEqual None
    }
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

    iotDeviceService.registerDevice(userId, serialNumber).map { nonRegisteredDevice =>
      nonRegisteredDevice.isLeft shouldEqual true
      nonRegisteredDevice shouldEqual Left(s"User with id $userId not found.")
    }
    userService.registerUser(username).map { registeredUser =>
      registeredUser.isRight shouldEqual true
      val existingUser = registeredUser.right.get
      existingUser.id shouldEqual userId
      existingUser.username shouldEqual username
    }
    iotDeviceService.registerDevice(userId, serialNumber).map { registeredDevice =>
      registeredDevice.isRight shouldEqual true
      val existingDevice = registeredDevice.right.get
      existingDevice.id shouldEqual iotDeviceId
      existingDevice.sn shouldEqual serialNumber
      existingDevice.userId shouldEqual userId
    }
    iotDeviceService.registerDevice(userId, serialNumber).map { nonRegisteredDevice =>
      nonRegisteredDevice.isLeft shouldEqual true
      nonRegisteredDevice shouldEqual Left(s"Serial number $serialNumber already exists.")
    }
    iotDeviceService.registerDevice(2, serialNumber).map { nonRegisteredDevice =>
      nonRegisteredDevice.isLeft shouldEqual true
      nonRegisteredDevice shouldEqual Left("User with id 2 not found.")
    }
  }
}
