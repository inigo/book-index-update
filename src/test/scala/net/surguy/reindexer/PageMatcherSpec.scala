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
    "identify nearby words" in { findContext("jumps", text, 3) mustEqual split("quick brown fox jumps over the lazy") }
    "identify nearby words regardless of term case" in { findContext("Jumps", text, 3) mustEqual split("quick brown fox jumps over the lazy") }
    "ignore punctuation" in { findContext("dog", text, 3) mustEqual split("over the lazy dog It sings the") }
    "stop at the beginning of a page" in { findContext("quick", text, 3) mustEqual split("The quick brown fox jumps") }
    "stop at the end of a page" in { findContext("Om", text, 3) mustEqual split("the tasty hens Om nom") }
  }

  "finding matches" should {
    import pageMatcher.checkPage
    val text = "The quick brown fox jumps over the lazy dog. It sings the song of happy chickens then the fox eats all the tasty hens. Om nom."
    "find an exact match" in { checkPage(text, "jumps", split("quick brown fox jumps over the lazy")) must beSome }
    "find an match with changed case" in { checkPage(text, "Jumps", split("quick Brown fox Jumps over the LAZY")) must beSome }
    "find an match with additional words" in { checkPage(text, "jumps", split("quickish brown fox also jumps over the lazy")) must beSome }
    "find an match with changed punctuation" in { checkPage(text, "jumps", split("quick brown fox jumps. Over the lazy!")) must beSome }
    "reject a match with missing term" in { checkPage(text, "rabbit", split("quick brown rabbit jumps over the lazy")) must beNone }
    "reject a match with changed context" in { checkPage(text, "jumps", split("my shiny horse jumps the fence regularly")) must beNone }
    "find the first match if context is correct" in { checkPage(text, "fox", split("The quick brown fox jumps over the")) must beSome }
    "find the second match if context is correct" in { checkPage(text, "fox", split("chickens then the fox eats all the tasty")) must beSome }
  }

  private def split(s: String) = s.split(" ").toList

}
