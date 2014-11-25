/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.content;




public class AlignmentContentAreaTools {
	public static void assignSequenceAreaSize(SequenceAreaMap sequenceAreaMap, int sequenceID) throws IllegalArgumentException {
		SequenceArea area = sequenceAreaMap.get(sequenceID);
		if (area != null) {
			area.assignSize();
		}
		else {
			throw new IllegalArgumentException("No sequence area for the ID " + sequenceID + " was found.");
		}
	}
}