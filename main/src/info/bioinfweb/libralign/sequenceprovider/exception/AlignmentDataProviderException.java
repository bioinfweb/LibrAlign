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
package info.bioinfweb.libralign.sequenceprovider.exception;


import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * Base class of all exceptions that can be thrown by an {@link SequenceDataProvider}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class AlignmentDataProviderException extends RuntimeException {
	private SequenceDataProvider<?> source;
	
	
	public AlignmentDataProviderException(SequenceDataProvider<?> source) {
		super();
		this.source = source;
	}

	
	public AlignmentDataProviderException(SequenceDataProvider<?> source, String message) {
		super(message);
		this.source = source;
	}


	/**
	 * Returns the instance of {@link SequenceDataProvider} that threw this exception.
	 */
	public SequenceDataProvider<?> getSource() {
		return source;
	}
}
