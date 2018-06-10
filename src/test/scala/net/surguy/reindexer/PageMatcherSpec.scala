package net.surguy.reindexer

import org.specs2.mutable.Specification

class PageMatcherSpec extends Specification {

  private val pageMatcher = new PageMatcher(null, null)

  "pages to check" should {
    import pageMatcher.identifyPagesToCheck
    "start with the passed in page" in { identifyPagesToCheck(4, 20).head mustEqual 4 }
    "not go below 1" in { identifyPagesToCheck(4, 20) must allOf(greaterThan(0)) }
    "not go below the page count" in { identifyPagesToCheck(4, 6) must allOf(lessThanOrEqualTo(6)) }
    "include multiple results" in { identifyPagesToCheck(4, 20) must haveLength(greaterThan(5)) }
    "not include duplicates" in { identifyPagesToCheck(4, 20).distinct mustEqual identifyPagesToCheck(4, 20) }
    "not go beyond the expected bounds" in { identifyPagesToCheck(4, 20, 5) must not(contain(10)) }
  }

}
