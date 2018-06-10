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

  "identifying context" should {
    import pageMatcher.findContext
    val text = "The quick brown fox jumps over the lazy dog. It sings the song of happy chickens then the fox eats all the tasty hens. Om nom."
    def split(s: String) = s.split(" ").toList
    "identify nearby words" in { findContext("jumps", text, 3) mustEqual split("quick brown fox jumps over the lazy") }
    "identify nearby words regardless of term case" in { findContext("Jumps", text, 3) mustEqual split("quick brown fox jumps over the lazy") }
    "ignore punctuation" in { findContext("dog", text, 3) mustEqual split("over the lazy dog It sings the") }
    "stop at the beginning of a page" in { findContext("quick", text, 3) mustEqual split("The quick brown fox jumps") }
    "stop at the end of a page" in { findContext("Om", text, 3) mustEqual split("the tasty hens Om nom") }
  }

}
