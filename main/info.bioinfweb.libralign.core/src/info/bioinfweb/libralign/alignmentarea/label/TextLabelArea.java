/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2015  Ben Stöver
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


import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentSubArea;

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;



public abstract class TextLabelArea extends AlignmentLabelSubArea {
	public TextLabelArea(AlignmentLabelArea owner, AlignmentSubArea labeledSubArea) {
		super(owner, labeledSubArea);
	}
	
	
	protected abstract String getText();

	
	@Override
	public void paint(TICPaintEvent e) {
		Graphics2D g  = e.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

  		// Paint background:
		g.setColor(SystemColor.menu);
		g.fill(e.getRectangle());
		
		g.setColor(SystemColor.menuText);
		
		g.setFont(getOwner().getOwner().getPaintSettings().getTokenHeightFont());
		FontMetrics fm = g.getFontMetrics();
		
		// Paint name:
		g.drawString(getText(), AlignmentLabelArea.BORDER_WIDTH, fm.getAscent());
	}


	@Override
	public int getNeededWidth() {
		return Math2.roundUp(FontCalculator.getInstance().getWidth(getOwner().getOwner().getPaintSettings().getTokenHeightFont(),
				getText())) +	2 * AlignmentLabelArea.BORDER_WIDTH;
	}
}