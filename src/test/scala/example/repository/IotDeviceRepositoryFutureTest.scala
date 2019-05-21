package example.repository

import example.domain.IotDevice
import org.scalatest.{AsyncFlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

class IotDeviceRepositoryFutureTest extends AsyncFlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val iotDeviceRepository = new IotDeviceRepositoryFuture(db)
  db.run(iotDeviceRepository.iotDevices.schema.create)
  private val userRepository = new UserRepositoryFuture(db)
  db.run(userRepository.users.schema.create)
  userRepository.registerUser("Homer Simpson", Option("Springfield"), "homer_simpson@email.com")
  private val userId = 1
  private val serialNumber = "EA2700"
  private val iotDeviceId = 1

  "Register iot device" should "return valid result" in {
    iotDeviceRepository.registerDevice(userId, serialNumber).map { iotDevice =>
      iotDevice.id shouldEqual iotDeviceId
      iotDevice.sn shouldEqual serialNumber
      iotDevice.userId shouldEqual userId
    }
  }

  "Retrieve iot device by id" should "return valid result" in {
    iotDeviceRepository.getById(iotDeviceId).map { retrievedById =>
      retrievedById.get.id shouldEqual iotDeviceId
      retrievedById.get.sn shouldEqual serialNumber
      retrievedById.get.userId shouldEqual userId
    }
  }

  "Retrieve iot device with invalid id" should "return none" in {
    iotDeviceRepository.getById(2).map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual None
    }
  }

  "Retrieve iot device by serial number" should "return valid result" in {
    iotDeviceRepository.getBySn(serialNumber).map { retrievedBySerialNumber =>
      retrievedBySerialNumber.get.id shouldEqual iotDeviceId
      retrievedBySerialNumber.get.sn shouldEqual serialNumber
      retrievedBySerialNumber.get.userId shouldEqual userId
    }
  }

  "Retrieve iot device with invalid serial number" should "return none" in {
    iotDeviceRepository.getBySn("Random serial number").map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual None
    }
  }

  "Retrieve iot devices by user" should "return valid result" in {
    val anotherSerialNumber = "BB0012"
    val anotherIotDeviceId = 2
    iotDeviceRepository.registerDevice(userId, anotherSerialNumber).map { anotherIotDevice =>
      anotherIotDevice.id shouldEqual anotherIotDeviceId
    }
    iotDeviceRepository.getByUser(userId).map { allRetrievedByUser =>
      allRetrievedByUser.size shouldEqual 1
      allRetrievedByUser.head shouldEqual IotDevice(iotDeviceId, userId, serialNumber)
    }
  }

  "Retrieve iot device with invalid user" should "return none" in {
    iotDeviceRepository.getByUser(2).map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual List.empty
    }
  }

  "Register iot device with the same serial number" should "return valid result" in {
    iotDeviceRepository.registerDevice(userId, serialNumber).map { oneMoreIotDevice =>
      oneMoreIotDevice.id shouldEqual 3
    }
  }

  "Retrieve all iot devices" should "return valid result" in {
    iotDeviceRepository.registerDevice(userId, serialNumber).map { anotherIotDevice =>
      anotherIotDevice.id shouldEqual iotDeviceId
    }
    iotDeviceRepository.getAll.map { allIotDevices =>
      allIotDevices.nonEmpty shouldEqual true
      allIotDevices.size shouldEqual 3
    }
  }
}
