package example.repository

import java.util.concurrent.atomic.AtomicLong
import example.domain.IotDevice
import example.schema.IotDeviceTable
import scala.concurrent.{ExecutionContext, Future}
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery

class IotDeviceRepositoryFuture(db: Database)(implicit val ec: ExecutionContext) extends IotDeviceRepository[Future] {

  lazy val iotDevices = TableQuery[IotDeviceTable]
  private val id = new AtomicLong(0)

  override def registerDevice(userId: Long, serialNumber: String): Future[IotDevice] = Future {
    val deviceId = id.incrementAndGet()
    val iotDevice = IotDevice(deviceId, userId, serialNumber)
    db.run(iotDevices.insertOrUpdate(iotDevice))
    iotDevice
  }
  override def getById(id: Long): Future[Option[IotDevice]] = {
    db.run(iotDevices.filter(_.id === id).result.headOption)
  }
  override def getBySn(sn: String): Future[Option[IotDevice]] = {
    db.run(iotDevices.filter(_.serialNumber === sn).result.headOption)
  }
  override def getByUser(userId: Long): Future[Seq[IotDevice]] = {
    db.run(iotDevices.filter(_.userId === userId).result)
  }
  override def getAll: Future[Seq[IotDevice]] = {
    db.run(iotDevices.result)
  }
}
