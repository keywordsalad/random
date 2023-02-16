package green.thisfieldwas.random

import java.awt.event.KeyEvent
import java.lang.Double.longBitsToDouble

object Rand {

  implicit class RandOps[A](val rand: Rand[A]) extends AnyVal {

    def map[B](f: A => B): Rand[B] = rng => {
      val (nextRng, x) = rand(rng)
      (nextRng, f(x))
    }

    def ap[B](ff: Rand[A => B]): Rand[B] = rng => {
      val (nextRng1, x) = rand(rng)
      val (nextRng2, f) = ff(nextRng1)
      (nextRng2, f(x))
    }

    def flatMap[B](f: A => Rand[B]): Rand[B] = rng => {
      val (nextRng, x) = rand(rng)
      f(x)(nextRng)
    }
  }

  def pure[A](value: A): Rand[A] = (_, value)

  def flatten[A](rand: Rand[Rand[A]]): Rand[A] =
    rand.flatMap(identity)

  def next(numBits: Int): Rand[Long] = rng => {
    val (nextRng, result) = rng.next()
    (nextRng, result & ((1L << numBits) - 1))
  }

  def nextInt(bound: Int = Int.MaxValue): Rand[Int] =
    next(31).map(i => (i.toInt & Int.MaxValue) % bound)

  def nextLong(bound: Long = Long.MaxValue): Rand[Long] =
    next(63).map(i => (i & Long.MaxValue) % bound)

  def nextDouble(bound: Double = 1.0): Rand[Double] =
    nextLong().map(n => (longBitsToDouble(0x3ffL << 52 | n >>> 12) - 1.0) / 1.0 * bound)

  def nextBoolean(): Rand[Boolean] =
    nextLong().map(n => (n & 1L) != 0)

  def nextChar(): Rand[Char] =
    next(16).flatMap(c => if (c.isValidChar) pure(c.toChar) else nextChar())

  def nextAsciiChar(): Rand[Char] =
    next(7).flatMap(c => if (c.isValidChar) pure(c.toChar) else nextAsciiChar())

  def nextIdChar(): Rand[Char] =
    nextChar().flatMap(c => if (c.isLetterOrDigit || c == '_') pure(c) else nextIdChar())

  def nextPrintableChar(): Rand[Char] =
    nextChar().flatMap { c =>
      if (
        !Character.isISOControl(c) && c != KeyEvent.CHAR_UNDEFINED && Option(Character.UnicodeBlock.of(c))
          .fold(false)(_ ne Character.UnicodeBlock.SPECIALS)
      ) {
        pure(c)
      } else {
        nextPrintableChar()
      }
    }

  def nextAsciiString(length: Int): Rand[String] =
    repeat(length)(nextAsciiChar()).map(_.mkString(""))

  def nextIdString(length: Int): Rand[String] =
    repeat(length)(nextIdChar()).map(_.mkString(""))

  def nextPrintableString(length: Int): Rand[String] =
    repeat(length)(nextPrintableChar()).map(_.mkString(""))

  def sequence[A](listOfRand: List[Rand[A]]): Rand[List[A]] =
    listOfRand
      .foldLeft(pure(List.empty[A]))((randOfList, rand) => rand.flatMap(x => randOfList.map(x +: _)))
      .map(_.reverse)

  def repeat[A](count: Int)(rand: Rand[A]): Rand[List[A]] =
    sequence(List.fill(count)(rand))
}
