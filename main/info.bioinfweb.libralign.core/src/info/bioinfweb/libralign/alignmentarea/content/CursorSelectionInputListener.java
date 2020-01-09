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


import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.tic.input.TICKeyEvent;
import info.bioinfweb.tic.input.TICKeyListener;
import info.bioinfweb.tic.input.TICMouseAdapter;
import info.bioinfweb.tic.input.TICMouseEvent;
import info.bioinfweb.tic.input.TICMouseListener;



/**
 * Mouse and key listener modifying the selection and cursor if a cursor is used.
 * <p>
 * Note that this listener is only meant to be registered on instances of {@link SequenceArea} (or possibly 
 * instances of inherited classes). Registering it to other instances may cause {@link ClassCastException}s.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CursorSelectionInputListener extends TICMouseAdapter implements TICMouseListener, TICKeyListener {
	private AlignmentArea owner;
	
	
	public CursorSelectionInputListener(AlignmentArea owner) {
		super();
		this.owner = owner;
	}

	
	public AlignmentArea getOwner() {
		return owner;
	}
	
	
	private Point calculateColumnRow(TICMouseEvent event) {
		int row;
		SequenceArea area = (SequenceArea)event.getSource();  // This listener is only registered in sequence areas.
		if (event.getSource().hasToolkitComponent()) {  // This method is used if subcomponents are present, because mouse dragging events do not necessarily come from subareas that contain the current mouse position.
			row = getOwner().getContentArea().rowByPaintY(event.getComponentY() + 
					area.getToolkitComponent().getLocationInParent().getY());
		}
		else {  // This method is used for possible direct painting in SWT. There the source area will always contain the mouse coordinates.
			row = getOwner().getSequenceOrder().indexByID(area.getSequenceID());
		}
		return new Point(getOwner().getContentArea().columnByPaintX(event.getComponentX()), row);
	}
	
	
	@Override
	public boolean mousePressed(TICMouseEvent event) {
		if (event.getClickCount() > 1) {
			// Handle double click events here
		}
		else if (event.isMouseButton1Down()) {
			Point columnRow = calculateColumnRow(event);
			getOwner().getSelection().setNewCursorPosition(columnRow.x, columnRow.y, 1);  // Height is always set to 1 on a mouse click.
		}
		
		if (event.getSource().hasToolkitComponent()) {
			event.getSource().getToolkitComponent().requestFocus();  // Make sure that this component receives the keyboard events from now on.
		}
		
		return true;  // Forwarding to parent is not necessary.
	}


	@Override
	public boolean mouseDragged(TICMouseEvent event) {
		if (event.isMouseButton1Down()) {
			Point columnRow = calculateColumnRow(event);
			getOwner().getSelection().setSelectionEnd(columnRow.x, columnRow.y);
		}
		return true;  // Forwarding to parent is not necessary.
	}

	
	@Override
	public boolean keyPressed(TICKeyEvent event) {
		KeyStroke keyStroke = KeyStroke.getKeyStroke(event.getKeyCode(), event.getModifiers());
		Action action = getOwner().getContentArea().getActionMap().get(keyStroke);
		if (action != null) {  // Execute action:
			action.actionPerformed(new ActionEvent(event.getSource(), ActionEvent.ACTION_PERFORMED, "", System.currentTimeMillis(), 
					event.getModifiers()));
		}
		else {  // Insert token:
			AlignmentModel<?> model = getOwner().getAlignmentModel();
			Object token = model.getTokenSet().tokenByKeyStroke(keyStroke);
			if (token == null) {
				token = model.getTokenSet().tokenByRepresentation(Character.toString(event.getKeyCharacter()));
			}
			
			if (token != null) {
				if (getOwner().getEditSettings().isInsert()) {
					if (!getOwner().getActionProvider().insertToken(token)) {
						Toolkit.getDefaultToolkit().beep();
					}
				}
				else {
					if (!getOwner().getActionProvider().overwriteWithToken(token)) {
						Toolkit.getDefaultToolkit().beep();
					}
				}
			}
		}
		
		return true;  // Forwarding to parent is not necessary.
	}


	@Override
	public boolean keyReleased(TICKeyEvent event) {
		return true;  // Forwarding to parent is not necessary.
	}
}
