object ch05_events extends App {
  case class Event(name: String, start: Int, end: Int)

  def validateName(name: String): Option[String] = if name.nonEmpty then Some(name) else None

  def validateStart(start: Int, end: Int): Option[Int] = if start <= end then Some(start) else None

  def validateEnd(end: Int): Option[Int] = if end < 3000 then Some(end) else None

  def validateDuration(start: Int, end: Int, duration: Int): Option[Int] = if end - start >= duration then Some(duration) else None

  def parseEvent(name: String, start: Int, end: Int): Option[Event] = {
    for {
      name <- validateName(name)
      start <- validateStart(start, end)
      end <- validateEnd(end)
    } yield Event(name, start, end)
  }

  def parseLongEvent(name: String, start: Int, end: Int, duration: Int): Option[Event] =
    for {
      validName <- validateName(name)
      validStart <- validateStart(start, end)
      validEnd <- validateEnd(end)
      validDuration <- validateDuration(start, end, duration)
    } yield Event(validName, validStart, validEnd)

  // Let's make some events
  assert(parseEvent("Employment", 2022, 2024) == Some(Event("Employment", 2022, 2024)))
  assert(parseEvent("", 2002, 2003) == None)

  assert(parseLongEvent("Employment", 2022, 2032, 10) == Some(Event("Employment", 2022, 2032)))
  assert(parseLongEvent("Employment", 2022, 2024, 5) == None)
}
