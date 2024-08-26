object ch06 extends App {
  case class TvShow(title: String, start: Int, end: Int)

  def sortShows(shows: List[TvShow]): List[TvShow] = {
    shows.sortBy(show => show.end - show.start).reverse
  }

  def parseShowsBestEffort(shows: List[String]): List[TvShow] =
    shows.map(parseShow).flatMap(_.toSeq)

  def parseShowsAllOrNothing(shows: List[String]): Either[String, List[TvShow]] =
    val initialResult = Right(List.empty)
    shows.map(parseShow).foldLeft(initialResult)(addOrResign)

  def extractName(rawShow: String): Either[String, String] =
    val bracketOpen = rawShow.indexOf('(')
    if bracketOpen == -1 then Left(s"Failed to extract title for ${rawShow}") else Right(rawShow.substring(0, bracketOpen).trim)

  def extractStart(rawShow: String): Either[String, Int] =
    val bracketOpen = rawShow.indexOf('(')
    val dash = rawShow.indexOf('-')
    for {
      startYearString <- if bracketOpen == -1 || dash == -1 || dash < bracketOpen then Left(s"Failed to extract start year from ${rawShow}") else Right(rawShow.substring(bracketOpen + 1, dash).trim)
      startYearInt <- startYearString.toIntOption.toRight(s"Start year is invalid for ${rawShow}")
    } yield startYearInt

  def extractEnd(rawShow: String): Either[String, Int] =
    val dash = rawShow.indexOf('-')
    val bracketClose = rawShow.indexOf(')')
    for {
      endYearString <- if dash == -1 || bracketClose == -1 || dash > bracketClose then Left(s"Failed to extract end year for ${rawShow}") else Right(rawShow.substring(dash + 1, bracketClose).trim)
      endYearInt <- endYearString.toIntOption.toRight(s"End year is invalid for ${rawShow}")
    } yield endYearInt

  def extractSingleYear(rawShow: String): Either[String, Int] =
    val dash = rawShow.indexOf('-')
    val bracketOpen = rawShow.indexOf('(')
    val bracketClose = rawShow.indexOf(')')
    for {
      yearString <- if dash == -1 && bracketClose >= bracketOpen + 2 then Right(rawShow.substring(bracketOpen + 1, bracketClose).trim) else Left(s"Failed to extract single year for ${rawShow}")
      year <- yearString.toIntOption.toRight(s"Single year is invalid for ${rawShow}")
    } yield year

  // Supported Format strings
  // NAME (START - END)
  // NAME (YEAR)
  def parseShow(show: String): Either[String, TvShow] =
    for {
      title <- extractName(show)
      start <- extractStart(show).orElse(extractSingleYear(show))
      end <- extractEnd(show).orElse(extractSingleYear(show))
    } yield TvShow(title, start, end)

  def addOrResign(parsedShows: Either[String, List[TvShow]], newParsedShow: Either[String, TvShow]): Either[String, List[TvShow]] =
    for {
      validatedParsedShows <- parsedShows
      validatedNewParsedShow <- newParsedShow
    } yield validatedParsedShows.appended(validatedNewParsedShow)

  //////////
  // Tests//
  //////////
  val shows = List(
    TvShow("Breaking Bad", 2008, 2013),
    TvShow("The Wire", 2002, 2008),
    TvShow("Mad Men", 2007, 2015)
  )
  assert(
    sortShows(shows) ==
      List(
        TvShow("Mad Men", 2007, 2015),
        TvShow("The Wire", 2002, 2008),
        TvShow("Breaking Bad", 2008, 2013)
      )
  )

  // Test parseShow
  assert(parseShow("Breaking Bad (2008-2013)") == Right(TvShow("Breaking Bad", 2008, 2013)))

  val rawShows = List(
    "Breaking Bad (2008 - 2013)",
    "The Wire (2002 - 2008)",
    "Mad Men (2007 - 2015)"
  )
  assert(parseShowsBestEffort(rawShows) ==
    List(
      TvShow("Breaking Bad", 2008, 2013),
      TvShow("The Wire", 2002, 2008),
      TvShow("Mad Men", 2007, 2015)
    )
  )

  assert(parseShow("Stranger Things (2016-)") == Left("Failed to extract single year for Stranger Things (2016-)"))
  assert(parseShow("Chernobyl (2019)") == Right(TvShow("Chernobyl", 2019, 2019)))
  assert(parseShow("Chernobyl (-2019)") == Left("Failed to extract single year for Chernobyl (-2019)"))

  assert(parseShow("A (1992-") == Left("Failed to extract single year for A (1992-"))
  assert(parseShow("B (-2012") == Left("Failed to extract single year for B (-2012"))
  assert(parseShow("2012") == Left("Failed to extract title for 2012"))
  assert(parseShow("C (-)") == Left("Failed to extract single year for C (-)"))

  // All or nothing error-handling
  assert(parseShowsAllOrNothing(rawShows) ==
    Right(List(
      TvShow("Breaking Bad", 2008, 2013),
      TvShow("The Wire", 2002, 2008),
      TvShow("Mad Men", 2007, 2015)
    ))
  )

  assert(parseShowsAllOrNothing(rawShows.appended("Invalid")) == Left("Failed to extract title for Invalid"))
}
