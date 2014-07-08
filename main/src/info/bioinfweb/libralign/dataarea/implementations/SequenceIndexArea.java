/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben St�ver
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
package info.bioinfweb.libralign.dataarea.implementations;


import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.SystemColor;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.EnumSet;
import java.util.Set;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;



/**
 * A data area displaying the indices of aligned sequences.
 * <p>
 * You would usually use this component in the head area of an {@link AlignmentArea} but it is possible to
 * insert it at any position.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class SequenceIndexArea extends CustomHeightFullWidthArea {
	/** The default value returned by {@link #getFirstIndex()} if it has not been changed. */
	public static final int DEFAULT_FIRST_INDEX = 1;
	
	/** The stroke used to paint the dashes */
	public static final Stroke DASH_STROKE = new BasicStroke(0, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
	
  /** The length of a dash */
  public static final float DASH_LENGTH_FACTOR = 0.3f;
  
  /** The length of a dash with a label */
  public static final float LABELED_DASH_LENGTH_FACTOR = 1f;
  
	/** The distance of the labels to the border of the component */
	public static final float LABEL_TOP_DISTANCE_FACTOR = 0.5f;

	/** The distance of the labels to their dash  */
	public static final float LABEL_LEFT_DISTANCE_FACTOR = 0.2f;

	/** 
  * This string is used to test if the interval between two main dashes is smaller than 
  * the usual label text. 
  */
  public static final String LABEL_LENGTH_STANDARD = "00000";
	
	
  private int firstIndex = DEFAULT_FIRST_INDEX;

	
	/**
	 * Creates a new instance of this class using a default height.
	 * 
	 * @param owner - the alignment area that is going to contain this data area
	 */
	public SequenceIndexArea(AlignmentArea owner) {
		super(owner, owner.getCompoundHeight());
	}


	private void paintSelection(Graphics2D g) {
		//TODO implement
//		g.setColor(SystemColor.textHighlight);
//		AlignmentComparisonPanelSelection selection = getAlignmentComparisonPanel().getSelection();
//		int x = getAlignmentComparisonPanel().paintXBycolumn(selection.getFirstPos()); 
//		g.fill(new Rectangle2D.Float(x, 0, 
//				getAlignmentComparisonPanel().paintXBycolumn(selection.getLastPos() + 1) - 1 - x, HEIGHT));
	}
  
  
	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g = e.getGraphics();
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
    		RenderingHints.VALUE_ANTIALIAS_ON);
  
    // Paint background:
    Rectangle visibleRect = e.getRectangle();
		g.setColor(SystemColor.menu);
		g.fill(visibleRect);
		paintSelection(g);
		
    // Paint base line
		float compoundWidth = getOwner().getCompoundWidth();
		g.setColor(SystemColor.menuText);
    g.draw(new Line2D.Float(visibleRect.x, getHeight() - 1, visibleRect.x + visibleRect.width, getHeight() - 1));  // base line
    
    // Paint text data and dashes:
    float labelLeftDistance = LABEL_LEFT_DISTANCE_FACTOR * getOwner().getCompoundWidth();
    g.setFont(getOwner().getCompoundFont());
    int labelInterval = Math2.roundUp(
    		(g.getFontMetrics().stringWidth(LABEL_LENGTH_STANDARD) + 2 * labelLeftDistance) / compoundWidth);
    float x = Math.max(compoundWidth / 2f,  
    		visibleRect.x - visibleRect.x % compoundWidth - labelInterval * getOwner().getCompoundWidth() - compoundWidth / 2f);  // labelInterval is subtracted because partly visible text should also be painted
    Stroke stroke = g.getStroke();
    try {
      while (x <= visibleRect.x + visibleRect.width) {
    		// Text output
    		float dashLength = DASH_LENGTH_FACTOR * getHeight();
    		int compoundIndex = Math.round(x / compoundWidth); 
    		if ((compoundIndex - 1) % labelInterval == 0) {  // BioJava indices start with 1
    			g.drawString("" + (compoundIndex + getFirstIndex() - 1), x + labelLeftDistance,	
    					LABEL_TOP_DISTANCE_FACTOR * getHeight());
    			dashLength = LABELED_DASH_LENGTH_FACTOR * getHeight();
    		}
    		
      	// Dash output
    		g.setStroke(DASH_STROKE);
    		Path2D path = new  Path2D.Float();
    		path.moveTo(x - 0.5, getHeight());
    		path.lineTo(x + 0.5, getHeight());
    		path.lineTo(x + 0.5, getHeight() - dashLength);
    		path.lineTo(x - 0.5, getHeight() - dashLength);
    		path.closePath();
    		g.fill(path);

    		x += compoundWidth;
      }
    }
    finally {
    	g.setStroke(stroke);
    }
	}

	
	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.TOP, DataAreaListType.SEQUENCE, DataAreaListType.BOTTOM);
  }
	

	@Override
	public int getLength() {
		return getOwner().getCompoundWidth() * getOwner().getSequenceProvider().getMaxSequenceLength();
	}


	/**
	 * Returns the index that will be displayed for the first column of the alignment area.
	 * <p>
	 * The default value is {@value #DEFAULT_FIRST_INDEX}. Note that the methods of {@link AlignmentArea} and associated classes will always 
	 * use 0 as the index of the first column no matter which value is set here. It is used for the GUI output 
	 * of this data area only.
	 * 
	 * @return the displayed index of the first column 
	 */
	public int getFirstIndex() {
		return firstIndex;
	}


	/**
	 * Sets a new index that will be displayed for the first column of the alignment area.
	 * <p>
	 * The methods of {@link AlignmentArea} and associated classes will always use 0 as the index of the first 
	 * column no matter which value is specified here. It is used for the GUI output of this data area only.
	 * 
	 * @param firstIndex - the new index display for the first column
	 */
	public void setFirstIndex(int firstIndex) {
		this.firstIndex = firstIndex;
	}
}
