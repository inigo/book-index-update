package net.surguy.reindexer

import java.io.File

import org.specs2.mutable.Specification

class IndexUpdaterIntegrationTest extends Specification {

  val oldFile = new File("src/test/resources/ReportWriting1e.pdf")
  val newFile = new File("src/test/resources/ReportWriting2e.pdf")
  val indexFile = new File("src/test/resources/index.txt")

  // This requires copyrighted documents, that are not checked in to Github, so the test is skipped
  args(skipAll = !(oldFile.exists() && newFile.exists()))

  "converting a book" should {
    "produce an updated index" in {
      val newIndex = new IndexUpdater().updateIndex(oldFile, -10, newFile, -10, indexFile)
      newIndex must haveLength(greaterThan(10))
    }
  }

}
