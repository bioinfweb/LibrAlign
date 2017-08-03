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


import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.tic.TICPaintEvent;

import java.awt.Dimension;



/**
 * Implementation of a <i>TIC</i> component that displays a single {@link AlignmentSubArea}. It delegates its paint method
 * to {@link AlignmentSubArea#paintPart(AlignmentPaintEvent)}. {@link AlignmentSubArea#createComponent()} returns instances 
 * of this class if inherited classes do not overwrite it.
 * <p>
 * Instances of this class will only be used by an {@link AlignmentArea} is set to use subcomponents and will not be able to
 * display sequences that use up more than {@link Integer#MAX_VALUE} pixels. If <i>SWT</i> under <i>Windows</i> is used as 
 * the target toolkit, the maximum component width will currently only be 32768 pixels (due to a limitation in GUI component 
 * libraries of these operating systems). If larger sequences need to be handled, {@link AlignmentArea} should be set not to 
 * use subcomponents.
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 */
public class DefaultAlignmentSubAreaComponent extends AbstractAlignmentSubAreaComponent {
	public DefaultAlignmentSubAreaComponent(AlignmentSubArea owner) {
		super(owner);
	}


	@Override
	public void paint(TICPaintEvent event) {
		//event.getGraphics().translate(-xOffset, 0);
		//TODO Consider x-shift of graphics context due to data area width left of alignment if coordinate shift on y is implemented. (Here or/and somewhere else?)
		getOwner().paintPart(new AlignmentPaintEvent(event.getSource(), getOwner().getOwner().getOwner(),
				Math.max(0, getOwner().getOwner().columnByPaintX(event.getRectangle().getMinX())),  // first column 
				getOwner().getOwner().columnByPaintX((int)event.getRectangle().getMaxX()),  // last column
				event.getGraphics(), 
				event.getRectangle()));  // Rectangle is used instead of Rectangle2D.Double. This is possible, because the component width is anyway limited.
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
		return new Dimension((int)Math.round(getOwner().getOwner().getOwner().getGlobalMaxNeededWidth()), 
				(int)Math.round(getOwner().getHeight()));  
	}
}
