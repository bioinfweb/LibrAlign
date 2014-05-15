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
 * Classes implementing this interface provide access to the data provided by an implementation of
 * {@link SequenceDataProvider} in a modified way.
 * <p>
 * There are a number of more specialized subinterfaces which might be more suitable for many cases.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the token type used by the underlying provider
 */
public interface BasicSequenceDataView<T> {
	/**
	 * Returns the underlying sequence data provider that provides the data accessed with this object.
	 * 
	 * @return the underlying sequence data provider implementation
	 */
	public SequenceDataProvider<T> getUnderlyingProvider();
}
