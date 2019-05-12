package example.repository

import org.scalatest.{FlatSpec, Matchers}

class IotDeviceRepositoryIdTest extends FlatSpec with Matchers {

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
}
