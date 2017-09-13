/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.alignmentarea.content;


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelArea;
import info.bioinfweb.libralign.alignmentarea.label.AlignmentLabelSubArea;
import info.bioinfweb.libralign.alignmentarea.label.DefaultLabelSubArea;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;



/**
 * All GUI components that are part of an {@link AlignmentContentArea} should inherit from this class.
 * The method {@link #paintPart(int, Graphics2D, Rectangle)} is responsible for drawing this component
 * and is called by the <i>TIC</i> method {@link #paint(TICPaintEvent)} internally.
 * 
 * @author Ben St&ouml;ver
 * @since 0.0.0
 */
public abstract class AlignmentSubArea extends TICComponent {
	private AlignmentLabelSubArea labelSubArea = null;
	private AlignmentContentArea owner = null;

	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the alignment content area that will contain this instance
	 */
	public AlignmentSubArea(AlignmentContentArea owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the alignment content area that displays this data area.
	 * 
	 * @return the owning alignment content area
	 */
	public AlignmentContentArea getOwner() {
		return owner;
	}
	
	
	/**
	 * This method delegates to {@link #paintPart(AlignmentPaintEvent)}. Classes inheriting from this class should 
	 * overwrite {@link #paintPart(AlignmentPaintEvent)} instead of this method.
	 *  
	 * @param event the paint event providing the graphics context and information on which part of the 
	 *        component should be repainted
	 * @see info.bioinfweb.tic.TICComponent#paint(info.bioinfweb.tic.TICPaintEvent)
	 */
	@Override
	public void paint(TICPaintEvent event) {
		//event.getGraphics().translate(-xOffset, 0);
		//TODO Consider x-shift of graphics context due to data area width left of alignment if coordinate shift on y is implemented. (Here or/and somewhere else?)
		paintPart(new AlignmentPaintEvent(event.getSource(), getOwner().getOwner(),
				Math.max(0, getOwner().columnByPaintX(event.getRectangle().getMinX())),  // first column 
				getOwner().columnByPaintX(event.getRectangle().getMaxX()),  // last column
				event.getGraphics(), 
				event.getRectangle()));  // Rectangle is used instead of Rectangle2D.Double. This is possible, because the component width is anyway limited.
	}
	

	public void repaint() {
		if (getOwner().hasToolkitComponent() && !getOwner().getToolkitComponent().hasSubcomponents()) {
			getOwner().repaint();  // The repaint() method of the toolkit component must not be used here directly, because AlignmentContentArea.repaint() may combine several subsequent paint operations.
		}
		else {
			super.repaint();
		}
	}
	
	
	/**
	 * Returns the size of the component depending on the return values of {@link #getLength()}, {@link #getHeight()}
	 * and the maximum length before the first alignment position in the associated alignment area. (That means this
	 * method might return a different dimension depending on the {@link AlignmentArea} is it contained in.)
	 * 
	 * @return the (minimal) width and height of this component
	 */
	@Override
	public Dimension getSize() {
		return new Dimension((int)Math.round(getOwner().getOwner().getGlobalMaxNeededWidth()), 
				(int)Math.round(getHeight()));  
	}

	
	/**
	 * This method can be overwritten to provide a specific implementation for labeling the implementing data area.
	 * <p>
	 * This default implementation always returns an instance of {@link DefaultLabelSubArea}.
	 * 
	 * @param owner the alignment label area that can be set as the owner of the returned component.
	 * @return a new instance of {@link DefaultLabelSubArea} linked to this instance
	 */
	protected AlignmentLabelSubArea createLabelSubArea(AlignmentLabelArea owner) {
		return new DefaultLabelSubArea(owner, this);
	}
	
	
	/**
	 * Returns the GUI component that displays the row label for this part of the alignment.
	 * 
	 * @return a GUI component displaying the according label information
	 */
	public AlignmentLabelSubArea getLabelSubArea() {
		if (labelSubArea == null) {
			labelSubArea = createLabelSubArea(getOwner().getOwner().getLabelArea());
		}
		return labelSubArea;
	}
	
	
	/**
	 * Returns the height in pixels considering the current zoom factor this component needs.
	 * 
	 * @return a {@code double} value > 0
	 */
	public abstract double getHeight();


	/**
	 * Implementations of this method perform the painting of a part of the component. It is used when an
	 * {@link AlignmentArea} is set to contain no subcomponents but to paint its contents directly.
	 * <p>
	 * This default implementation always throws an {@link UnsupportedOperationException}. Note that 
	 * therefore inherited classes must either overwrite this method or {@link #createComponent()} in 
	 * order to return an instance there that does not delegate to this method.
	 * <p>
	 * Note that the aim of this method is to allow painting very wide components, which may have a width
	 * larger than {@link Integer#MAX_VALUE}. Therefore painting coordinates are stored as {@code double}
	 * values (using {@link Graphics2D} and {@link Rectangle2D.Double}). Implementations should not 
	 * calculate pixel coordinates as {@code int}s at any time to avoid overflows, but only use
	 * {@code double} (or possibly {@code long} if necessary).
	 * 
	 * @param event the paint event providing information on the area to be painted, the graphics context
	 *        and tool methods.
	 * @since 0.5.0
	 */
	public void paintPart(AlignmentPaintEvent event) {
		throw new UnsupportedOperationException("This class provides no implementation for direct component painting.");
	}
}
