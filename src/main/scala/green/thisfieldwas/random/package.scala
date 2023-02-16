package green.thisfieldwas

package object random {

  /**
   * RNG state transition function.
   *
   * @tparam A The data produced by the RNG.
   */
  type Rand[A] = RNG => (RNG, A)
}
