package example.service

import example.repository.{IotDeviceRepositoryFuture, UserRepositoryFuture}
import org.scalatest.{AsyncFlatSpec, Matchers}

class IotDeviceServiceFutureTest extends AsyncFlatSpec with Matchers {

  val iotDeviceRepositoryFuture = new IotDeviceRepositoryFuture()
  val userRepositoryFuture = new UserRepositoryFuture()
  val iotDeviceService = new IotDeviceServiceFuture(iotDeviceRepositoryFuture, userRepositoryFuture)
  val userService = new UserServiceFuture(userRepositoryFuture)

  val username = "John Smith"
  val userId = 1
  val iotDeviceId = 1
  val serialNumber = "EA2700"

  "Register iot device with not existed user" should "return error message" in {
    iotDeviceService.registerDevice(userId, serialNumber).map { nonRegisteredDevice =>
      nonRegisteredDevice.isLeft shouldEqual true
      nonRegisteredDevice shouldEqual Left(s"User with id $userId not found.")
    }
  }

  "Register iot device" should "return valid result" in {
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
  }

  "Register iot device with already existed serial number" should "return error message" in {
    iotDeviceService.registerDevice(userId, serialNumber).map { nonRegisteredDevice =>
      nonRegisteredDevice.isLeft shouldEqual true
      nonRegisteredDevice shouldEqual Left(s"Serial number $serialNumber already exists.")
    }
  }

  "Register iot device with already existed serial number and not existed user" should "return error message" in {
    iotDeviceService.registerDevice(2, serialNumber).map { nonRegisteredDevice =>
      nonRegisteredDevice.isLeft shouldEqual true
      nonRegisteredDevice shouldEqual Left(s"Serial number $serialNumber already exists.")
    }
  }
}