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


import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import info.bioinfweb.commons.tic.TICPaintEvent;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;



/**
 * A data area displaying different character sets associated with the current alignment, as they are e.g. defined
 * in Nexus files.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CharSetArea extends DataArea {
	public static final double BORDER_FRACTION = 0.17;
	
	
	private CharSetDataModel model;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner - the alignment area that will be containing the returned data area instance
	 * @param model - the model providing the character set data
	 */
	public CharSetArea(AlignmentContentArea owner, CharSetDataModel model) {
		super(owner);
		this.model = model;
	}


	/**
	 * Creates a new instance of this class with an empty data model.
	 * 
	 * @param owner - the alignment area that will be containing the returned data area instance
	 */
	public CharSetArea(AlignmentContentArea owner) {
		this(owner, new CharSetDataModel());
	}
	
	
	/**
	 * Returns the underlying model providing character set data.
	 * 
	 * @return the instance of the underlying model
	 */
	public CharSetDataModel getModel() {
		return model;
	}


	@Override
	public int getLength() {
		AlignmentArea area = getOwner().getOwner();
		return getOwner().paintXByColumn(area.getGlobalMaxSequenceLength()) - area.getDataAreas().getGlobalMaxLengthBeforeStart();
	}

	
	@Override
	public int getHeight() {
		return (int)Math.round(getModel().size() * getOwner().getOwner().getTokenHeight());  //TODO Add possible border height, possibly round up?
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
		int lastColumn = getOwner().getOwner().getGlobalMaxSequenceLength() - 1;  //getSequenceProvider().getMaxSequenceLength() - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {
			lastIndex = lastColumn;
		}
		
		// Paint output:
		Iterator<CharSet> iterator = getModel().iterator();
		double y = 0;
		final double borderHeight = getOwner().getOwner().getTokenHeight() * BORDER_FRACTION;
		final double height = getOwner().getOwner().getTokenHeight() - 2 * borderHeight;
		while (iterator.hasNext()) {
			CharSet charSet = iterator.next();
			g.setColor(charSet.getColor());
			double x = getOwner().paintXByColumn(firstIndex);
			for (int index = firstIndex; index <= lastIndex; index++) {
				if (charSet.contains(index)) {
					double width = getOwner().getOwner().getTokenWidth(index);
					g.fill(new Rectangle2D.Double(x, y + borderHeight, width, height));
					x += width;
				}
			}
			y += getOwner().getOwner().getTokenHeight();
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


	@Override
	public <T> void afterSequenceChange(SequenceChangeEvent<T> e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <T> void afterSequenceRenamed(SequenceRenamedEvent<T> e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <T> void afterTokenChange(TokenChangeEvent<T> e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public <T, U> void afterProviderChanged(AlignmentModel<T> previous,
			AlignmentModel<U> current) {
		
		// TODO Auto-generated method stub		
	}


	@Override
	protected AlignmentLabelSubArea createLabelSubArea(AlignmentLabelArea owner) {
		return new CharSetNameArea(owner, this);
	}
}
