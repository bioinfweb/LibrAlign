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
package info.bioinfweb.libralign.model.exception;


import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * This exceptions is thrown if a modifying method of {@link AlignmentModel} is called
 * which has a data source that is not writable.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class AlignmentSourceNotWritableException extends AlignmentModelException {
	public AlignmentSourceNotWritableException(AlignmentModel<?> source) {
		super(source, "The underlying data source is not writable.");
	}
}
