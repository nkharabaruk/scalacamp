package example.schema

import example.domain.IotDevice
import slick.jdbc.H2Profile.api._

/**
  * Entity representing IOT Device Table in the database.
  * @param tag a tag marks a specific row.
  */
class IotDeviceTable(tag: Tag) extends Table[IotDevice](tag, "IOT_DEVICE") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def userId = column[Long]("user_id")
  def serialNumber = column[String]("serial_number")

  def * = (id, userId, serialNumber).mapTo[IotDevice]
}

