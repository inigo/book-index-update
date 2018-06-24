package net.surguy.reindexer

import java.io.File

import org.apache.pdfbox.pdmodel.common.PDRectangle

/**
  * Convert the index for the first edition of a book to be correct for the second edition of the book.
  */
class IndexUpdater {

  def updateIndex(oldPdf: File, oldOffset: Int, oldBoundingBox: PDRectangle,
                  newPdf: File, newOffset: Int, newBoundingBox: PDRectangle,
                  indexTextFile: File): Seq[IndexEntry] = {
    val pdfReader = new PdfPageReader()
    val oldPages = pdfReader.readPages(oldPdf, oldOffset, oldBoundingBox)
    val newPages = pdfReader.readPages(newPdf, newOffset, newBoundingBox)

    val pageMatcher = new PageMatcher(oldPages, newPages)

    val entries = new IndexReader().readAllEntries(indexTextFile)
    val newEntries = for (entry <- entries) yield {
      val newPages: Seq[Option[Int]] = for (oldPage <- entry.pages) yield pageMatcher.findNewIndex(entry.term, oldPage)
      IndexEntry(entry.term, newPages.map(_.getOrElse(-1)))
    }
    newEntries.foreach( println )
    newEntries
  }


}
