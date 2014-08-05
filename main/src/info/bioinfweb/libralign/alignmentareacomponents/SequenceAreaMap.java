/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentareacomponents;


import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;

import java.util.Iterator;
import java.util.TreeMap;



/**
 * Manages the {@link SequenceArea}s that are contained in an implementation of 
 * {@link ToolkitSpecificAlignmentOverviewArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SequenceAreaMap extends TreeMap<Integer, SequenceArea> {
	private AlignmentArea owner;

	
	public SequenceAreaMap(AlignmentArea owner) {
		super();
		this.owner = owner;
		recreateElements();
	}


	public AlignmentArea getOwner() {
		return owner;
	}
	
	
	/**
	 * Clears the current contents of the map and creates a new instance for each sequence currently 
	 * contained in the associated {@link SequenceDataProvider} (defined by {@link #getOwner()}) object.
	 */
	public void recreateElements() {
		clear();
		if (getOwner().hasSequenceProvider()) {
			SequenceDataProvider provider = getOwner().getSequenceProvider();
			Iterator<Integer> iterator = provider.sequenceIDIterator();
			while (iterator.hasNext()) {
				Integer id = iterator.next();
				put(id, new SequenceArea(getOwner(), id));
			}
		}
	}
}
