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
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Set;

import org.biojava3.core.sequence.compound.NucleotideCompound;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentDataViewMode;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.label.SequenceLabelArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionType;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * The area inside an {@link AlignmentArea} that displays one sequence of the alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SequenceArea extends AlignmentSubArea {
	private int seqenceID;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will contain this instance
	 * @param seqenceID - the unique identifier of the sequence that will be displayed in this area
	 */
	public SequenceArea(AlignmentContentArea owner, int seqenceID) {
		super(owner);
		this.seqenceID = seqenceID;
		assignSize();
	}


	/**
	 * Returns the unique identifier of the the sequence displayed by this area.
	 * 
	 * @return an ID of a sequence stored in the according {@link SequenceDataProvider}
	 */
	public int getSeqenceID() {
		return seqenceID;
	}


	private static Color getBGColor(SequenceColorSchema colorSchema, Color color, boolean selected) {
		if (color == null) {
			color = colorSchema.getDefaultBgColor();
		}
		if (selected) {
			color = GraphicsUtils.blend(color, colorSchema.getSelectionColor());
		}
		return color;
	}


	private static String getNucleotideBaseString(AlignmentDataViewMode viewMode, NucleotideCompound compound) {
		String result = compound.getUpperedBase();
		if (result.equals("U") && viewMode.equals(AlignmentDataViewMode.DNA)) {
			return "T";
		}
		else if (result.equals("T") && viewMode.equals(AlignmentDataViewMode.RNA)) {
			return "U";
		}
		else {
			return result;
		}
	}
	
	
  private static void paintNucleotideCompound(AlignmentContentArea area, Graphics2D g, NucleotideCompound compound, 
  		float x, float y, boolean selected) {
  	
  	Set<NucleotideCompound> consituents = compound.getConstituents();
  	final float height = area.getCompoundHeight() / (float)consituents.size();
  	Iterator<NucleotideCompound> iterator = consituents.iterator();
  	float bgY = y;
  	while (iterator.hasNext()) {  // Fill the compound rectangle with differently colored zones, if ambiguity codes are used.
  		g.setColor(getBGColor(area.getColorSchema(), area.getColorSchema().getNucleotideColorMap().get(
  				getNucleotideBaseString(area.getViewMode(), iterator.next())), selected));
    	g.fill(new Rectangle2D.Float(x, bgY, area.getCompoundWidth(), height));
    	bgY += height;
  	}
  	
  	if (area.isPaintCompoundText()) {  // Text output only if font size is not too low
	  	g.setColor(area.getColorSchema().getFontColor());
	  	g.setFont(area.getCompoundFont());
			FontMetrics fm = g.getFontMetrics();
	  	g.drawString(compound.getBase(), x + 0.5f * 
	  			(area.getCompoundWidth() - fm.charWidth(compound.getBase().charAt(0))), y + fm.getAscent());
  	}
  }
  
  
  public static void paintCompound(AlignmentContentArea area, Graphics2D g, Object compound, float x, float y, boolean selected) {
  	g.setColor(area.getColorSchema().getTokenBorderColor());
  	g.draw(new Rectangle2D.Float(x, y, area.getCompoundWidth(), area.getCompoundHeight()));
  	
  	switch (area.getViewMode()) {
  		case NUCLEOTIDE:
			case DNA:
			case RNA:
				paintNucleotideCompound(area, g, (NucleotideCompound)compound, x, y, selected);
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

  
  public static void paintSequence(AlignmentContentArea area, int sequenceID, TICPaintEvent event, float x, float y) {
		int firstIndex = Math.max(0, area.columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = area.columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = area.getSequenceProvider().getSequenceLength(sequenceID) - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
			lastIndex = lastColumn;
		}
		
  	x += firstIndex * area.getCompoundWidth();
		for (int i = firstIndex; i <= lastIndex; i++) {			
    	paintCompound(area, event.getGraphics(), 
    			area.getSequenceProvider().getTokenAt(sequenceID, i), x, y,	
    			area.getSelection().isSelected(i, area.getSequenceOrder().indexByID(sequenceID)));
	    x += area.getCompoundWidth();
    }
  }
  
  
  private void paintCursor(Graphics2D g, double x, double y) {
  	SelectionModel selection = getOwner().getSelection();
  	if (Math2.isBetween(getOwner().getSequenceOrder().indexByID(getSeqenceID()), 
  			selection.getCursorRow(), selection.getCursorRow() + selection.getCursorHeight() - 1)) {
  		
  		Stroke previousStroke = g.getStroke();
  		try {
  			SequenceColorSchema colorSchema = getOwner().getColorSchema();
  			g.setColor(colorSchema.getCursorColor());
  			g.setStroke(new BasicStroke(colorSchema.getCursorLineWidth()));
  			x += selection.getCursorColumn() * getOwner().getCompoundWidth();
  			g.draw(new Line2D.Double(x, y, x, y + getOwner().getCompoundHeight()));
  		}
  		finally {
  			g.setStroke(previousStroke);
  		}
  	}
  }
  
  
  @Override
	public void paint(TICPaintEvent event) {
		event.getGraphics().setColor(getOwner().getColorSchema().getDefaultBgColor());  //TODO Define different background color for whole component and unknown tokens
		event.getGraphics().fillRect(event.getRectangle().x, event.getRectangle().y, event.getRectangle().width, event.getRectangle().height);

		int firstIndex = Math.max(0, getOwner().columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = getOwner().columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = getOwner().getSequenceProvider().getSequenceLength(getSeqenceID()) - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
			lastIndex = lastColumn;
		}
		
		event.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		float x = firstIndex * getOwner().getCompoundWidth() + getOwner().getDataAreas().getGlobalMaxLengthBeforeStart(); 
		for (int i = firstIndex; i <= lastIndex; i++) {			
    	paintCompound(getOwner(), event.getGraphics(), 
    			getOwner().getSequenceProvider().getTokenAt(getSeqenceID(), i), x, 0f,	
    			getOwner().getSelection().isSelected(i, getOwner().getSequenceOrder().indexByID(getSeqenceID())));
	    x += getOwner().getCompoundWidth();
    }
		
		if (getOwner().getSelection().getType().equals(SelectionType.CELLS)) {
			paintCursor(event.getGraphics(), getOwner().getDataAreas().getGlobalMaxLengthBeforeStart(), 0);
		}
	}

	
	@Override
	public Dimension getSize() {
		return new Dimension(
				getOwner().getDataAreas().getGlobalMaxLengthBeforeStart() + 
				getOwner().getCompoundWidth() * getOwner().getSequenceProvider().getSequenceLength(getSeqenceID()),  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end? 
				getOwner().getCompoundHeight());
	}


	@Override
	protected AlignmentLabelSubArea createLabelSubArea(AlignmentLabelArea owner) {
		return new SequenceLabelArea(owner, this);
	}
}
