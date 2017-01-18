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
 * Base class of all exceptions that can be thrown by an {@link AlignmentModel}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class AlignmentModelException extends RuntimeException {
	private AlignmentModel<?> source;
	
	
	public AlignmentModelException(AlignmentModel<?> source) {
		super();
		this.source = source;
	}

	
	public AlignmentModelException(AlignmentModel<?> source, String message) {
		super(message);
		this.source = source;
	}


	/**
	 * Returns the instance of {@link AlignmentModel} that threw this exception.
	 */
	public AlignmentModel<?> getSource() {
		return source;
	}
}
