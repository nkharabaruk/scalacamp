package example.service

import example.repository.{IotDeviceRepositoryId, UserRepositoryId}
import org.scalatest.{FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

/**
  * Integration level testing of the IOT Device Service Id.
  */
class IotDeviceServiceIdTest extends FlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val iotDeviceRepository = new IotDeviceRepositoryId(db)
  db.run(iotDeviceRepository.iotDevices.schema.create)
  private val userRepository = new UserRepositoryId(db)
  db.run(userRepository.users.schema.create)
  private val iotDeviceService = new IotDeviceServiceId(iotDeviceRepository, userRepository)
  private val userService = new UserServiceId(userRepository)

  private val username = "John Smith"
  private val address = Option("Philadelphia, PA 19101")
  private val email = "john_smith@gmail.com"
  private val userId = 1
  private val iotDeviceId = 1
  private val serialNumber = "EA2700"

  "Register iot device with not existed user" should "return error message" in {
    val nonRegisteredDevice = iotDeviceService.registerDevice(userId, serialNumber)
    nonRegisteredDevice.isLeft shouldEqual true
    nonRegisteredDevice shouldEqual Left(s"User with id $userId not found.")
  }

  "Register iot device" should "return valid result" in {
    val registeredUser = userService.registerUser(username, address, email)
    registeredUser.isRight shouldEqual true
    val existingUser = registeredUser.right.get
    existingUser.id shouldEqual userId
    existingUser.username shouldEqual username
    existingUser.address shouldEqual address
    existingUser.email shouldEqual email

    val registeredDevice = iotDeviceService.registerDevice(userId, serialNumber)
    registeredDevice.isRight shouldEqual true
    val existingDevice = registeredDevice.right.get
    existingDevice.id shouldEqual iotDeviceId
    existingDevice.sn shouldEqual serialNumber
    existingDevice.userId shouldEqual userId
  }

  "Register iot device with already existed serial number" should "return error message" in {
    val nonRegisteredDevice1 = iotDeviceService.registerDevice(userId, serialNumber)
    nonRegisteredDevice1.isLeft shouldEqual true
    nonRegisteredDevice1 shouldEqual Left(s"Serial number $serialNumber already exists.")
  }

  "Register iot device with already existed serial number and not existed user" should "return error message" in {
    val nonRegisteredDevice2 = iotDeviceService.registerDevice(2, serialNumber)
    nonRegisteredDevice2.isLeft shouldEqual true
    nonRegisteredDevice2 shouldEqual Left(s"Serial number $serialNumber already exists.")
  }
}
