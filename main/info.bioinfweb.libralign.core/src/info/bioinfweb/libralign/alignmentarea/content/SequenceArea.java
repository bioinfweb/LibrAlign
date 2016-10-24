/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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
import info.bioinfweb.tic.TICPaintEvent;
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
	
	
	private String sequenceID;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will contain this instance
	 * @param sequenceID - the unique identifier of the sequence that will be displayed in this area
	 */
	public SequenceArea(AlignmentContentArea owner, String sequenceID) {
		super(owner);
		this.sequenceID = sequenceID;
		assignSize();
	}


	/**
	 * Returns the unique identifier of the the sequence displayed by this area.
	 * 
	 * @return an ID of a sequence stored in the according {@link AlignmentModel}
	 */
	public String getSequenceID() {
		return sequenceID;
	}


  private void paintCursor(Graphics2D g) {
  	SelectionModel selection = getOwner().getOwner().getSelection();
  	if (Math2.isBetween(getOwner().getOwner().getSequenceOrder().indexByID(getSequenceID()), 
  			selection.getCursorRow(), selection.getCursorRow() + selection.getCursorHeight() - 1)) {
  		
  		Stroke previousStroke = g.getStroke();
  		try {
  			PaintSettings paintSettings = getOwner().getOwner().getPaintSettings();
  			g.setColor(paintSettings.getCursorColor());
  			g.setStroke(new BasicStroke((float)paintSettings.getCursorLineWidth()));
  			double x = getOwner().paintXByColumn(selection.getCursorColumn());  //TODO Test if this is equivalent to previous implementation.
  			g.draw(new Line2D.Double(x, 0, x, paintSettings.getTokenHeight()));
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
		int lastColumn = getOwner().getOwner().getAlignmentModel().getSequenceLength(getSequenceID()) - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {  //TODO Elongate to the length of the longest sequence and paint empty/special tokens on the right end?
			lastIndex = lastColumn;
		}
		
		event.getGraphics().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		double x = getOwner().paintXByColumn(firstIndex);
		PaintSettings paintSettings = getOwner().getOwner().getPaintSettings();
		for (int i = firstIndex; i <= lastIndex; i++) {
			paintSettings.getTokenPainterList().painterByColumn(i).paintToken(getOwner().getOwner(), getSequenceID(), i, 
					event.getGraphics(), new Rectangle2D.Double(x, 0, paintSettings.getTokenWidth(i), paintSettings.getTokenHeight()), 
					paintSettings.getSelectionColor());
	    x += paintSettings.getTokenWidth(i);
    }
		
		if (!getOwner().getOwner().getSelection().getType().equals(SelectionType.ROW_ONLY)) {
			paintCursor(event.getGraphics());
		}
	}

	
	@Override
	public Dimension getSize() {
		return new Dimension(getOwner().getOwner().getGlobalMaxNeededWidth(),
				(int)Math.round(getOwner().getOwner().getPaintSettings().getTokenHeight()));  //TODO Always round up here?
	}


	@Override
	protected AlignmentLabelSubArea createLabelSubArea(AlignmentLabelArea owner) {
		return new SequenceLabelArea(owner, this);
	}
}
