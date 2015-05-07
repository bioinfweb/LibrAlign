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
package info.bioinfweb.libralign.alignmentmodel;



/**
 * This interface should be implemented by all classes which return the contents of another implementation
 * of {@link AlignmentModel} in a modified way but can still act as sequence data providers.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 *
 * @param <T> - the type of sequence elements (tokens) the implementing view works with
 * @param <U> - the type of sequence elements (tokens) the underlying provider works with
 */
public interface SequenceDataView<T, U> extends AlignmentModel<T>, BasicSequenceDataView<U> {}
