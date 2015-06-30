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
package info.bioinfweb.libralign.dataarea.implementations.charset;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.SystemColor;
import java.util.Iterator;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.graphics.FontCalculator;
import info.bioinfweb.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;



/**
 * GUI component used to display the name labels of character sets displayed by {@link CharSetArea}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CharSetNameArea extends AlignmentLabelSubArea {
	public CharSetNameArea(AlignmentLabelArea owner, CharSetArea labeledSubArea) {
		super(owner, labeledSubArea);
	}

	
	@Override
	public CharSetArea getLabeledArea() {
		return (CharSetArea)super.getLabeledArea();
	}


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
		
		// Paint names:
		Iterator<CharSet> iterator = getLabeledArea().getModel().iterator();
		double y = 0;
		final double compoundHeight = getOwner().getOwner().getPaintSettings().getTokenHeight();
		while (iterator.hasNext()) {
			g.drawString(iterator.next().getName(), AlignmentLabelArea.BORDER_WIDTH, Math.round(y + fm.getAscent()));			
			y += compoundHeight;
		}
	}


	@Override
	public int getNeededWidth() {
		Font compundFont = getOwner().getOwner().getPaintSettings().getTokenHeightFont();
		Iterator<CharSet> iterator = getLabeledArea().getModel().iterator();
		float maxWidth = 0;
		while (iterator.hasNext()) {
			maxWidth = Math.max(maxWidth, FontCalculator.getInstance().getWidth(compundFont, iterator.next().getName()));
		}
		return Math2.roundUp(maxWidth) + 2 * AlignmentLabelArea.BORDER_WIDTH;
	}
}
