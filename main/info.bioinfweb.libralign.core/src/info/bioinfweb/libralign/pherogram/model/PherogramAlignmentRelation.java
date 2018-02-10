/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.pherogram.model;


import java.util.ListIterator;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.utils.indextranslation.IndexRelation;



/**
 * A bean class storing the base call sequence position corresponding to editable sequence position or the other way
 * around in an alignment between the base call sequence from Sanger sequencing and the corresponding editable sequence
 * in an {@link AlignmentArea}. 
 * 
 * @author Ben St&ouml;ver
 */
public class PherogramAlignmentRelation extends IndexRelation {
	private ListIterator<ShiftChange> iterator;
	
	
	public PherogramAlignmentRelation(int before, int corresponding, int after, ListIterator<ShiftChange> iterator) {
		super(before, corresponding, after);
		this.iterator = iterator;
	}


	/**
	 * Returns an iterator positioned before the next shift change entry. 
	 */
	public ListIterator<ShiftChange> getIterator() {
		return iterator;
	}
}
