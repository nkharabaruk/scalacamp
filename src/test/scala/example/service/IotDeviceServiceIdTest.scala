package example.service

import example.repository.{IotDeviceRepositoryId, UserRepositoryId}
import org.scalatest.{FlatSpec, Matchers}

class IotDeviceServiceIdTest extends FlatSpec with Matchers {

  val iotDeviceRepositoryId = new IotDeviceRepositoryId()
  val userRepositoryId = new UserRepositoryId()
  val iotDeviceService = new IotDeviceServiceId(iotDeviceRepositoryId, userRepositoryId)
  val userService = new UserServiceId(userRepositoryId)

  val username = "John Smith"
  val userId = 1
  val iotDeviceId = 1
  val serialNumber = "EA2700"

  "Register iot device with not existed user" should "return error message" in {
    val nonRegisteredDevice = iotDeviceService.registerDevice(userId, serialNumber)
    nonRegisteredDevice.isLeft shouldEqual true
    nonRegisteredDevice shouldEqual Left(s"User with id $userId not found.")
  }

  "Register iot device" should "return valid result" in {
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
