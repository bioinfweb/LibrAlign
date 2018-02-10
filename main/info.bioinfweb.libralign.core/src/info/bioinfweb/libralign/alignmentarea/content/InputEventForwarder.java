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
package info.bioinfweb.libralign.alignmentarea.content;


import java.util.Iterator;

import info.bioinfweb.tic.input.TICKeyEvent;
import info.bioinfweb.tic.input.TICMouseEvent;
import info.bioinfweb.tic.input.TICMouseListener;
import info.bioinfweb.tic.input.TICKeyListener;
import info.bioinfweb.tic.input.TICMouseWheelEvent;
import info.bioinfweb.tic.input.TICMouseWheelListener;



/**
 * Forwards key and mouse events received by an {@link AlignmentContentArea} to the respective 
 * {@link AlignmentSubArea}. For key events that is the first sequence area currently containing 
 * the cursor and for mouse events the are containing the point where the mouse event occurred.
 * <p>
 * Instances of this class are used when {@link AlignmentSubArea}s paint directly on the toolkit
 * component of {@link AlignmentContentArea} (currently only optionally the case in <i>SWT</i>)
 * to allow the subareas to receive input events, even if they have no associated toolkit 
 * component. (Since no toolkit components for the subareas exists, it is not possible to 
 * determine the subarea owning the focus, which is why the alignment cursor position defines 
 * the receiver subarea for key events.)
 * <p>
 * Events are not forwarded directly but copies are created that reference the respective 
 * {@link AlignmentSubArea} as the source and mouse event coordinates are transformed to the
 * coordinate system origin used by the respective {@link AlignmentSubArea}.
 * <p>
 * This class is used by <i>LibrAlign</i> components internally. There is usually no need to
 * reference it directly in application code. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.5.0
 * @bioinfweb.module info.bioinfweb.libralign.core
 */
public class InputEventForwarder implements TICKeyListener, TICMouseListener, TICMouseWheelListener {
	private AlignmentContentArea owner;	
	
	
	public InputEventForwarder(AlignmentContentArea owner) {
		super();
		this.owner = owner;
	}
	
	
	public AlignmentContentArea getOwner() {
		return owner;
	}


	private boolean dispatchKeyEvent(TICKeyEvent event) {
		AlignmentSubArea area = null;
		if (getOwner().getOwner().getAlignmentModel().getSequenceCount() > 0) {  // Otherwise no sequence area exists
			area = getOwner().getSequenceAreaByRow(getOwner().getOwner().getSelection().getFirstRow());  // Selected index should always be in bounds.
		}
		else {  // Forward event to the first data area instead:
			Iterator<AlignmentSubArea> iterator = getOwner().subAreaIterator();
			if (iterator.hasNext()) {
				area = iterator.next();
			}
		}
		
		if (area != null) {
			return area.dispatchEvent(event.cloneWithNewSource(area));
		}
		else {
			return false;
		}
	}


	@Override
	public boolean keyPressed(TICKeyEvent event) {
		return dispatchKeyEvent(event);
	}
	
	
	@Override
	public boolean keyReleased(TICKeyEvent event) {
		return dispatchKeyEvent(event);
	}

	
	private boolean dispatchMouseEvent(TICMouseEvent event) {
		AlignmentSubAreaInfo info = getOwner().getAreaInfoByPaintY(event.getComponentY());
		if (info != null) {
			return info.getArea().dispatchEvent(event.cloneWithNewSourceTranslated(info.getArea(), 0, (int)Math.round(info.getY())));
		}
		else {
			return false;
		}
	}
	
	
	@Override
	public boolean mousePressed(TICMouseEvent event) {
		return dispatchMouseEvent(event);
	}

	
	@Override
	public boolean mouseReleased(TICMouseEvent event) {
		return dispatchMouseEvent(event);
	}

	
	@Override
	public boolean mouseEntered(TICMouseEvent event) {
		return dispatchMouseEvent(event);
	}

	
	@Override
	public boolean mouseExited(TICMouseEvent event) {
		return dispatchMouseEvent(event);
	}

	
	@Override
	public boolean mouseMoved(TICMouseEvent event) {
		return dispatchMouseEvent(event);
	}

	
	@Override
	public boolean mouseDragged(TICMouseEvent event) {
		return dispatchMouseEvent(event);
	}
	
	
	@Override
	public boolean mouseWheelMoved(TICMouseWheelEvent event) {
		return dispatchMouseEvent(event);
	}
}
