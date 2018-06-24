package net.surguy.reindexer

import java.io.File

import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.specs2.mutable.Specification

class PdfPageReaderSpec extends Specification {

  val pageReader = new PdfPageReader()
  val testFile = new File("src/test/resources/ReportWriting2e.pdf")

  args(skipAll = !testFile.exists())

  "reading pages from a PDF" should {
    "find multiple pages" in {
      pageReader.readPages(testFile, 0) must haveLength(greaterThan(10))
    }
    "find the correct text on the first page" in {
      pageReader.readPages(testFile, 0)(1).contents must contain("POCKET")
    }
    "retrieve text that spans multiple lines" in {
      pageReader.readPages(testFile, 0)(1).contents must contain("Each guide focuses on a single crucial aspect")
    }
  }

  "removing text" should {
    "remove the header" in {
      val page12 = pageReader.readPages(testFile, 0, new PDRectangle(0, 285, 1000, 550-285))(11).contents
      page12 must contain("Reports normally have a brief")
      page12 must contain("from reading your brief carefully")
      page12 must not(contain("NOT FOR DISTRIBUTION"))
      page12 must not(contain(" 2 REPORT"))
      page12 must not(contain("02_cha01_2pp"))
      page12 must not(contain("7:20 PM"))
    }
    "remove a different set of data for another PDF" in {
      val testFile = new File("src/test/resources/ReportWriting1e.pdf")
      val page9 = pageReader.readPages(testFile, 0, new PDRectangle(0, 0, 1000, 315))(8).contents
      page9 must contain("This book demonstrates")
      page9 must not(contain("Introduction"))
      page9 must not(contain("10:27"))
    }
  }

}
