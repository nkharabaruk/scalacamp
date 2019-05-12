package example.repository

import example.domain.IotDevice
import org.scalatest.{AsyncFlatSpec, Matchers}

class IotDeviceRepositoryFutureTest extends AsyncFlatSpec with Matchers {

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
}
