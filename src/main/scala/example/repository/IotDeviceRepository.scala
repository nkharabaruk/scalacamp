package example.repository

import example.domain.IotDevice

/**
  * Manages operations with IOT Device table in database.
  * @tparam F specific type of return data.
  */
trait IotDeviceRepository[F[_]] {
  def registerDevice(userId: Long, serialNumber: String): F[IotDevice]
  def getById(id: Long): F[Option[IotDevice]]
  def getBySn(sn: String): F[Option[IotDevice]]
  def getByUser(userId: Long): F[Seq[IotDevice]]
  def getAll: F[Seq[IotDevice]]
}
