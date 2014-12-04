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
package info.bioinfweb.libralign.alignmentarea.label;


import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.awt.geom.Line2D;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;
import info.bioinfweb.libralign.alignmentarea.content.SequenceArea;



/**
 * GUI component used to label instances of {@link SequenceArea}. The name of the according sequence will be displayed.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class SequenceLabelArea extends AlignmentLabelSubArea {
	public SequenceLabelArea(AlignmentLabelArea owner, AlignmentSubArea labeledSubArea) {
		super(owner, labeledSubArea);
	}


	@Override
	public SequenceArea getLabeledArea() {
		return (SequenceArea)super.getLabeledArea();
	}


	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g  = e.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

  		// Paint background:
		g.setColor(SystemColor.menu);
		g.fill(e.getRectangle());
		
		g.setColor(SystemColor.menuText);
		AlignmentContentArea contentArea = getOwner().getOwner().getContentArea(); 
		g.setFont(contentArea.getCompoundFont());
		FontMetrics fm = g.getFontMetrics();
		
		// Paint name:
		if (getLabeledArea().getOwner().getSequenceOrder().indexByID(getLabeledArea().getSeqenceID()) > 0) {
			g.draw(new Line2D.Float(0, 0, getSize().width, 0));  // Draw line only if there is another label above.
		}
		g.drawString(contentArea.getSequenceProvider().sequenceNameByID(getLabeledArea().getSeqenceID()), 
				AlignmentLabelArea.BORDER_WIDTH, fm.getAscent());
	}


	@Override
	public int getNeededWidth() {
		AlignmentContentArea contentArea = getOwner().getOwner().getContentArea(); 
		return Math2.roundUp(FontCalculator.getInstance().getWidth(contentArea.getCompoundFont(), 
				contentArea.getSequenceProvider().sequenceNameByID(getLabeledArea().getSeqenceID()))) + 
				2 * AlignmentLabelArea.BORDER_WIDTH;
	}
}
