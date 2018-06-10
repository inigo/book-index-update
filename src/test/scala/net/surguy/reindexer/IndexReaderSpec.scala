package net.surguy.reindexer

import org.specs2.mutable.Specification

class IndexReaderSpec extends Specification {

  val indexReader = new IndexReader()

  "converting index entries" should {
    import indexReader.readLine
    "convert a single simple number" in { readLine("fish, 53") mustEqual IndexEntry("fish", Seq(53)) }
    "convert multiple simple numbers" in { readLine("cat, 53, 67, 89") mustEqual IndexEntry("cat", Seq(53, 67, 89)) }
    "convert multiple complex numbers" in { readLine("dog, 12, 15-7, 89-90") mustEqual IndexEntry("dog", Seq(12, 15, 16, 17, 89, 90)) }
    "convert terms containing commas" in { readLine("cats, colours of, 16–19, 12") mustEqual IndexEntry("colours of cats", Seq(16, 17, 18, 19, 12)) }
  }

  "converting page references" should {
    import indexReader.toPages
    "convert simple numbers" in { toPages("45") mustEqual Seq(45) }
    "convert full numbers with hyphens" in { toPages("49-51") mustEqual Seq(49,50,51) }
    "convert partial numbers with hyphens" in { toPages("42-3") mustEqual Seq(42,43) }
    "convert longer partial numbers with hyphens" in { toPages("100-2") mustEqual Seq(100,101,102) }
  }

  "truncating integers" should {
    "keep the specified significant figures" in { indexReader.truncate(43, 1) mustEqual 40 }
    "always round down" in { indexReader.truncate(47, 1) mustEqual 40 }
  }

}
