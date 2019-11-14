/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataelement;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;



/**
 * Enumerates the types locations a {@link DataArea} can have inside a {@link AlignmentArea}. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public enum DataListType {
	/** Indicates that the associated data area is located above the alignment. */
  TOP,
  
	/** Indicates that the associated data area is located underneath the alignment. */
  BOTTOM,

  /** Indicates that the associated data area is associated with a sequence in the alignment. */
  SEQUENCE;
}
