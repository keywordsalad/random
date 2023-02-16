package green.thisfieldwas.random

import java.awt.event.KeyEvent
import java.lang.Double.longBitsToDouble

/**
 * RNG state transition functions.
 */
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

  /**
   * Lift a pure value into the Rand context.
   *
   * @param value The value to lift.
   * @return A Rand generator that returns the value and given RNG.
   */
  def pure[A](value: A): Rand[A] = (_, value)

  /**
   * Flattens a nested Rand context.
   *
   * @param rand The nested context.
   * @tparam A The data produced by the context.
   * @return The flattened context.
   */
  def flatten[A](rand: Rand[Rand[A]]): Rand[A] =
    rand.flatMap(identity)

  /**
   * Gets `numBits` from the next Long value produced by the RNG.
   *
   * @param numBits The number of bits to return.
   * @return A Rand producing `numBits` from the next value.
   */
  def next(numBits: Int): Rand[Long] = rng => {
    val (nextRng, result) = rng.next()
    (nextRng, result & ((1L << numBits) - 1))
  }

  /**
   * Gets the next Int value between 0 and `bound` produced by the RNG.
   *
   * @param bound The maximum limit of the Int value.
   * @return A Rand producing the next Int value.
   */
  def nextInt(bound: Int = Int.MaxValue): Rand[Int] =
    next(31).map(i => (i.toInt & Int.MaxValue) % bound)

  /**
   * Gets the next Long value between 0 and `bound` produced by the RNG.
   *
   * @param bound The maximum limit of the Long value.
   * @return A Rand producing the next Long value.
   */
  def nextLong(bound: Long = Long.MaxValue): Rand[Long] =
    next(63).map(i => (i & Long.MaxValue) % bound)

  /**
   * Gets the next Double value between 0 and `bound` produced by the RNG.
   *
   * @param bound The maximum limit of the Double value.
   * @return A Rand producing the next Double value.
   */
  def nextDouble(bound: Double = 1.0): Rand[Double] =
    nextLong().map(n => (longBitsToDouble(0x3ffL << 52 | n >>> 12) - 1.0) / 1.0 * bound)

  /**
   * Gets the next Boolean value produced by the RNG.
   *
   * @return A Rand producing the next Boolean value.
   */
  def nextBoolean(): Rand[Boolean] =
    nextLong().map(n => (n & 1L) != 0)

  /**
   * Gets the next Char value produced by the RNG.
   *
   * @return A Rand producing the next Char value.
   */
  def nextChar(): Rand[Char] =
    next(16).flatMap(c => if (c.isValidChar) pure(c.toChar) else nextChar())

  /**
   * Gets the next ASCII Char value produced by the RNG.
   *
   * @return A Rand producing the next ASCII Char value.
   */
  def nextAsciiChar(): Rand[Char] =
    next(7).flatMap(c => if (c.isValidChar) pure(c.toChar) else nextAsciiChar())

  /**
   * Gets the next Java identifier Char value produced by the RNG.
   *
   * @return A Rand producing the next Char value.
   */
  def nextIdChar(): Rand[Char] =
    nextChar().flatMap(c => if (c.isLetterOrDigit || c == '_') pure(c) else nextIdChar())

  /**
   * Gets the next printable Char value produced by the RNG.
   *
   * @return A Rand producing the next Char value.
   */
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

  /**
   * Gets a String of ASCII characters produced by the RNG.
   *
   * @param length The length of the String to generate.
   * @return A Rand producing a String of the given `length`.
   */
  def nextAsciiString(length: Int): Rand[String] =
    repeat(length)(nextAsciiChar()).map(_.mkString(""))

  /**
   * Gets a String of Java identifier characters produced by the RNG.
   *
   * @param length The length of the String to generate.
   * @return A Rand producing a String of the given `length`.
   */
  def nextIdString(length: Int): Rand[String] =
    repeat(length)(nextIdChar()).map(_.mkString(""))

  /**
   * Gets a String of printable characters produced by the RNG.
   *
   * @param length The length of the String to generate.
   * @return A Rand producing a String of the given `length`.
   */
  def nextPrintableString(length: Int): Rand[String] =
    repeat(length)(nextPrintableChar()).map(_.mkString(""))

  /**
   * Sequences a List of Rand's into a Rand producing a List of random values.
   *
   * @param listOfRand The List of Rand's.
   * @tparam A The data produced by the Rand.
   * @return A Rand producing a List of generated values.
   */
  def sequence[A](listOfRand: List[Rand[A]]): Rand[List[A]] =
    listOfRand
      .foldLeft(pure(List.empty[A]))((randOfList, rand) => rand.flatMap(x => randOfList.map(x +: _)))
      .map(_.reverse)

  /**
   * Repeats a Rand `count` times to generate a List of `count` randomly generated values.
   *
   * @param count The number of times to repeat the `rand`.
   * @param rand The Rand to repeat.
   * @tparam A The data produced by the `rand`.
   * @return A Rand producing a list of `count` values.
   */
  def repeat[A](count: Int)(rand: Rand[A]): Rand[List[A]] =
    sequence(List.fill(count)(rand))
}
