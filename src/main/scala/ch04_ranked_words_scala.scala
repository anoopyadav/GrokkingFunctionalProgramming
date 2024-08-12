object ch04_ranked_words_scala extends App {
  def score(word: String): Int = word.replaceAll("a", "").length

  def bonus(word: String): Int = if word.contains("c") then 5 else 0

  def penalty(word: String): Int = if word.contains("s") then 7 else 0

  def rankedWords(wordScore: String => Int, words: List[String]): List[String] = {
    words.sortBy(wordScore).reverse
  }

  def wordScores(words: List[String]): List[Int] = words.map(score)

  def highScoringWords(wordScore: String => Int): Int => List[String] => List[String] = {
    higherThan =>
      words =>
        words.filter(word => wordScore(word) > higherThan)
  }

  val words = List("ada", "haskell", "java", "scala", "rust")
  assert(rankedWords(score, words) == List("haskell", "rust", "scala", "java", "ada"))
  assert(rankedWords(w => score(w) + bonus(w), words) == List("scala", "haskell", "rust", "java", "ada"))
  assert(rankedWords(w => score(w) + bonus(w) - penalty(w), words) == List("java", "scala", "ada", "haskell", "rust"))
  assert(wordScores(words) == List(1, 6, 2, 3, 4))

  // Higher-order function
  val wordsWithScoreHigherThan: Int => List[String] => List[String] =
    highScoringWords(w => score(w) + bonus(w) - penalty(w))

  val words1: List[String] = List("ada", "haskell", "scala", "java", "rust")
  val words2: List[String] = List("football", "f1", "hockey", "basketball")

  assert(wordsWithScoreHigherThan(1)(words1) == List("java"))
  assert(wordsWithScoreHigherThan(0)(words2) == List("football", "f1", "hockey", "basketball"))
  assert(wordsWithScoreHigherThan(5)(words2) == List("football", "hockey"))

  // And we're currying!
  def cumulativeScore(wordScore: String => Int)(words: List[String]): Int = {
    words.foldLeft(0)((total, word) => total + wordScore(word))
  }

  val cumulativeScoreNormal = cumulativeScore(score)
  assert(cumulativeScoreNormal(words) == 16)
  assert(cumulativeScoreNormal(words2) == 23)
}
