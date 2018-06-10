package net.surguy.reindexer

import java.math.{BigInteger, MathContext, RoundingMode}

/**
  * Read terms from an index file, and identify the page numbers associated with them.
  */
class IndexReader {



  /**
    * Convert a line like "apparatus, 43, 65" to an IndexEntry.
    */
  def readLine(indexLine: String): IndexEntry = {
    val items = indexLine.split(",").map(_.trim).filterNot(_.isEmpty)
    val term = items.head
    val pages = items.tail.flatMap(toPages)
    IndexEntry(term, pages)
  }

  /** Convert a page reference like "53" or "59-60" or "23-4" or "100-1" to a sequence of page numbers. */
  private[reindexer] def toPages(pageRef: String): Seq[Int] = {
    val splitRef = pageRef.split("-").toList
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

case class IndexEntry(term: String, pages: Seq[Int])