package example.domain

/**
  * Entity representing IOT device object.
  * @param id unique identifier of the iot device.
  * @param userId id of the user the iot device belongs to.
  * @param sn the serial number of the ion device.
  */
case class IotDevice(id: Long, userId: Long, sn: String)
