package example.repository

import java.util.concurrent.atomic.AtomicLong
import cats.Id
import example.domain.IotDevice
import example.schema.IotDeviceTable
import slick.jdbc.H2Profile.api._
import slick.lifted.TableQuery
import scala.concurrent.Await
import scala.concurrent.duration._

class IotDeviceRepositoryId(db: Database) extends IotDeviceRepository[Id] {

  lazy val iotDevices = TableQuery[IotDeviceTable]
  private val id = new AtomicLong(0)

  override def registerDevice(userId: Long, serialNumber: String): Id[IotDevice] = {
    val deviceId = id.incrementAndGet()
    val iotDevice = IotDevice(deviceId, userId, serialNumber)
    db.run(iotDevices.insertOrUpdate(iotDevice))
    iotDevice
  }
  override def getById(id: Long): Id[Option[IotDevice]] = {
    Await.result(db.run(iotDevices.filter(_.id === id).result.headOption), 2.seconds)
  }
  override def getBySn(sn: String): Id[Option[IotDevice]] = {
    Await.result(db.run(iotDevices.filter(_.serialNumber === sn).result.headOption), 2.seconds)
  }
  override def getByUser(userId: Long): Id[Seq[IotDevice]] = {
    Await.result(db.run(iotDevices.filter(_.userId === userId).result), 2.seconds)
  }
  override def getAll: Id[Seq[IotDevice]] = {
    Await.result(db.run(iotDevices.result), 2.seconds)
  }
}
