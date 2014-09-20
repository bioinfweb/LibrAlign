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
package info.bioinfweb.libralign.dataarea.implementations.charset;


import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;



/**
 * A data area displaying different character sets associated with the current alignment, as they are e.g. defined
 * in Nexus files.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CharSetArea extends DataArea {
	public static final double BORDER_FRACTION = 0.17;
	
	
	private List<CharSet> model;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will be containing the returned data area instance
	 * @param model - the model providing the character set data
	 */
	public CharSetArea(AlignmentArea owner, List<CharSet> model) {
		super(owner);
		this.model = model;
	}


	/**
	 * Returns the underlying model providing character set data.
	 * 
	 * @return the instance of the underlying model
	 */
	public List<CharSet> getModel() {
		return model;
	}


	@Override
	public int getLength() {
		return getOwner().getCompoundWidth() * getOwner().getSequenceProvider().getMaxSequenceLength();
	}

	
	@Override
	public int getHeight() {
		return getModel().size() * getOwner().getCompoundHeight();  //TODO Add possible border height
	}

	
	@Override
	public void paint(TICPaintEvent event) {
		// Paint background:
		Graphics2D g = event.getGraphics();
		g.setColor(SystemColor.menu);
		g.fill(event.getRectangle());
		
		// Determine area to be painted:
		int firstIndex = Math.max(0, getOwner().columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = getOwner().columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = getOwner().getSequenceProvider().getMaxSequenceLength() - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {
			lastIndex = lastColumn;
		}
		
		// Paint output:
		Iterator<CharSet> iterator = getModel().iterator();
		double y = 0;
		final double borderHeight = getOwner().getCompoundHeight() * BORDER_FRACTION;
		final double height = getOwner().getCompoundHeight() - 2 * borderHeight;
		while (iterator.hasNext()) {
			CharSet charSet = iterator.next();
			g.setColor(charSet.getColor());
			for (int index = firstIndex; index <= lastIndex; index++) {
				if (charSet.contains(index)) {
					g.fill(new Rectangle2D.Double(index * getOwner().getCompoundWidth(), y + borderHeight, 
							getOwner().getCompoundWidth(), height));
				}
			}
			y += getOwner().getCompoundHeight();
		}
	}

	
	/**
	 * This data area is allowed to attached at the top and bottom of an alignment area as well as to each sequence.
	 * (You would usually attach this area to an alignment area as a whole, but anyway it is still allowed to attach
	 * it to a single sequence.)
	 * 
	 * @see info.bioinfweb.libralign.dataarea.DataArea#validLocations()
	 */
	@Override
	public Set<DataAreaListType> validLocations() {
		return EnumSet.of(DataAreaListType.TOP, DataAreaListType.BOTTOM, DataAreaListType.SEQUENCE);
	}
}
