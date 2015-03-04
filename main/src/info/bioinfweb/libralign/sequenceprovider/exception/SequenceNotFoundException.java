/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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
 * This exceptions is thrown if a requested sequence provided by an implementation of 
 * {@link SequenceDataProvider} was not found in the underlying data source.
 * <p>
 * Note that not all methods of {@link SequenceDataProvider} throw this exception. Some indicate
 * the same thing also by their return value. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class SequenceNotFoundException extends AlignmentDataProviderException {
	/**
	 * Create a new instance of this class.
	 * 
	 * @param source - the sequence provider where this exception happened
	 */
	public SequenceNotFoundException(SequenceDataProvider<?> source) {
		super(source, "The requested sequence was not found in the underlying data source.");
	}
}
