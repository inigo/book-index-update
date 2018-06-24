package net.surguy.reindexer

import java.io.File

import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.specs2.mutable.Specification

class IndexUpdaterIntegrationTest extends Specification {

  val oldFile = new File("src/test/resources/ReportWriting1e.pdf")
  val newFile = new File("src/test/resources/ReportWriting2e.pdf")
  val indexFile = new File("src/test/resources/index.txt")

  // This requires copyrighted documents, that are not checked in to Github, so the test is skipped
  args(skipAll = !(oldFile.exists() && newFile.exists()))

  "converting a book" should {
    "produce an updated index" in {
      val newIndex: Seq[IndexEntry] = new IndexUpdater().updateIndex(oldFile, -10, new PDRectangle(0, 0, 1000, 315),
                                                                     newFile, -10, new PDRectangle(0, 285, 1000, 550-285),
                                                                     indexFile)
      newIndex must haveLength(greaterThan(10))

      val matches = newIndex.map(ie => ie.pages.count(_ != -1) ).sum
      println("Matches are "+matches)
      matches must beGreaterThanOrEqualTo(221) // Increase this as we get more matches
    }
  }

}
