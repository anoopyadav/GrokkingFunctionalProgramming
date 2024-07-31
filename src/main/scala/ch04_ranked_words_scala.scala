object ch04_ranked_words_scala extends App {
  def score(word: String): Int = word.replaceAll("a", "").length

  def bonus(word: String): Int = if word.contains("c") then 5 else 0

  def penalty(word: String): Int = if word.contains("s") then 7 else 0

  def rankedWords(wordScore: String => Int, words: List[String]): List[String] = {
    words.sortBy(wordScore).reverse
  }

  def wordScores(words: List[String]): List[Int] = words.map(score)

  val words = List("ada", "haskell", "java", "scala", "rust")
  assert(rankedWords(score, words) == List("haskell", "rust", "scala", "java", "ada"))
  assert(rankedWords(w => score(w) + bonus(w), words) == List("scala", "haskell", "rust", "java", "ada"))
  assert(rankedWords(w => score(w) + bonus(w) - penalty(w), words) == List("java", "scala", "ada", "haskell", "rust"))
  assert(wordScores(words) == List(1, 6, 2, 3, 4))
}
