/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.model.exception;


import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * This exceptions is thrown if a sequence provided by an implementation of {@link AlignmentModel} 
 * shall be renamed and the a sequence with the new name is already present in the underlying data source.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class DuplicateSequenceNameException extends AlignmentModelException {
	private String name;
	
	
	/**
	 * Create a new instance of this class.
	 * 
	 * @param source - the sequence provider where this exception happened
	 * @param name - the duplicate sequence name
	 */
	public DuplicateSequenceNameException(AlignmentModel<?> source, String name) {
		super(source, "A sequence with the name \"" + name + 
				"\" is already contained in the underlying data source.");
		this.name = name;
	}


	/**
	 * Returns the duplicate name.
	 */
	public String getDuplicateName() {
		return name;
	}
}
