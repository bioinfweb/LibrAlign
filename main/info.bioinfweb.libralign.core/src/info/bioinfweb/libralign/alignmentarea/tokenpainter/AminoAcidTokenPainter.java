/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.tokenpainter;


import info.bioinfweb.commons.bio.SequenceUtils;

import java.awt.Color;



/**
 * Token painter that paints amino acids.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class AminoAcidTokenPainter extends AbstractBioPolymerTokenPainter {
	public AminoAcidTokenPainter() {
		super();
		putAminoAcidColors();
	}
	
	
	private void putAminoAcidColor(String code1, Color color) {
		getBackgroundColorMap().put(code1.toUpperCase(), color);
		getBackgroundColorMap().put(code1.toLowerCase(), color);
		getBackgroundColorMap().put(SequenceUtils.threeLetterAminoAcidByOneLetter(code1.charAt(0)).toUpperCase(), color);
	}
	
	
	protected void putAminoAcidColors() {
		putAminoAcidColor("A", new Color(32, 255, 8));
		putAminoAcidColor("C", new Color(255, 162, 202));
		putAminoAcidColor("D", new Color(220, 134, 38));
		putAminoAcidColor("E", new Color(236, 164, 38));
		putAminoAcidColor("F", new Color(255, 0, 179));
		putAminoAcidColor("G", new Color(100, 137, 179));
		putAminoAcidColor("H", new Color(151, 60, 142));
		putAminoAcidColor("I", new Color(248, 255, 0));
		putAminoAcidColor("K", new Color(0, 196, 252));
		putAminoAcidColor("L", new Color(255, 253, 0));
		putAminoAcidColor("M", new Color(248, 247, 0));
		putAminoAcidColor("N", new Color(197, 109, 105));
		putAminoAcidColor("P", new Color(148, 83, 95));
		putAminoAcidColor("Q", new Color(255, 190, 3));
		putAminoAcidColor("R", new Color(7, 182, 255));
		putAminoAcidColor("S", new Color(0, 255, 12));
		putAminoAcidColor("T", new Color(0, 255, 59));
		putAminoAcidColor("V", new Color(254, 255, 0));
		putAminoAcidColor("W", new Color(255, 0, 29));
		putAminoAcidColor("Y", new Color(235, 0, 173));
		
		putAminoAcidColor("O", new Color(0, 196, 252).brighter());
		putAminoAcidColor("U", new Color(255, 162, 202).brighter());
	}


	@Override
	protected boolean isAmbiguity(String tokenRepresentation) {
		return SequenceUtils.isAminoAcidAmbiguityCode(tokenRepresentation);
	}


	@Override
	protected char[] calculateConstituents(String tokenRepresentation) {
		return SequenceUtils.oneLetterAminoAcidConstituents(tokenRepresentation);
	}


	@Override
	public Color backgroundColorByRepresentation(String tokenRepresentation, Color selectionColor) {
		if (tokenRepresentation.length() == 3) {  // Handle 3 letter amino acid codes.
			tokenRepresentation = tokenRepresentation.toUpperCase();
		}
		return super.backgroundColorByRepresentation(tokenRepresentation, selectionColor);
	}
}
