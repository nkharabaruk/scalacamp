package example.service

import cats.Id
import example.repository.{IotDeviceRepository, UserRepository}

class IotDeviceServiceId(repository: IotDeviceRepository[Id],
                         userRepository: UserRepository[Id]) extends IotDeviceService[Id](repository, userRepository)
