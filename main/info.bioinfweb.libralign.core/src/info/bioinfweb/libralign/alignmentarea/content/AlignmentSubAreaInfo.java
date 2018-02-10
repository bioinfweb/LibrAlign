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
package info.bioinfweb.libralign.alignmentarea.content;



/**
 * Stores a reference to an {@link AlignmentSubArea} and its y position relative to its parent 
 * {@link AlignmentContentArea}. Instances of this class are used by 
 * {@link AlignmentContentArea#getAreaInfoByPaintY(double)} to return both values together.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class AlignmentSubAreaInfo {
	private AlignmentSubArea area;
	private double y;
	
	
	public AlignmentSubAreaInfo(AlignmentSubArea area, double y) {
		super();
		this.area = area;
		this.y = y;
	}
	
	
	public AlignmentSubArea getArea() {
		return area;
	}
	
	
	public double getY() {
		return y;
	}
}
