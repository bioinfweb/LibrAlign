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
package info.bioinfweb.libralign.alignmentarea;


import info.bioinfweb.commons.collections.observable.ListChangeListener;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataelement.DataList;
import info.bioinfweb.libralign.dataelement.DataListType;
import info.bioinfweb.libralign.dataelement.DataLists;
import info.bioinfweb.libralign.dataelement.DataLocation;



/**
 * Instances of this class are used to organize the data areas that are contained within an {@link AlignmentArea} instance.
 * 
 * @author Ben St&ouml;ver
 * @see 0.10.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class DataAreaLists extends DataLists<AlignmentArea, DataArea>{
  private final DataList<AlignmentArea, DataArea> topList;
  private final DataList<AlignmentArea, DataArea> bottomList;
  
  
	/**
	 * Creates a new instance of this class.
	 *
	 * @param owner the {@link AlignmentArea} that will be using the new instance
	 * @param listChangeListener This listener will be informed on changes in all lists contained in the new instance. Owning classes should provide an 
	 *        implementation that processes the events, e.g., by forwarding them to their own respective listeners. 
	 */
	public DataAreaLists(AlignmentArea owner, ListChangeListener<DataArea> listChangeListener) {
		super(owner, listChangeListener);

		topList = new DataList<AlignmentArea, DataArea>(this, DataListType.TOP);
		topList.addListChangeListener(listChangeListener);
		bottomList = new DataList<AlignmentArea, DataArea>(this, DataListType.BOTTOM);
		bottomList.addListChangeListener(listChangeListener);
	}

	
	/**
	 * Returns a list of data areas to be displayed on the top of the alignment.
	 *
	 * @return a modifiable list
	 */
	public DataList<AlignmentArea, DataArea> getTopList() {
		return topList;
	}


	/**
	 * Returns a list of data areas to be displayed underneath the alignment.
	 *
	 * @return a modifiable list
	 */
	public DataList<AlignmentArea, DataArea> getBottomList() {
		return bottomList;
	}
	
	
	public boolean addDataArea(DataArea area, DataLocation location) {
		return addDataArea(area, location.getListType(), location.getSequenceID());
	}
	
	
	public boolean addDataArea(DataArea area, DataListType listType, String sequenceID) {
		switch (listType) {
			case TOP:
				return getTopList().add(area);
			case BOTTOM:
				return getBottomList().add(area);
			case SEQUENCE:
				if (sequenceID == null) {
					throw new IllegalArgumentException("sequenceID must not be null if listType is SEQUENCE.");
				}
				else {
					return getSequenceList(sequenceID).add(area);
				}
			default:
				return false;
		}
	}
}
