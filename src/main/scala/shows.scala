object shows extends App {
  case class Show(name: String, start: Int, end: Int)

  // Supported Format strings
  // NAME (START - END)
  // NAME (YEAR)
  def parseShow(rawShow: String): Either[String, Show] =
    for {
      name <- parseName(rawShow)
      startYear <- parseStartYear(rawShow).orElse(parseSingleYear(rawShow))
      endYear <- parseEndYear(rawShow).orElse(parseSingleYear(rawShow))
      show <- if isValidShow(name, startYear, endYear) then Right(Show(name, startYear, endYear)) else Left("Failed")
    } yield show

  private def parseName(rawShow: String): Either[String, String] =
    val bracketOpen = rawShow.indexOf("(")
    if bracketOpen > 0 then Right(rawShow.substring(0, bracketOpen).trim) else Left(s"Failed to extract name for $rawShow")

  private def parseStartYear(rawShow: String): Either[String, Int] =
    val bracketOpen = rawShow.indexOf("(")
    val dash = rawShow.indexOf("-")
    for {
      startYearString <- if bracketOpen > 0 && dash > bracketOpen then Right(rawShow.substring(bracketOpen + 1, dash).trim) else Left(s"Failed to extract start year for ${rawShow}")
      startYear <- startYearString.toIntOption.toRight(s"Failed to convert year to Int for ${rawShow}")
    } yield startYear

  private def parseEndYear(rawShow: String): Either[String, Int] =
    val bracketClose = rawShow.indexOf(")")
    val dash = rawShow.indexOf("-")
    for {
      endYearString <- if dash > 0 && dash < bracketClose then Right(rawShow.substring(dash + 1, bracketClose).trim) else Left(s"Failed to extract end year for ${rawShow}")
      endYear <- endYearString.toIntOption.toRight(s"Failed to convert end year to Int for ${rawShow}")
    } yield endYear

  private def parseSingleYear(rawShow: String): Either[String, Int] =
    val bracketOpen = rawShow.indexOf("(")
    val bracketClose = rawShow.indexOf(")")

    for {
      yearString <- if bracketOpen > 0 && bracketClose > bracketOpen then Right(rawShow.substring(bracketOpen + 1, bracketClose)) else Left(s"Failed to extract single year for ${rawShow}")
      year <- yearString.toIntOption.toRight(s"Failed to convert single year to Int for ${rawShow}")
    } yield year

  private def isValidShow(name: String, startYear: Int, endYear: Int): Boolean =
    name.nonEmpty && endYear >= startYear

  // Unit tests
  assert(parseName("Burn Notice (2007 - 2013").contains("Burn Notice"))
  assert(parseName("(2007 - 2013)") == Left("Failed to extract name for (2007 - 2013)"))

  assert(parseStartYear("Burn Notice (2007 - 2013)").contains(2007))
  assert(parseStartYear("Burn Notice (2007)") == Left(s"Failed to extract start year for Burn Notice (2007)"))

  assert(parseEndYear("Burn Notice (2007 - 2013)").contains(2013))
  assert(parseEndYear("Burn Notice (2007 -") == Left(s"Failed to extract end year for Burn Notice (2007 -"))

  assert(parseSingleYear("Chernobyl (2019)").contains(2019))
  assert(parseSingleYear("(2019)") == Left("Failed to extract single year for (2019)"))

  // Happy cases
  assert(parseShow("Chernobyl (2019)").contains(Show("Chernobyl", 2019, 2019)))
  assert(parseShow("Burn Notice (2007 - 2013)").contains(Show("Burn Notice", 2007, 2013)))

  // Not-so-happy cases
  assert(parseShow("") == Left("Failed to extract name for "))
  assert(parseShow("Invalid (2009 - )") == Left("Failed to convert single year to Int for Invalid (2009 - )"))
  assert(parseShow("Invalid (- 2010)") == Left("Failed to convert single year to Int for Invalid (- 2010)"))
  assert(parseShow("(2009 - 2010)") == Left("Failed to extract name for (2009 - 2010)"))
  assert(parseShow("(2009)") == Left("Failed to extract name for (2009)"))
  assert(parseShow("Invalid") == Left("Failed to extract name for Invalid"))
  assert(parseShow("Burn Notice (2019 - 2010)") == Left("Failed"))
  assert(parseShow("Chernobyl 2019 - 2019") == Left("Failed to extract name for Chernobyl 2019 - 2019"))
}
