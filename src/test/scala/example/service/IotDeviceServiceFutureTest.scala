package example.service

import example.repository.{IotDeviceRepositoryFuture, UserRepositoryFuture}
import org.scalatest.{AsyncFlatSpec, Matchers}

class IotDeviceServiceFutureTest extends AsyncFlatSpec with Matchers {

  private val iotDeviceRepositoryFuture = new IotDeviceRepositoryFuture()
  private val userRepositoryFuture = new UserRepositoryFuture()
  private val iotDeviceService = new IotDeviceServiceFuture(iotDeviceRepositoryFuture, userRepositoryFuture)
  private val userService = new UserServiceFuture(userRepositoryFuture)

  private val username = "John Smith"
  private val address = Option("Philadelphia, PA 19101")
  private val email = "john_smith@gmail.com"
  private val userId = 1
  private val iotDeviceId = 1
  private val serialNumber = "EA2700"

  "Register iot device with not existed user" should "return error message" in {
    iotDeviceService.registerDevice(userId, serialNumber).map { nonRegisteredDevice =>
      nonRegisteredDevice.isLeft shouldEqual true
      nonRegisteredDevice shouldEqual Left(s"User with id $userId not found.")
    }
  }

  "Register iot device" should "return valid result" in {
    userService.registerUser(username, address, email).map { registeredUser =>
      registeredUser.isRight shouldEqual true
      val existingUser = registeredUser.right.get
      existingUser.id shouldEqual userId
      existingUser.username shouldEqual username
      existingUser.address shouldEqual address
      existingUser.email shouldEqual email
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
