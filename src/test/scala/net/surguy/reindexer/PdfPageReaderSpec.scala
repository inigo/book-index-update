package net.surguy.reindexer

import java.io.File

import org.specs2.mutable.Specification

class PdfPageReaderSpec extends Specification {

  val pageReader = new PdfPageReader()
  val testFile = new File("src/test/resources/ReportWriting2e.pdf")

  "reading pages from a PDF" should {
    "find multiple pages" in {
      pageReader.readPages(testFile, 0) must haveLength(greaterThan(10))
    }
    "find the correct text on the first page" in {
      pageReader.readPages(testFile, 0).head.contents must contain("This file is to be used only for a purpose specified by")
    }
    "retrieve text that spans multiple lines" in {
      pageReader.readPages(testFile, 0)(1).contents must contain("Each guide focuses on a single crucial aspect")
    }
  }

}
