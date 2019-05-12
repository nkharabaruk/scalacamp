package example.repository

import java.util.concurrent.atomic.AtomicLong
import cats.Id
import example.domain.IotDevice

class IotDeviceRepositoryId extends IotDeviceRepository[Id] {
  private var storage: Map[Long, IotDevice] = Map()
  private val id = new AtomicLong(0)

  override def registerDevice(userId: Long, serialNumber: String): Id[IotDevice] = {
    val deviceId = id.incrementAndGet()
    val iotDevice = IotDevice(deviceId, userId, serialNumber)
    storage = storage + (deviceId -> iotDevice)
    iotDevice
  }
  override def getById(id: Long): Id[Option[IotDevice]] = storage.get(id)
  override def getBySn(sn: String): Id[Option[IotDevice]] = storage.values.find(device => device.sn == sn)
  override def getByUser(userId: Long): Id[Seq[IotDevice]] = storage.values.filter(device => device.userId == userId).toSeq
}
