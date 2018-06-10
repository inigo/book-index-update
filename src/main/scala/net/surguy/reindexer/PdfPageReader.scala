package net.surguy.reindexer

import java.io.File

import org.apache.pdfbox.io.RandomAccessFile
import org.apache.pdfbox.pdfparser.PDFParser
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper


/**
  * Read a PDF into individual pages.
  */
class PdfPageReader {

  def readPages(pdf: File, pageNumberOffset: Int): List[Page] = {
    val pdfFile = new RandomAccessFile(pdf, "r")

    val parser = new PDFParser(pdfFile)
    parser.parse()

    val pdDoc = new PDDocument(parser.getDocument)
    val stripper = new PDFTextStripper()

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

    // @todo Remove repeated header / footer text
    pages.toList
  }

  /**
    * The page offset in the PDF is not necessarily the same as the page number in the index.
    */
  private def calculatePageNumber(pagePosition: Int, pageNumberOffset: Int): Int = {
    pagePosition + pageNumberOffset
  }

}
