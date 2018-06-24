package net.surguy.reindexer

import java.io.File

import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.text.{PDFTextStripper, TextPosition}


/**
  * Read a PDF into individual pages.
  */
class PdfPageReader {

  def readPages(pdf: File, pageNumberOffset: Int, boundingRect: PDRectangle = new PDRectangle(0,0, 10000, 10000)): List[Page] = {
    val pdfFile = new RandomAccessFile(pdf, "r")

    val parser = new PDFParser(pdfFile)
    parser.parse()

    val pdDoc = new PDDocument(parser.getDocument)
    val stripper = new ClippingTextStripper(boundingRect)

    val pages = for (page <- 1 to pdDoc.getNumberOfPages) yield {
      stripper.setStartPage(page)
      stripper.setEndPage(page)
      val text = stripper.getText(pdDoc)
                    .replaceAll("\\n"," ")
                    .replaceAll("\\s+"," ")
                    .trim
      val pageNumber = calculatePageNumber(page, pageNumberOffset)
      Page(pageNumber, text)
    }
    pdDoc.close()
    pdfFile.close()

    pages.toList
  }

  /**
    * The page offset in the PDF is not necessarily the same as the page number in the index.
    */
  private def calculatePageNumber(pagePosition: Int, pageNumberOffset: Int): Int = {
    pagePosition + pageNumberOffset
  }

}

/**
  * Ignore text that's outside a bounding box - typically there are things like page numbers that should be ignored.
  */
class ClippingTextStripper(bounds: PDRectangle) extends PDFTextStripper {
  override def processTextPosition(text: TextPosition): Unit = {
//    println(s"X: ${text.getX} Y: ${text.getY} Text: ${text.toString}")
    if (bounds.contains(text.getX, text.getY)) {
      super.processTextPosition(text)
    }
  }
}