/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
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


import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.SystemColor;
import java.awt.geom.Rectangle2D;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import info.bioinfweb.commons.Math2;
import info.bioinfweb.commons.events.GenericEventObject;
import info.bioinfweb.commons.graphics.GraphicsUtils;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentContentArea;
import info.bioinfweb.libralign.alignmentarea.content.AlignmentPaintEvent;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.paintsettings.PaintSettings;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionListener;
import info.bioinfweb.libralign.dataarea.ModelBasedDataArea;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColorChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColumnChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetRenamedEvent;
import info.bioinfweb.libralign.dataelement.DataListType;
import info.bioinfweb.libralign.multiplealignments.MultipleAlignmentsContainer;
import info.bioinfweb.tic.input.TICMouseAdapter;
import info.bioinfweb.tic.input.TICMouseEvent;



/**
 * A data area displaying different character sets associated with the current alignment, as they are e.g. defined
 * in <i>Nexus</i> files.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CharSetArea extends ModelBasedDataArea<CharSetDataModel> {
	public static final double BORDER_FRACTION = 0.17;
	
	
	private int selectedIndex = -1;
	private Set<SelectionListener<GenericEventObject<CharSetArea>>> selectionListeners = 
			new HashSet<SelectionListener<GenericEventObject<CharSetArea>>>();
	private CharSetDataModelListener modelListener;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @param labeledAlignmentArea the alignment area that shall determine the token widths considered when painting
	 *        the character sets (Should only be different from {@code owner.getOwner()} if the new instance will be
	 *        placed in a different alignment area than the sequence data in a scenario with a
	 *        {@link MultipleAlignmentsContainer}.) 
	 * @param model the model providing the character set data
	 * @throws IllegalArgumentException if {@code model} is {@code null}
	 */
	public CharSetArea(AlignmentContentArea owner, AlignmentArea labeledAlignmentArea, CharSetDataModel model) {
		super(owner, labeledAlignmentArea, model);
		modelListener = new CharSetDataModelListener() {
			@Override
			public void afterCharSetRenamed(CharSetRenamedEvent e) {
				if (e.isLastEvent()) {
					getOwner().getOwner().revalidate();
					repaint();
				}
			}
			
			@Override
			public void afterCharSetColumnChange(CharSetColumnChangeEvent e) {
				if (e.isLastEvent()) {
					repaint();
				}
			}
			
			@Override
			public void afterCharSetColorChange(CharSetColorChangeEvent e) {
				if (e.isLastEvent()) {
					repaint();
				}
			}
			
			@Override
			public void afterCharSetChange(CharSetChangeEvent e) {
				if (e.isLastEvent()) {
					checkSelectedIndex();
					getOwner().getOwner().revalidate();
					repaint();
				}
			}

			@Override
			public void afterModelChanged(CharSetDataModel previous, CharSetDataModel current) {
				checkSelectedIndex();
				getOwner().getOwner().revalidate();
				repaint();
			}
		};
		model.addModelListener(modelListener);
		
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
	 * @param owner the alignment area that will be containing the returned data area instance
	 * @param labeledAlignmentArea the alignment area that shall determine the token widths considered when painting
	 *        the character sets (Should only be different from {@code owner.getOwner()} if the new instance will be
	 *        placed in a different alignment area than the sequence data in a scenario with a
	 *        {@link MultipleAlignmentsContainer}.) 
	 */
	public CharSetArea(AlignmentContentArea owner, AlignmentArea labeledAlignmentArea) {
		this(owner, labeledAlignmentArea, new CharSetDataModel(labeledAlignmentArea.getAlignmentModel()));  // CharSetDataModel currently does not make use of the specified alignment model.
	}
	
	
	/**
	 * Changes the model instance that is displayed by this area.
	 * 
	 * @param model the new model
	 * @param moveListeners Specify {@code true} here if all listers registered to the current model should be 
	 *        moved to the new model or {@code false} otherwise
	 * @return the previous model
	 * @throws IllegalArgumentException if {@code model} is {@code null}
	 */
	public CharSetDataModel setModel(CharSetDataModel model, boolean moveListeners) {
		//TODO Consider moving most of the functionality of this method into the model. (#358)
		
		if (model == null) {
			throw new IllegalArgumentException("The model must not be null.");
		}
		else {
			CharSetDataModel result = getModel();
			if (model != getModel()) {  // equals() would, e.g., consider two different empty models as equal, which is not suitable here.
				if (getModel() != null) {
					if (moveListeners) {  // Move all listeners
						model.modelListeners.addAll(getModel().modelListeners);
						getModel().modelListeners.clear();
					}
					else {  // Move this instance as the listener anyway:
						getModel().removeModelListener(modelListener);
						model.addModelListener(modelListener);
					}
				}
				
				setModel(model);
				
	      // Fire events for listener move after the process finished
				if (getModel() != null) {
					getModel().addModelListener(modelListener);  // Add this object as a listener if it was not already moved from the previous model.
					
					if (moveListeners) {
						Iterator<CharSetDataModelListener> iterator = getModel().modelListeners.iterator();
						while (iterator.hasNext()) {
							iterator.next().afterModelChanged(result, getModel());
						}
					}
					else {
						modelListener.afterModelChanged(result, getModel());
					}
				}
			}
			return result;
		}
	}


	public int getSelectedIndex() {
		return selectedIndex;
	}


	public void setSelectedIndex(int selectedIndex) {
		if ((selectedIndex == -1) || Math2.isBetween(selectedIndex, 0, getModel().size() - 1)) {
			if (this.selectedIndex != selectedIndex) {
				this.selectedIndex = selectedIndex;
				repaint();
				fireSelectionChanged();
			}
		}
		else {
			throw new IllegalArgumentException("The index " + selectedIndex + " does not refer to a valid line in this area.");
		}
	}
	
	
	private void checkSelectedIndex() {
		if (getModel().size() > 0) {
			selectedIndex = Math.min(selectedIndex, getModel().size() - 1);
		}
		else {
			selectedIndex = -1;
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
		int lastColumn = getLabeledAlignmentArea().getSizeManager().getGlobalMaxSequenceLength() - 1;  //getSequenceProvider().getMaxSequenceLength() - 1;
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
	public Set<DataListType> validLocations() {
		return EnumSet.of(DataListType.TOP, DataListType.BOTTOM, DataListType.SEQUENCE);
	}


	@Override
	protected AlignmentLabelSubArea createLabelSubArea(AlignmentLabelArea owner) {
		return new CharSetNameArea(owner, this);
	}


	protected void fireSelectionChanged() {
		GenericEventObject<CharSetArea> event = new GenericEventObject<CharSetArea>(this);
		for (SelectionListener<GenericEventObject<CharSetArea>> listener : selectionListeners) {
			listener.selectionChanged(event);
		}
	}
	
	
	public Set<SelectionListener<GenericEventObject<CharSetArea>>> getSelectionListeners() {
		return selectionListeners;
	}
}
