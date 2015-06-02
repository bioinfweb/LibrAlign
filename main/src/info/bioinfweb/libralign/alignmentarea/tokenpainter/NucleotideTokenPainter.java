/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.commons.bio.SequenceUtils;

import java.awt.Color;



/**
 * Token painter that paints nucleotides.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class NucleotideTokenPainter extends AbstractBioPolymerTokenPainter {
	/**
	 * Creates a new instance of this class.
	 */
	public NucleotideTokenPainter() {
		super();
		putNucleotideColors();
	}
	
	
	private void putNucleotideColor(String nucleotide, Color color) {
		getBackgroundColorMap().put(nucleotide.toUpperCase(), color);
		getBackgroundColorMap().put(nucleotide.toLowerCase(), color);
	}
	
	
	protected void putNucleotideColors() {
		Color colorTU = new Color(230, 90, 90);
		putNucleotideColor("A", new Color(90, 228, 93));
		putNucleotideColor("T", colorTU);
		putNucleotideColor("U", colorTU);
		putNucleotideColor("C", new Color(90, 90, 230));
		putNucleotideColor("G", new Color(226, 230, 90));
		putNucleotideColor("-", Color.GRAY);
		putNucleotideColor("?", Color.GRAY.brighter());
	}
	
	
	@Override
	protected boolean isAmbiguity(String tokenRepresentation) {
		return (tokenRepresentation.length() == 1) && 
				SequenceUtils.isNucleotideAmbuguityCode(tokenRepresentation.charAt(0));
	}


	@Override
	protected char[] calculateConstituents(String tokenRepresentation) {
		return SequenceUtils.nucleotideConstituents(tokenRepresentation.charAt(0));
	}
}
