package net.surguy.reindexer

import java.text.BreakIterator

import scala.language.implicitConversions

/**
  * Utilities for manipulating words within text.
  *
  * @author Inigo Surguy
  */
//noinspection TypeAnnotation
object WordUtils {
  /** Extends string with methods to get sentences, words, and lines, using the default locale. */
  implicit def StringWithBreaks(s: String) = new {
    def words: Iterator[String] = new BreakIt(removePeriods(s), BreakIterator.getWordInstance).filter(word => word.text(0).isLetterOrDigit).map(_.text)
  }

  /* Periods are not counted as word breaks, whereas semi-colons are treated as word breaks. */
  private def removePeriods(s: String) = s.replace('.', ';')

  /**
    * Convert a Java BreakIterator into a Scala iterator (note that the BreakIterator is not
    * actually a Java Iterator).
    * By Rex Kerr, taken from http://comments.gmane.org/gmane.comp.lang.scala.user/23536
    */
  private class BreakIt(target: String, bi: BreakIterator) extends Iterator[StringWithPosition] {
    bi.setText(target)
    private var start = bi.first
    private var end = bi.next

    def hasNext: Boolean = end != BreakIterator.DONE

    def next(): StringWithPosition = {
      val result = target.substring(start, end)

      val stringWithPosition = StringWithPosition(result, start, end)

      start = end
      end = bi.next

      stringWithPosition
    }
  }

  case class StringWithPosition(text: String, start: Int, end: Int)
}
