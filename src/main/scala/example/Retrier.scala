package example

import scala.annotation.tailrec
import scala.concurrent.duration.FiniteDuration

class Retrier {

  @tailrec
  final def retry[A](block: () => A,
                     acceptResult: A => Boolean,
                     retries: List[FiniteDuration]): A = {
    val result = block.apply()
    val accept = acceptResult.apply(result)
    if (accept || retries.isEmpty) return result
    Thread.sleep(retries.head.toMillis)
    retry(block, acceptResult, retries.tail)
  }
}
