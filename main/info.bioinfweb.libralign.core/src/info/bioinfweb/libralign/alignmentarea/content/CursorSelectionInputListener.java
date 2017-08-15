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


import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import info.bioinfweb.tic.TICComponent;
import info.bioinfweb.tic.TargetToolkit;
import info.bioinfweb.tic.input.TICInputEvent;
import info.bioinfweb.tic.input.TICKeyEvent;
import info.bioinfweb.tic.input.TICKeyListener;
import info.bioinfweb.tic.input.TICMouseAdapter;
import info.bioinfweb.tic.input.TICMouseEvent;
import info.bioinfweb.tic.input.TICMouseListener;
import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.model.AlignmentModel;



/**
 * Mouse and key listener modifying the selection and cursor if a cursor is used.
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
		double y = event.getComponentY();
		if (isEventFromSequenceArea(event)) {
			y = getOwner().getContentArea().alignmentPartY(getSequenceArea(event.getSource()), y);
		}
		return new Point(getOwner().getContentArea().columnByPaintX(event.getComponentX()),	getOwner().getContentArea().rowByPaintY(y));
	}
	
	
	private void setSwingFocus(TICMouseEvent event) {
		if (event.getSource().getCurrentToolkit().equals(TargetToolkit.SWING)) {
			((JComponent)event.getSource().getToolkitComponent()).requestFocus();
		}
	}


	private boolean isEventFromSequenceArea(TICInputEvent event) {
		return (event.getSource() instanceof DefaultAlignmentSubAreaComponent) && 
				(((DefaultAlignmentSubAreaComponent)event.getSource()).getOwner() instanceof SequenceArea);
	}
	
	
	private SequenceArea getSequenceArea(TICComponent component) {
		return (SequenceArea)((DefaultAlignmentSubAreaComponent)component).getOwner();
	}
	
	
	@Override
	public boolean mousePressed(TICMouseEvent event) {
		setSwingFocus(event);  // Necessary for Swing components to react to keyboard events.
		if (event.getClickCount() > 1) {
			// Handle double click events here
		}
		else if (event.isMouseButton1Down()) {
			Point columnRow = calculateColumnRow(event);
			getOwner().getSelection().setNewCursorPosition(columnRow.x, columnRow.y, 1);  // Height is always set to 1 on a mouse click.
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
