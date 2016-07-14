/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2016  Ben Stöver
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


import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;

import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;



/**
 * GUI component used to label {@link AlignmentSubArea}s that do not provide an own label component.
 * 
 * @author Ben St&ouml;ver
 * @since 0.3.0
 */
public class DefaultLabelSubArea extends AlignmentLabelSubArea {
	public DefaultLabelSubArea(AlignmentLabelArea owner, AlignmentSubArea labeledSubArea) {
		super(owner, labeledSubArea);
	}

	
	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g  = e.getGraphics();
  	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
  			RenderingHints.VALUE_ANTIALIAS_ON);

  	// Paint background:
		g.setColor(SystemColor.menu);
		g.fill(e.getRectangle());
	}


	/**
	 * Always returns 0 since this default implementation does not display anything.
	 * 
	 * @return 0
	 */
	@Override
	public int getNeededWidth() {
		return 0;
	}	
}
