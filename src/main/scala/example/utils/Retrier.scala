package example.utils

import java.util.concurrent.ForkJoinPool
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}

/**
  * Implements retrying logic for resources which should be called multiple times to retrieve better result.
  */
class Retrier {

  private implicit val executionContext: ExecutionContextExecutor = ExecutionContext.fromExecutor(new ForkJoinPool())

  final def retry[A](block: () => Future[A],
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): Future[A] = {
    block().flatMap(result => {
      if (acceptResult(result) || retries.isEmpty) {
        Future.successful(result)
      } else {
        Thread.sleep(retries.head.toMillis)
        retry(block, acceptResult, retries.tail)
      }
    })
  }
}
