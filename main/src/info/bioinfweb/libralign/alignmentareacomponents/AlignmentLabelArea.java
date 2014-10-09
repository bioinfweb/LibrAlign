/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014  Ben Stöver
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
package info.bioinfweb.libralign.alignmentareacomponents;


import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Line2D;
import java.util.Iterator;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.commons.tic.TICComponent;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;



/**
 * A toolkit independent GUI component displaying the sequence names in an {@link AlignmentArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class AlignmentLabelArea extends TICComponent {
	public static final int BORDER_WIDTH = 2;
	
	
  private AlignmentArea owner;
  private ToolkitSpecificAlignmentArea alignmentPartArea;
  private DataAreaListType position;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that uses this instance
	 * @param position - Specify here whether this area will be used to label the head, the content, or the 
	 *        bottom part of the alignment area.
	 */
	public AlignmentLabelArea(AlignmentArea owner, DataAreaListType position) {
		super();
		this.owner = owner;
		this.alignmentPartArea = null;
		this.position = position;
	}


	public ToolkitSpecificAlignmentArea getAlignmentPartArea() {
		return alignmentPartArea;
	}


	public void setAlignmentPartArea(ToolkitSpecificAlignmentArea alignmentPartArea) {
		this.alignmentPartArea = alignmentPartArea;
		assignSize();
	}


	/**
	 * Returns the alignment area that uses this instance.
	 * 
	 * @return the owning alignment area
	 */
	public AlignmentArea getOwner() {
		return owner;
	}


	/**
	 * Returns whether this area is used to label the head, the content, or the bottom part of the 
	 * alignment area.
	 * 
	 * @return the position of this area
	 */
	public DataAreaListType getPosition() {
		return position;
	}


	/**
	 * Calculates the necessary with of the component depending on the maximal label length.
	 */
	private float calculateWidth() {
		Font font = getOwner().getCompoundFont();
		Iterator<Integer> idIterator = getOwner().getSequenceOrder().getIdList().iterator();
		float maxLength = 0;
		while (idIterator.hasNext()) {
			maxLength = Math.max(maxLength, FontCalculator.getInstance().getWidth(font, 
					getOwner().getSequenceProvider().sequenceNameByID(idIterator.next()))); 			
		}
		return maxLength + 2 * BORDER_WIDTH;  //TODO Is the font for the correct zoom already set before this method is called?
  }

	
	@Override
	public Dimension getSize() {
		int height = 0;
		if (getAlignmentPartArea() != null) {
			height = getAlignmentPartArea().getHeight();
		}
		return new Dimension(Math2.roundUp(calculateWidth()),	height);  // If references starting from owner would be used here, there would be problems in initialization order.
	}

	
	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g  = e.getGraphics();
  	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);

  	// Paint background:
		g.setColor(SystemColor.menu);
		g.fill(e.getRectangle());
		
		if (getPosition().equals(DataAreaListType.SEQUENCE)) {
			// Set font:
			g.setColor(SystemColor.menuText);
			g.setFont(getOwner().getCompoundFont());
			FontMetrics fm = g.getFontMetrics();
			
			// Paint labels:
			Iterator<Integer> idIterator = getOwner().getSequenceOrder().getIdList().iterator();
			int y = 0;
			while (idIterator.hasNext()) {
				int id = idIterator.next();
				if (y > 0) {  // Do not draw a line at the top of the component.
					g.draw(new Line2D.Float(0, y, getSize().width, y));
				}
		  	g.drawString(getOwner().getSequenceProvider().sequenceNameByID(id), BORDER_WIDTH, y + fm.getAscent());
		  	y += getOwner().getCompoundHeight() + getOwner().getDataAreas().getSequenceAreas(id).getVisibleHeight();
			}
		}
	}	
}
