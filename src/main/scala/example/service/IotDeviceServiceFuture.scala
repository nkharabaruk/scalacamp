package example.service

import example.repository.{IotDeviceRepository, UserRepository}
import scala.concurrent.{ExecutionContext, Future}
import cats.implicits._

class IotDeviceServiceFuture(repository: IotDeviceRepository[Future],
                             userRepository: UserRepository[Future])
                            (implicit ec: ExecutionContext) extends IotDeviceService[Future](repository, userRepository)
