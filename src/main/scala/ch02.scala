object TipCalculator {
  def getTipPercentage(names: List[String]): Int = {
    if (names.length > 5) 20
    else if (names.length > 0) 15
    else 0
  }
}

object ch02 extends App {
  assert(TipCalculator.getTipPercentage(List.empty) == 0)

  val justApple = List("Name", "Name", "Name", "Name", "Name", "Name")
  assert(TipCalculator.getTipPercentage(justApple) == 20)

  val appleAndBook = List("Name", "Name", "Name", "Name")
  assert(TipCalculator.getTipPercentage(appleAndBook) == 15)
}