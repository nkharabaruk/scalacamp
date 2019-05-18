package example.schema

import example.domain.IotDevice
import slick.jdbc.H2Profile.api._

class IotDeviceTable(tag: Tag) extends Table[IotDevice](tag, "IOT_DEVICE") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id")
  def serialNumber = column[String]("serial_number")

  def * = (id, userId, serialNumber).mapTo[IotDevice]
}

