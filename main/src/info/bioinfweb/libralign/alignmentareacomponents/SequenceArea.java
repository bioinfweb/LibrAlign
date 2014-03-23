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
package info.bioinfweb.libralign.alignmentareacomponents;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Set;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.AlignmentDataViewMode;
import info.bioinfweb.libralign.AlignmentSubArea;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * The area inside an {@link AlignmentArea} that displays one sequence of the alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 1.0.0
 */
public class SequenceArea extends AlignmentSubArea {
	private int seqenceID;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will contain this instance
	 * @param seqenceID - the unique identifier of the sequence that will be displayed in this area
	 */
	public SequenceArea(AlignmentArea owner, int seqenceID) {
		super(owner);
		this.seqenceID = seqenceID;
	}


	/**
	 * Returns the unique identifier of the the sequence displayed by this area.
	 * 
	 * @return an ID of a sequence stored in the according {@link SequenceDataProvider}
	 */
	public int getSeqenceID() {
		return seqenceID;
	}


	private Color getBGColor(Color color, boolean selected) {
		if (color == null) {
			color = getOwner().getColorSchema().getDefaultBgColor();
		}
		if (selected) {
			color = GraphicsUtils.blend(color, getOwner().getColorSchema().getSelectionColor());
		}
		return color;
	}


	private String getNucleotideBaseString(NucleotideCompound compound) {
		String result = compound.getUpperedBase();
		if (result.equals("U") && getOwner().getViewMode().equals(AlignmentDataViewMode.DNA)) {
			return "T";
		}
		else if (result.equals("T") && getOwner().getViewMode().equals(AlignmentDataViewMode.RNA)) {
			return "U";
		}
		else {
			return result;
		}
	}
	
	
  private void paintNucleotideCompound(Graphics2D g, NucleotideCompound compound, float x, float y, 
  		boolean selected) {
  	
  	Set<NucleotideCompound> consituents = compound.getConstituents();
  	final float height = getOwner().getCompoundHeight() / (float)consituents.size();
  	Iterator<NucleotideCompound> iterator = consituents.iterator();
  	float bgY = y;
  	while (iterator.hasNext()) {  // Fill the compound rectangle with differently colored zones, if ambiguity codes are used.
  		g.setColor(getBGColor(getOwner().getColorSchema().getNucleotideColorMap().get(
  				getNucleotideBaseString(iterator.next())),	selected));
    	g.fill(new Rectangle2D.Float(x, bgY, getOwner().getCompoundWidth(), height));
    	bgY += height;
  	}
  	
  	if (getOwner().isPaintCompoundText()) {  // Text output only if font size is not too low
	  	g.setColor(getOwner().getColorSchema().getFontColor());
	  	g.setFont(getOwner().getCompoundFont());
			FontMetrics fm = g.getFontMetrics();
	  	g.drawString(compound.getBase(), x + 0.5f * 
	  			(getOwner().getCompoundWidth() - fm.charWidth(compound.getBase().charAt(0))), y + fm.getAscent());
  	}
  }
  
  
  private void paintCompound(Graphics2D g, Object compound, float x, float y, boolean selected) {
  	g.setColor(getOwner().getColorSchema().getTokenBorderColor());
  	g.draw(new Rectangle2D.Float(x, y, getOwner().getCompoundWidth(), getOwner().getCompoundHeight()));
  	
  	switch (getOwner().getViewMode()) {
  		case NUCLEOTIDE:
			case DNA:
			case RNA:
				paintNucleotideCompound(g, (NucleotideCompound)compound, x, y, selected);
				//TODO Type cast funktioniert so nicht, wenn Quelldaten nicht diesen Datentyp haben! => Konvertierung mit GeneticCode hinzufügen.
				break;
			case CODON:
				break;
			case MIXED_AMINO_ACID:
				break;
			case ALL_AMINO_ACID:
				break;
			case NONE:
				break;
  	}
  }

  
  @Override
	public void paint(TICPaintEvent event) {
		int firstIndex = Math.max(0, getOwner().columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = getOwner().columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = getOwner().getSequenceProvider().getSequenceLength(getSeqenceID()) - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
			lastIndex = lastColumn;
		}
		
  	float x = firstIndex * getOwner().getCompoundWidth();
		for (int i = firstIndex; i <= lastIndex; i++) {			
    	paintCompound(event.getGraphics(), 
    			getOwner().getSequenceProvider().getTokenAt(getSeqenceID(), i), x, 0f,	
    			getOwner().getSelection().isSelected(i, getOwner().getSequenceOrder().indexByID(getSeqenceID())));
	    x += getOwner().getCompoundWidth();
    }
	}

	
	@Override
	public Dimension getSize() {
		return new Dimension(
				getOwner().getCompoundWidth() * getOwner().getSequenceProvider().getSequenceLength(getSeqenceID()),  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end? 
				getOwner().getCompoundHeight());
	}
}
