package example.repository

import example.domain.IotDevice
import org.scalatest.{FlatSpec, Matchers}
import slick.jdbc.H2Profile.api._

class IotDeviceRepositoryIdTest extends FlatSpec with Matchers {

  private val db = Database.forConfig("scalacamp")
  private val iotDeviceRepository = new IotDeviceRepositoryId(db)
  db.run(iotDeviceRepository.iotDevices.schema.create)
  private val userId = 1
  private val serialNumber = "EA2700"
  private val iotDeviceId = 1

  "Register iot device" should "return valid result" in {
    val iotDevice = iotDeviceRepository.registerDevice(userId, serialNumber)
    iotDevice.id shouldEqual iotDeviceId
    iotDevice.sn shouldEqual serialNumber
    iotDevice.userId shouldEqual userId
  }

  "Retrieve iot device by id" should "return valid result" in {
    val retrievedById = iotDeviceRepository.getById(iotDeviceId).get
    retrievedById.id shouldEqual iotDeviceId
    retrievedById.sn shouldEqual serialNumber
    retrievedById.userId shouldEqual userId
  }

  "Retrieve iot device with invalid id" should "return none" in {
    iotDeviceRepository.getById(2) shouldEqual None
  }

  "Retrieve iot device by serial number" should "return valid result" in {
    val retrievedBySerialNumber = iotDeviceRepository.getBySn(serialNumber).get
    retrievedBySerialNumber.id shouldEqual iotDeviceId
    retrievedBySerialNumber.sn shouldEqual serialNumber
    retrievedBySerialNumber.userId shouldEqual userId
  }

  "Retrieve iot device with invalid serial number" should "return none" in {
    iotDeviceRepository.getBySn("Random serial number") shouldEqual None
  }

  "Retrieve iot devices by user" should "return valid result" in {
    val anotherSN = "BB0012"
    val anotherIotDeviceId = 2
    val anotherIotDevice = iotDeviceRepository.registerDevice(userId,anotherSN)
    anotherIotDevice.id shouldEqual anotherIotDeviceId

    val allRetrievedByUser = iotDeviceRepository.getByUser(userId)
    allRetrievedByUser.size shouldEqual 2
    allRetrievedByUser.head shouldEqual IotDevice(iotDeviceId, userId, serialNumber)
    allRetrievedByUser.tail.head shouldEqual anotherIotDevice
  }

  "Retrieve iot device with invalid user" should "return none" in {
    iotDeviceRepository.getByUser(2) shouldEqual List.empty
  }

  "Register iot device with the same serial number" should "return valid result" in {
    val iotDevice = iotDeviceRepository.registerDevice(userId, serialNumber)
    iotDevice.id shouldEqual 3
    iotDevice.sn shouldEqual serialNumber
    iotDevice.userId shouldEqual 1
  }
}
