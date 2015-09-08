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
package info.bioinfweb.libralign.model.implementations.translation;


import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Shows the underlying nucleotide data source as a DNA sequence, i.e. replaces all thymine tokens 
 * by uracil tokens.
 * 
 * @author Ben St&ouml;ver
 * @since 0.1.0
 */
public class DNASequenceDataView extends SameTokenTypeAlignmentModelView<Character> {
	private static final char THYMINE = 'T';
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param underlyingProvider - the underlying provider to be viewed
	 */
	public DNASequenceDataView(AlignmentModel<Character> underlyingProvider) {
		super(underlyingProvider);
		getTokenSet().add(THYMINE);  // Just to be sure.
	}

	
	@Override
	public Character translateToken(Character token) {
		if ((token != null) && token.equals('U')) {
			return THYMINE;
		}
		else {
			return token;
		}
	}
}
