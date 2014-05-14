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
package info.bioinfweb.libralign.sequenceprovider;



/**
 * This interface should be implemented by all classes which return the contents of another implementation
 * of {@link SequenceDataProvider} in a modified way.
 * <p>
 * An example would be the translation from nucleotide sequences to amino acid sequences.
 * 
 * @author Ben St&ouml;ver
 *
 * @param <T> - the type of sequence elements (tokens) the implementing and underlying provider objects 
 *        work with
 */
public interface SequenceDataProviderView<T> extends SequenceDataProvider<T> {
	/**
	 * Returns underlying provider instance that returns the unmodified data.
	 * 
	 * @return another sequence data provider object
	 */
	public SequenceDataProvider<T> getUnderlyingProvider();
}
