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
package info.bioinfweb.libralign;


import java.util.ArrayList;
import java.util.List;

import info.bioinfweb.libralign.dataarea.implementations.SequenceIndexArea;



/**
 * This class allows to use multiple instances of {@link AlignmentArea}, that are coupled related to horizontal
 * scrolling. This can e.g. be used to display a data area with heading information (e.g. {@link SequenceIndexArea}) 
 * which always remains visible independently of the vertical scrolling of the alignment it belongs to. (To achieve this
 * you would add to alignment areas to an instance of this class, one containing the heading data area and the other one
 * containing the alignment.)
 * <p>
 * Note that it makes only sense to combine alignment areas that display related information and therefore have an
 * equal number of according columns.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class MultipleAlignmentsContainer {
	private List<AlignmentArea> alignmentAreas = new ArrayList<AlignmentArea>();

	
	public boolean addArea(AlignmentArea e) {
		return alignmentAreas.add(e);
	}
	

	public void addArea(int index, AlignmentArea element) {
		alignmentAreas.add(index, element);
	}

	
	public boolean containsArea(AlignmentArea area) {
		return alignmentAreas.contains(area);
	}

	
	public AlignmentArea getArea(int index) {
		return alignmentAreas.get(index);
	}

	
	public int indexOfArea(Object o) {
		return alignmentAreas.indexOf(o);
	}

	
	public AlignmentArea removeArea(int index) {
		return alignmentAreas.remove(index);
	}

	
	public boolean removeArea(AlignmentArea area) {
		return alignmentAreas.remove(area);
	}
	

	public int areaCount() {
		return alignmentAreas.size();
	}
}
