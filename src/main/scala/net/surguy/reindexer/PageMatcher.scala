package net.surguy.reindexer

/**
  * Identify the matching pages.
  */
class PageMatcher(oldVersion: List[Page], newVersion: List[Page]) {

  def findNewIndex(indexTerm: String, originalPageNumber: Int): Option[Int] = {
    val originalPage = oldVersion.find(_.pageNumber == originalPageNumber)
      .getOrElse(throw new IllegalArgumentException("Cannot find original page number "+originalPageNumber))
    val context = findContext(indexTerm, originalPage.contents)

    val startSearchAt = originalPageNumber
    findMatchingPage(indexTerm, context, startSearchAt, newVersion)
  }

  /** Return the words in the page close to the original term, for example the full sentence it appears in */
  private[reindexer] def findContext(term: String, pageText: String): List[String] = {
    // @todo Do we want to pas in the previous and next page, because the sentence may be split across pages?
    val position = pageText.toLowerCase.indexOf(term.toLowerCase)
    ???
  }

  /** Find the page that a term is on. */
  private[reindexer] def findMatchingPage(indexTerm: String, context: List[String], startSearchAt: Int, pages: List[Page]): Option[Int] = {
    val pagesToCheck: Seq[Int] = identifyPagesToCheck(startSearchAt, pages.length)
    pagesToCheck.find(i => checkPage(pages(i).contents, indexTerm, context))
  }

  /** Return a list of the pages to check, in order of likelihood - for example 6, 7, 5, 8, 4 if the term was on page 6 originally. */
  private[reindexer] def identifyPagesToCheck(startSearchAt: Int, pageCount: Int, maxDistance: Int = 4): Seq[Int] = {
    val ascending = startSearchAt to Math.min(startSearchAt + maxDistance, pageCount)
    val descending = (startSearchAt - 1).to(1, -1)
    ascending.zipAll(descending,0,0).flatMap { case (a, b) => Seq(a, b) }.filter(_ > 0)
  }

  /** Identify if a term is on a page, with a close-enough match for the surrounding context.  */
  private[reindexer] def checkPage(contents: String, indexTerm: String, context: List[String]): Boolean = ???


}
