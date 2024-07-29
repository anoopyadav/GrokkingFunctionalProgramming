object NameShortener {
  def abbreviate(name: String): String = {
    val separator = name.indexOf(" ")
    // No need to abbreviate if only one name
    if (separator < 0) name
    else {
      // Get first name by splicing the string at first space
      val initial = name.substring(0, 1)
      val remainder = name.substring(separator + 1)
      initial + ". " + remainder
    }
  }
}

object ch03_strings extends App {
  // Normal usage
  assert(NameShortener.abbreviate("Anoop Yadav") == "A. Yadav")
  assert(NameShortener.abbreviate("Vincent van Gogh") == "V. van Gogh")
  // Corner cases
  assert(NameShortener.abbreviate("Snoop") == "Snoop")
  assert(NameShortener.abbreviate("") == "")
}
