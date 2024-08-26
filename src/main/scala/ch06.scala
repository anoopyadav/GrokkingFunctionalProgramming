object ch06 extends App {
  case class TvShow(title: String, start: Int, end: Int)

  def sortShows(shows: List[TvShow]): List[TvShow] = {
    shows.sortBy(show => show.end - show.start).reverse
  }

  def parseShowsBestEffort(shows: List[String]): List[TvShow] =
    shows.map(parseShow).flatMap(_.toList)

  def parseShowsAllOrNothing(shows: List[String]): Option[List[TvShow]] =
    val initialResult = Some(List.empty)
    shows.map(parseShow).foldLeft(initialResult)(addOrResign)

  def extractName(rawShow: String): Option[String] =
    val bracketOpen = rawShow.indexOf('(')
    if bracketOpen == -1 then None else Some(rawShow.substring(0, bracketOpen).trim)

  def extractStart(rawShow: String): Option[Int] =
    val bracketOpen = rawShow.indexOf('(')
    val dash = rawShow.indexOf('-')
    for {
      startYearString <- if bracketOpen == -1 || dash == -1 || dash < bracketOpen then None else Some(rawShow.substring(bracketOpen + 1, dash).trim)
      startYearInt <- startYearString.toIntOption
    } yield startYearInt

  def extractEnd(rawShow: String): Option[Int] =
    val dash = rawShow.indexOf('-')
    val bracketClose = rawShow.indexOf(')')
    for {
      endYearString <- if dash == -1 || bracketClose == -1 || dash > bracketClose then None else Some(rawShow.substring(dash + 1, bracketClose).trim)
      endYearInt <- endYearString.toIntOption
    } yield endYearInt

  def extractSingleYear(rawShow: String): Option[Int] =
    val dash = rawShow.indexOf('-')
    val bracketOpen = rawShow.indexOf('(')
    val bracketClose = rawShow.indexOf(')')
    for {
      yearString <- if dash == -1 && bracketClose >= bracketOpen + 2 then Some(rawShow.substring(bracketOpen + 1, bracketClose).trim) else None
      year <- yearString.toIntOption
    } yield year

  // Supported Format strings
  // NAME (START - END)
  // NAME (YEAR)
  def parseShow(show: String): Option[TvShow] =
    for {
      title <- extractName(show)
      start <- extractStart(show).orElse(extractSingleYear(show))
      end <- extractEnd(show).orElse(extractSingleYear(show))
    } yield TvShow(title, start, end)

  def addOrResign(parsedShows: Option[List[TvShow]], newParsedShow: Option[TvShow]): Option[List[TvShow]] =
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
  assert(parseShow("Breaking Bad (2008-2013)") == Option(TvShow("Breaking Bad", 2008, 2013)))

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

  assert(parseShow("Stranger Things (2016-)") == None)
  assert(parseShow("Chernobyl (2019)") == Option(TvShow("Chernobyl", 2019, 2019)))
  assert(parseShow("Chernobyl (-2019)") == None)

  assert(parseShow("A (1992-") == None)
  assert(parseShow("B (-2012") == None)
  assert(parseShow("2012") == None)
  assert(parseShow("C (-)") == None)

  // All or nothing error-handling
  assert(parseShowsAllOrNothing(rawShows) ==
    Some(List(
      TvShow("Breaking Bad", 2008, 2013),
      TvShow("The Wire", 2002, 2008),
      TvShow("Mad Men", 2007, 2015)
    ))
  )

  assert(parseShowsAllOrNothing(rawShows.appended("Invalid")) == None)
}
