package example.repository

import example.domain.IotDevice

trait IotDeviceRepository[F[_]] {
  def registerDevice(userId: Long, serialNumber: String): F[IotDevice]
  def getById(id: Long): F[Option[IotDevice]]
  def getBySn(sn: String): F[Option[IotDevice]]
  def getByUser(userId: Long): F[Seq[IotDevice]]
  def getAll: F[Seq[IotDevice]]
}
