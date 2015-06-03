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
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.label.SequenceLabelArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionType;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * The area inside an {@link AlignmentArea} that displays one sequence of the alignment.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public class SequenceArea extends AlignmentSubArea {
	private static final Color DEFAULT_BACKGROUND_COLOR = Color.LIGHT_GRAY.brighter();
	
	
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
	 * @return an ID of a sequence stored in the according {@link AlignmentModel}
	 */
	public int getSeqenceID() {
		return seqenceID;
	}


//	private static Color getBGColor(SequenceColorSchema colorSchema, Color color, boolean selected) {
//		if (color == null) {
//			color = colorSchema.getDefaultBgColor();
//		}
//		if (selected) {
//			color = GraphicsUtils.blend(color, colorSchema.getSelectionColor());
//		}
//		return color;
//	}


//	private static String getNucleotideBaseString(AlignmentDataViewMode viewMode, NucleotideCompound compound) {
//		String result = compound.getUpperedBase();
//		if (result.equals("U") && viewMode.equals(AlignmentDataViewMode.DNA)) {
//			return "T";
//		}
//		else if (result.equals("T") && viewMode.equals(AlignmentDataViewMode.RNA)) {
//			return "U";
//		}
//		else {
//			return result;
//		}
//	}
	
	
//  private static void paintNucleotideCompound(AlignmentArea area, Graphics2D g, NucleotideCompound compound, 
//  		double x, double y, boolean selected) {
//  	
//  	Set<NucleotideCompound> consituents = compound.getConstituents();
//  	final double height = area.getCompoundHeight() / (double)consituents.size();
//  	Iterator<NucleotideCompound> iterator = consituents.iterator();
//  	double bgY = y;
//  	while (iterator.hasNext()) {  // Fill the compound rectangle with differently colored zones, if ambiguity codes are used.
//  		g.setColor(getBGColor(area.getColorSchema(), area.getColorSchema().getNucleotideColorMap().get(
//  				getNucleotideBaseString(area.getViewMode(), iterator.next())), selected));
//    	g.fill(new Rectangle2D.Double(x, bgY, area.getCompoundWidth(), height));
//    	bgY += height;
//  	}
//  	
//  	if (area.getContentArea().isPaintCompoundText()) {  // Text output only if font size is not too low
//	  	g.setColor(area.getColorSchema().getFontColor());
//	  	g.setFont(area.getCompoundFont());
//			FontMetrics fm = g.getFontMetrics();
//	  	g.drawString(compound.getBase(), (int)(x + 0.5 * 
//	  			(area.getCompoundWidth() - fm.charWidth(compound.getBase().charAt(0)))), (int)(y + fm.getAscent()));  // int needs to be used because the precision of float is not sufficient to paint long sequences
//  	}
//  }
  
  
//  public static void paintCompound(AlignmentArea area, Graphics2D g, Object compound, double x, double y, boolean selected) {
//  	g.setColor(area.getColorSchema().getTokenBorderColor());
//  	g.draw(new Rectangle2D.Double(x, y, area.getCompoundWidth(), area.getCompoundHeight()));
//  	
//  	switch (area.getViewMode()) {
//  		case NUCLEOTIDE:
//			case DNA:
//			case RNA:
//				paintNucleotideCompound(area, g, (NucleotideCompound)compound, x, y, selected);
//				//TODO Type cast funktioniert so nicht, wenn Quelldaten nicht diesen Datentyp haben! => Konvertierung mit GeneticCode hinzufügen.
//				break;
//			case CODON:
//				break;
//			case MIXED_AMINO_ACID:
//				break;
//			case ALL_AMINO_ACID:
//				break;
//			case NONE:
//				break;
//  	}
//  }

  
//  public static void paintSequence(AlignmentArea area, int sequenceID, TICPaintEvent event, double x, double y) {
//		int firstIndex = Math.max(0, area.getContentArea().columnByPaintX((int)event.getRectangle().getMinX()));
//		int lastIndex = area.getContentArea().columnByPaintX((int)event.getRectangle().getMaxX());
//		int lastColumn = area.getAlignmentModel().getSequenceLength(sequenceID) - 1;
//		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
//			lastIndex = lastColumn;
//		}
//		
//  	x += firstIndex * area.getCompoundWidth();
//		for (int i = firstIndex; i <= lastIndex; i++) {			
//    	paintCompound(area, event.getGraphics(), 
//    			area.getAlignmentModel().getTokenAt(sequenceID, i), (double)x, y,	
//    			area.getSelection().isSelected(i, area.getSequenceOrder().indexByID(sequenceID)));
//	    x += area.getCompoundWidth();
//    }
//  }
  
  
  private void paintCursor(Graphics2D g, double x, double y) {
  	SelectionModel selection = getOwner().getOwner().getSelection();
  	if (Math2.isBetween(getOwner().getOwner().getSequenceOrder().indexByID(getSeqenceID()), 
  			selection.getCursorRow(), selection.getCursorRow() + selection.getCursorHeight() - 1)) {
  		
  		Stroke previousStroke = g.getStroke();
  		try {
  			PaintSettings paintSettings = getOwner().getOwner().getPaintSettings();
  			g.setColor(paintSettings.getCursorColor());
  			g.setStroke(new BasicStroke((float)paintSettings.getCursorLineWidth()));
  			x += getOwner().paintXByColumn(selection.getCursorColumn());  //TODO Test if this is equivalent to previous implementation.
  			g.draw(new Line2D.Double(x, y, x, y + paintSettings.getTokenHeight()));
  		}
  		finally {
  			g.setStroke(previousStroke);
  		}
  	}
  }
  
  
  @Override
	public void paint(TICPaintEvent event) {
		event.getGraphics().setColor(DEFAULT_BACKGROUND_COLOR);  //TODO Define different background color for whole component and unknown tokens
		event.getGraphics().fillRect(event.getRectangle().x, event.getRectangle().y, event.getRectangle().width, event.getRectangle().height);

		int firstIndex = Math.max(0, getOwner().columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = getOwner().columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = getOwner().getOwner().getAlignmentModel().getSequenceLength(getSeqenceID()) - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
			lastIndex = lastColumn;
		}
		
		event.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double x = getOwner().paintXByColumn(firstIndex);
		PaintSettings paintSettings = getOwner().getOwner().getPaintSettings();
		for (int i = firstIndex; i <= lastIndex; i++) {
			paintSettings.getTokenPainterList().painterByColumn(i).paintToken(getOwner().getOwner(), getSeqenceID(), i, 
					event.getGraphics(), new Rectangle2D.Double(x, 0, paintSettings.getTokenWidth(i), paintSettings.getTokenHeight()), 
					paintSettings.getSelectionColor());
//    	paintCompound(getOwner().getOwner(), event.getGraphics(), 
//    			getOwner().getOwner().getAlignmentModel().getTokenAt(getSeqenceID(), i), x, 0f,	
//    			getOwner().getOwner().getSelection().isSelected(i, getOwner().getOwner().getSequenceOrder().indexByID(getSeqenceID())));
	    x += paintSettings.getTokenWidth(i);
    }
		
		if (!getOwner().getOwner().getSelection().getType().equals(SelectionType.ROW_ONLY)) {
			paintCursor(event.getGraphics(), getOwner().getOwner().getDataAreas().getGlobalMaxLengthBeforeStart(), 0);
		}
	}

	
	@Override
	public Dimension getSize() {
		return new Dimension(
				getOwner().paintXByColumn(getOwner().getOwner().getAlignmentModel().getSequenceLength(getSeqenceID())),  //TODO Test if this is equivalent to previous implementation. //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end? 
				(int)Math.round(getOwner().getOwner().getPaintSettings().getTokenHeight()));  //TODO Always round up here?
	}


	@Override
	protected AlignmentLabelSubArea createLabelSubArea(AlignmentLabelArea owner) {
		return new SequenceLabelArea(owner, this);
	}
}
