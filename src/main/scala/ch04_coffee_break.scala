object ch04_coffee_break extends App {
  def largerThan(threshold: Int): Int => Boolean = {
    number => number > threshold
  }

  def divisibleBy(divisor: Int): Int => Boolean = {
    number => number % divisor == 0
  }

  def shorterThan(length: Int): String => Boolean = {
    word => word.length < length
  }

  def numberOfS(number: Int): String => Boolean = word => word.length - word.replaceAll("s", "").length > number

  assert(List(5, 1, 2, 4, 0).filter(largerThan(4)) == List(5))
  assert(List(5, 1, 2, 4, 0).filter(largerThan(1)) == List(5, 2, 4))

  assert(List(5, 1, 2, 4, 15).filter(divisibleBy(5)) == List(5, 15))
  assert(List(5, 1, 2, 4, 15).filter(divisibleBy(2)) == List(2, 4))

  assert(List("scala", "ada").filter(shorterThan(4)) == List("ada"))
  assert(List("scala", "ada").filter(shorterThan(7)) == List("scala", "ada"))

  assert(List("rust", "ada").filter(numberOfS(2)) == List())
  assert(List("rust", "ada").filter(numberOfS(0)) == List("rust"))
}
