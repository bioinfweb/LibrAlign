/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea.implementations.charset;


import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.dataarea.DataArea;
import info.bioinfweb.libralign.dataarea.DataAreaListType;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.events.SequenceChangeEvent;
import info.bioinfweb.libralign.model.events.SequenceRenamedEvent;
import info.bioinfweb.libralign.model.events.TokenChangeEvent;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.input.TICMouseAdapter;
import info.bioinfweb.tic.input.TICMouseEvent;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;



/**
 * A data area displaying different character sets associated with the current alignment, as they are e.g. defined
 * in <i>Nexus</i> files.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CharSetArea extends DataArea {
	public static final double BORDER_FRACTION = 0.17;
	
	
	private CharSetDataModel model;
	private int selectedIndex = -1;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @param labeledAlignmentArea the alignment area that shall determine the token widths considered when painting
	 *        the character sets (Should only be different from {@code owner.getOwner()} if the new instance will be
	 *        placed in a different alignment area than the sequence data in a scenario with a
	 *        {@link MultipleAlignmentsContainer}.) 
	 * @param model the model providing the character set data
	 */
	public CharSetArea(AlignmentContentArea owner, AlignmentArea labeledAlignmentArea, CharSetDataModel model) {
		super(owner, labeledAlignmentArea);
		this.model = model;
		
		addMouseListener(new TICMouseAdapter() {
			@Override
			public boolean mousePressed(TICMouseEvent event) {
				if (event.getClickCount() == 1) {
					if (event.isMouseButton1Down()) {
						setSelectedIndex((int)(event.getComponentY() / getLabeledAlignmentArea().getPaintSettings().getTokenHeight()));
						return true;
					}
					else if (event.isMouseButton3Down()) {
						setSelectedIndex(-1);
						return true;
					}
				}
				return false;
			}
		});
	}


	/**
	 * Creates a new instance of this class with an empty data model.
	 * 
	 * @param owner - the alignment area that will be containing the returned data area instance
	 * @param labeledAlignmentArea the alignment area that shall determine the token widths considered when painting
	 *        the character sets (Should only be different from {@code owner.getOwner()} if the new instance will be
	 *        placed in a different alignment area than the sequence data in a scenario with a
	 *        {@link MultipleAlignmentsContainer}.) 
	 */
	public CharSetArea(AlignmentContentArea owner, AlignmentArea labeledAlignmentArea) {
		this(owner, labeledAlignmentArea, new CharSetDataModel());
	}
	
	
	/**
	 * Returns the underlying model providing character set data.
	 * 
	 * @return the instance of the underlying model
	 */
	public CharSetDataModel getModel() {
		return model;
	}


	public int getSelectedIndex() {
		return selectedIndex;
	}


	public void setSelectedIndex(int selectedIndex) {
		if (this.selectedIndex != selectedIndex) {
			this.selectedIndex = selectedIndex;
			repaint();
		}
	}


	@Override
	public double getHeight() {
		return getModel().size() * getLabeledAlignmentArea().getPaintSettings().getTokenHeight();  //TODO Add possible border height, possibly round up?
	}

	
	@Override
	public void paintPart(AlignmentPaintEvent event) {
		AlignmentContentArea contentArea = getLabeledAlignmentArea().getContentArea();
		
		// Paint background:
		Graphics2D g = event.getGraphics();
		g.setColor(SystemColor.menu);
		g.fill(event.getRectangle());
		
		// Determine area to be painted:
		int firstIndex = Math.max(0, contentArea.columnByPaintX((int)event.getRectangle().getMinX()));
		int lastIndex = contentArea.columnByPaintX((int)event.getRectangle().getMaxX());
		int lastColumn = getLabeledAlignmentArea().getGlobalMaxSequenceLength() - 1;  //getSequenceProvider().getMaxSequenceLength() - 1;
		if ((lastIndex == -1) || (lastIndex > lastColumn)) {
			lastIndex = lastColumn;
		}
		
		// Paint output:
		Iterator<CharSet> iterator = getModel().valueList().iterator();
		double y = 0;
		PaintSettings paintSettings = getLabeledAlignmentArea().getPaintSettings();
		final double borderHeight = paintSettings.getTokenHeight() * BORDER_FRACTION;
		final double height = paintSettings.getTokenHeight() - 2 * borderHeight;
		int lineIndex = 0;
		while (iterator.hasNext()) {
			CharSet charSet = iterator.next();
			Color charSetColor = charSet.getColor();
			
			// Paint selection:
			if (lineIndex == getSelectedIndex()) {
				g.setColor(paintSettings.getSelectionColor());
				g.fill(new Rectangle2D.Double(event.getRectangle().getMinX(), y, event.getRectangle().getMaxX(), paintSettings.getTokenHeight()));
				charSetColor = GraphicsUtils.blend(charSetColor, paintSettings.getSelectionColor());
			}
			
			// Paint contained columns:
			g.setColor(charSetColor);
			double x = contentArea.paintXByColumn(firstIndex);
			for (int columnIndex = firstIndex; columnIndex <= lastIndex; columnIndex++) {
				double width = paintSettings.getTokenWidth(columnIndex);
				if (charSet.contains(columnIndex)) {
					g.fill(new Rectangle2D.Double(x, y + borderHeight, width, height));
				}
				x += width;
			}
			
			y += paintSettings.getTokenHeight();
			lineIndex++;
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
