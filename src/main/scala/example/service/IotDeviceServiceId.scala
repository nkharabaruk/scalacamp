package example.service

import cats.Id
import example.repository.{IotDeviceRepository, UserRepository}

/**
  * Id implementation of the IOT Device service.
  * @param repository the repository of the IOT Device.
  * @param userRepository the repository of the User.
  */
class IotDeviceServiceId(repository: IotDeviceRepository[Id],
                         userRepository: UserRepository[Id]) extends IotDeviceService[Id](repository, userRepository)
