package net.surguy.reindexer

import java.io.File
import java.math.{MathContext, RoundingMode}

import scala.io.Source

/**
  * Read terms from an index file, and identify the page numbers associated with them.
  */
class IndexReader {

  /** Read all lines (containing numbers) from an index file (omits entries like 'see xxx') */
  def readAllEntries(indexFile: File): Seq[IndexEntry] = {
    (for (line <- Source.fromFile(indexFile, "UTF-8").getLines(); if containsNumbers(line)) yield {
      readLine(line)
    }).toList
  }

  /**
    * Convert a line like "apparatus, 43, 65" to an IndexEntry.
    */
  def readLine(indexLine: String): IndexEntry = {
    val items = indexLine.split(",").map(_.trim).filterNot(_.isEmpty)
    val isDoubledTerm = items.length>1 && !containsNumbers(items(1))
    val term = if (isDoubledTerm) items(1)+" "+items(0) else items(0)
    val pages = items.tail.filter(containsNumbers).flatMap(toPages)
    IndexEntry(term, pages)
  }

  private def containsNumbers(s: String) = s.matches("^.*?[0-9].*$")

  /** Convert a page reference like "53" or "59-60" or "23-4" or "100-1" to a sequence of page numbers. */
  private[reindexer] def toPages(pageRef: String): Seq[Int] = {
    val splitRef = pageRef.split("[-–]").toList
    splitRef match {
      case List(a) if pageRef.matches("[0-9]+") => Seq(pageRef.toInt)
      case List(a,b) if b.length >= a.length => a.toInt to b.toInt
      case List(a,b) if b.length < a.length => a.toInt to truncate(a.toInt, a.length - b.length) + b.toInt
      case _ => throw new IllegalArgumentException(s"Unexpected page reference format '$pageRef")
    }
  }

  /** Truncate an integer to some number of significant figures - e.g. 123 to 120 or 100. */
  private[reindexer] def truncate(n: Int, sigFigs: Int): Int = BigDecimal(n).round(new MathContext(sigFigs, RoundingMode.FLOOR)).intValue()

}

case class IndexEntry(term: String, pages: Seq[Int]) {
  override def toString: String = term + pages.map(p => ", "+p).mkString
}