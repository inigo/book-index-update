package net.surguy.reindexer

/**
  * Identify the matching pages.
  */
class PageMatcher(oldVersion: List[Page], newVersion: List[Page]) {
  private val CONTEXT_SIZE_IN_WORDS = 5
  private val MAX_DISTANCE_FROM_ORIGINAL_PAGE = 30
  private val CONTEXT_MATCH_THRESHOLD = 0.7

  type Context = List[String]

  def findNewIndex(indexTerm: String, originalPageNumber: Int): Option[Int] = {
    val originalPage = oldVersion.find(_.pageNumber == originalPageNumber)
      .getOrElse(throw new IllegalArgumentException("Cannot find original page number "+originalPageNumber))
    val context = findContext(indexTerm, originalPage.contents)

    val startSearchAt = originalPageNumber
    findMatchingPage(indexTerm, context, startSearchAt, newVersion)
  }

  /** Return the words in the page close to the original term, for example the full sentence it appears in */
  private[reindexer] def findContext(term: String, pageText: String, contextSize: Int = CONTEXT_SIZE_IN_WORDS): Context = {
    import WordUtils.StringWithBreaks
    val words = pageText.words.toList
    val position: Int = words.indexWhere(_.toLowerCase==term.toLowerCase)
    nearbyWords(words, position, contextSize)
  }

  private def nearbyWords(words: List[String], position: Int, contextSize: Int): Context = words.slice(position - contextSize, position + contextSize + 1)

  /** Find the page that a term is on. */
  private[reindexer] def findMatchingPage(indexTerm: String, context: Context, startSearchAt: Int, pages: List[Page]): Option[Int] = {
    val pagesToCheck: Seq[Int] = identifyPagesToCheck(startSearchAt, pages.length)
    pagesToCheck.find(i => checkPage(pages.find(_.pageNumber==i).get.contents, indexTerm, context).isDefined )
  }

  /** Return a list of the pages to check, in order of likelihood - for example 6, 7, 5, 8, 4 if the term was on page 6 originally. */
  private[reindexer] def identifyPagesToCheck(startSearchAt: Int, pageCount: Int, maxDistance: Int = MAX_DISTANCE_FROM_ORIGINAL_PAGE): Seq[Int] = {
    val ascending = startSearchAt to Math.min(startSearchAt + maxDistance, pageCount)
    val descending = (startSearchAt - 1).to(1, -1)
    ascending.zipAll(descending,0,0).flatMap { case (a, b) => Seq(a, b) }.filter(_ > 0)
  }

  /** Identify if a term is on a page, with a close-enough match for the surrounding context.  */
  private[reindexer] def checkPage(contents: String, indexTerm: String, context: Context, contextSize: Int = CONTEXT_SIZE_IN_WORDS): Option[List[String]] = {
    import WordUtils.StringWithBreaks
    val sanitizedContext = context.map(sanitize)
    val words = contents.words.toList.map(sanitize)
    val sanitizedTerm = sanitize(indexTerm)

    val potentialMatchIndices = words.zipWithIndex.filter(_._1 == sanitizedTerm).map(_._2)
    val potentialContexts: Seq[Context] = potentialMatchIndices.map(i => nearbyWords(words, i, contextSize))
    val foundMatch: Option[Context] = potentialContexts.find(pc => isCloseMatch(sanitizedContext, pc))
    foundMatch
  }

  private def sanitize(s: String) = s.toLowerCase.replaceAll("[^A-Za-z0-9]","")

  private def isCloseMatch(first: Context, second: Context):Boolean = {
    val totalWords = Math.min(first.length, second.length)
    val matchingWords = first.intersect(second).length
    (matchingWords.toFloat / totalWords.toFloat) >= CONTEXT_MATCH_THRESHOLD
  }

}
