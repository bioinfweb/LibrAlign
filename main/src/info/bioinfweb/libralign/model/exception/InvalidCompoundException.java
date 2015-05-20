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
package info.bioinfweb.libralign.model.exception;


import org.biojava3.core.sequence.template.Compound;

import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * This exceptions is thrown if a compound is used with {@link AlignmentModel} which does not
 * match the data type of the provide (e.g. trying to write an amino acid to a nucleotide data source). 
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class InvalidCompoundException extends AlignmentDataProviderException {
	//TODO Is this class still needed?
	public InvalidCompoundException(AlignmentModel<?> source, Compound compound) {
		
		super(source, "The compound " + compound.getLongName() + " of type " + compound.getClass().getName() + 
				" cannot does not match the data type of the provider.");
	}
}
