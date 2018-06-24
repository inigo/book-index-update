Update the index for the old edition of a book so it is correct for the new edition of a book.

---

This requires:
   
 - The old edition of the book in PDF format
 - The new edition of the book in PDF format
 - The index for the old version of the book, saved as a text file
 
To run it, edit IndexUpdaterIntegrationTest to point at these files, and run it.

Set page number offsets for each book based on the difference between the page
number at the foot of the page, and the page in the PDF that this is - typically
this will be -10 or so, depending on the number of pages of preamble. This needs 
to be correct.

This code works by finding the first occurrence of an indexed term in the page of
the old edition referenced by the index; then identifying the surrounding context
for that term in the old edition; then looking for that same term and surrounding 
context in the new edition, starting at the same page number as in the old edition
and then searching the pages near it. It works only for simple index terms that
actually appear on the referenced page, not for more complex index terms that
convey a theme or idea.


Copyright (C) 2018 Inigo Surguy

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.

## Improvements

larger boundaries - spidergrams - DONE
Synonyms - accuracy -> precise, if 
multiple word phrases - lots - DONE
repeating text - photographs
reduce thresholds if no match - planning pg 38 (or recognize headings)
ignore hyphens - proofreading/proof-reading - DONE
