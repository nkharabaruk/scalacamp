package example.repository

import example.domain.IotDevice
import org.scalatest.{AsyncFlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

class IotDeviceRepositoryFutureTest extends AsyncFlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val iotDeviceRepositoryFuture = new IotDeviceRepositoryFuture(db)
  db.run(iotDeviceRepositoryFuture.iotDevices.schema.create)
  private val userId = 1
  private val serialNumber = "EA2700"
  private val iotDeviceId = 1

  "Register iot device" should "return valid result" in {
    iotDeviceRepositoryFuture.registerDevice(userId, serialNumber).map { iotDevice =>
      iotDevice.id shouldEqual iotDeviceId
      iotDevice.sn shouldEqual serialNumber
      iotDevice.userId shouldEqual userId
    }
  }

  "Retrieve iot device by id" should "return valid result" in {
    iotDeviceRepositoryFuture.getById(iotDeviceId).map { retrievedById =>
      retrievedById.get.id shouldEqual iotDeviceId
      retrievedById.get.sn shouldEqual serialNumber
      retrievedById.get.userId shouldEqual userId
    }
  }

  "Retrieve iot device with invalid id" should "return none" in {
    iotDeviceRepositoryFuture.getById(2).map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual None
    }
  }

  "Retrieve iot device by serial number" should "return valid result" in {
    iotDeviceRepositoryFuture.getBySn(serialNumber).map { retrievedBySerialNumber =>
      retrievedBySerialNumber.get.id shouldEqual iotDeviceId
      retrievedBySerialNumber.get.sn shouldEqual serialNumber
      retrievedBySerialNumber.get.userId shouldEqual userId
    }
  }

  "Retrieve iot device with invalid serial number" should "return none" in {
    iotDeviceRepositoryFuture.getBySn("Random serial number").map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual None
    }
  }

  "Retrieve iot devices by user" should "return valid result" in {
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
  }

  "Retrieve iot device with invalid user" should "return none" in {
    iotDeviceRepositoryFuture.getByUser(2).map { nonRegisteredIotDevice =>
      nonRegisteredIotDevice shouldEqual List.empty
    }
  }

  "Register iot device with the same serial number" should "return valid result" in {
    iotDeviceRepositoryFuture.registerDevice(userId, serialNumber).map { oneMoreIotDevice =>
      oneMoreIotDevice.id shouldEqual 3
    }
  }
}
