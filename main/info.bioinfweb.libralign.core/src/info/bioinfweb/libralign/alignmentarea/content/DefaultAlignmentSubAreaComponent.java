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
package info.bioinfweb.libralign.alignmentarea.content;


import java.awt.Dimension;
import java.awt.Rectangle;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TICPaintEvent;



/**
 * Implementation of a <i>TIC</i> component that displays a single {@link AlignmentSubArea}. It delegates its paint method
 * to {@link AlignmentSubArea#paintPart(AlignmentPaintEvent)} using a respective coordinate transformation. Instances of 
 * this class are returned by {@link AlignmentSubArea#createComponent()}, if inherited classes do not overwrite this method.
 * <p>
 * Instances of this class will only be used by an {@link AlignmentArea} is set to use subcomponents and will not be able to
 * display sequences that use up more than {@link Integer#MAX_VALUE} pixels. If <i>SWT</i> under <i>Windows</i> is used as 
 * the target toolkit, the maximum component width will only be 32768 pixels. If larger sequences need to be handled, 
 * {@link AlignmentArea} should be set not to use subcomponents.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class DefaultAlignmentSubAreaComponent extends TICComponent {
	private AlignmentSubArea owner;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param owner the parent alignment subarea that shall be displayed by this component
	 */
	public DefaultAlignmentSubAreaComponent(AlignmentSubArea owner) {
		super();
		this.owner = owner;
	}


	/**
	 * Returns the alignment subarea that is displayed using this component.
	 * 
	 * @return the parent alignment subarea
	 */
	public AlignmentSubArea getOwner() {
		return owner;
	}


	@Override
	public void paint(TICPaintEvent event) {
		int firstColumn = Math.max(0, getOwner().getOwner().columnByPaintX(event.getRectangle().x));
		int xOffset = getOwner().getOwner().paintXByColumn(firstColumn);
		event.getGraphics().translate(-xOffset, 0);  //TODO +1?
		getOwner().paintPart(new AlignmentPaintEvent(event.getSource(), getOwner().getOwner().getOwner(),
				firstColumn, getOwner().getOwner().columnByPaintX((int)event.getRectangle().getMaxX()), 
				event.getGraphics(), 
				new Rectangle(event.getRectangle().x - xOffset, event.getRectangle().y,  //TODO +1?
						event.getRectangle().width, event.getRectangle().height)));
	}
	

	@Override
	public Dimension getSize() {
		// TODO Implement similar to DataArea.getSize()
		return null;
	}
}
