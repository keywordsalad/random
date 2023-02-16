package green.thisfieldwas.random

trait RNG {

  /**
   * Generates the next 64 bits from the RNG and return the next state.
   *
   * @return
   */
  def next(): (RNG, Long)
}
