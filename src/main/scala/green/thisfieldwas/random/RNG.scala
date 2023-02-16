package green.thisfieldwas.random

import java.lang.Double.longBitsToDouble
import scala.annotation.tailrec

trait RNG {

  def next(): (RNG, Long)

  def next(numBits: Int): (RNG, Long) = {
    val (nextGen, result) = next()
    (nextGen, result & ((1L << numBits) - 1))
  }

  def nextLong(bound: Long = Long.MaxValue): (RNG, Long) = {
    val (nextGen, result) = next(63)
    (nextGen, (result & Long.MaxValue) % bound)
  }

  def nextBoolean(): (RNG, Boolean) = {
    val (nextGen, result) = nextLong()
    (nextGen, (result & 1L) != 0)
  }

  def nextInt(bound: Int = Int.MaxValue): (RNG, Int) = {
    val (nextGen, result) = next(31)
    (nextGen, (result.toInt & Int.MaxValue) % bound)
  }

  def nextDouble(bound: Double = 1.0): (RNG, Double) = {
    val (nextGen, longValue) = nextLong()
    val doubleValue = longBitsToDouble(0x3ffL << 52 | longValue >>> 12) - 1.0
    (nextGen, doubleValue / 1.0 * bound) // scale the value
  }

  def nextChar(): (RNG, Char) = {
    @tailrec
    def go(gen: RNG): (RNG, Char) = {
      val (nextGen, result) = gen.next(16)
      if (result.isValidChar) {
        (nextGen, result.toChar)
      } else {
        go(gen)
      }
    }
    go(this)
  }

  def nextAsciiChar(): (RNG, Char) = {
    val (nextGen, result) = next(7)
    (nextGen, result.toChar)
  }

  def nextIdChar(): (RNG, Char) = {
    @tailrec
    def go(gen: RNG): (RNG, Char) = {
      val (nextGen, result) = gen.next(7)
      val charResult = result.toChar
      if (charResult.isLetterOrDigit || charResult == '_') {
        (nextGen, charResult)
      } else {
        go(nextGen)
      }
    }
    go(this)
  }

  def nextAsciiString(length: Int): (RNG, String) =
    RNG.genString(this, _.nextAsciiChar(), length)

  def nextIdString(length: Int): (RNG, String) =
    RNG.genString(this, _.nextIdChar(), length)
}

object RNG {

  private def genString(
                         initGen: RNG,
                         genChar: RNG => (RNG, Char),
                         length: Int,
                       ): (RNG, String) = {
    val (nextGen, sb) = (0 to length).foldLeft((initGen, new StringBuilder())) { case ((gen, sb), _) =>
      genChar(gen) match {
        case (nextGen, result) =>
          (nextGen, sb.append(result))
      }
    }
    (nextGen, sb.toString())
  }
}
