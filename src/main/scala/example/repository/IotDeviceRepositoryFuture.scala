package example.repository

import java.util.concurrent.atomic.AtomicLong
import example.domain.IotDevice
import scala.concurrent.{ExecutionContext, Future}

class IotDeviceRepositoryFuture(implicit val ec: ExecutionContext) extends IotDeviceRepository[Future] {
  private var storage: Map[Long, IotDevice] = Map()
  private val id = new AtomicLong(0)

  override def registerDevice(userId: Long, serialNumber: String): Future[IotDevice] = Future {
    val deviceId = id.incrementAndGet()
    val iotDevice = IotDevice(deviceId, userId, serialNumber)
    storage = storage + (deviceId -> iotDevice)
    iotDevice
  }
  override def getById(id: Long): Future[Option[IotDevice]] = Future {storage.get(id)}
  override def getBySn(sn: String): Future[Option[IotDevice]] = Future {storage.values.find(device => device.sn == sn)}
  override def getByUser(userId: Long): Future[Seq[IotDevice]] = Future {storage.values.filter(device => device.userId == userId).toSeq}
}
