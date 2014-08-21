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
package info.bioinfweb.libralign.alignmentareacomponents.inputlisteners;


import java.awt.Point;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;

import info.bioinfweb.commons.tic.TargetToolkit;
import info.bioinfweb.commons.tic.input.TICKeyEvent;
import info.bioinfweb.commons.tic.input.TICKeyListener;
import info.bioinfweb.commons.tic.input.TICMouseAdapter;
import info.bioinfweb.commons.tic.input.TICMouseEvent;
import info.bioinfweb.commons.tic.input.TICMouseListener;
import info.bioinfweb.libralign.AlignmentArea;
import info.bioinfweb.libralign.alignmentareacomponents.SequenceArea;
import info.bioinfweb.libralign.cursor.AlignmentCursor;
import info.bioinfweb.libralign.selection.SelectionModel;



/**
 * Mouse and key listener modifying the selection and cursor if a cursor is used.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CursorSelectionInputListener extends TICMouseAdapter implements TICMouseListener, TICKeyListener {
	private AlignmentArea owner;
	private Point lastClickColumnRow = null;
	
	
	public CursorSelectionInputListener(AlignmentArea owner) {
		super();
		this.owner = owner;
	}


	public AlignmentArea getOwner() {
		return owner;
	}
	
	
	private Point calculateColumnRow(SequenceArea source, int x, int y) {
		return new Point(getOwner().columnByPaintX(x), getOwner().rowByPaintY(getOwner().alignmentPartY(source, y)));
	}
	
	
	private void setSwingFocus(TICMouseEvent event) {
		if (event.getSource().getCurrentToolkit().equals(TargetToolkit.SWING)) {
			((JComponent)event.getSource().getToolkitComponent()).requestFocus();
		}
	}


	@Override
	public void mousePressed(TICMouseEvent event) {
		setSwingFocus(event);  // Necessary for Swing components to react to keyboard events.
		if (event.getClickCount() > 1) {
			// Handle double click events here
		}
		else if ((event.isMouseButton1Down()) && (event.getSource() instanceof SequenceArea)) {
			getOwner().getSelection().clear();
			
			lastClickColumnRow = calculateColumnRow((SequenceArea)event.getSource(), event.getComponentX(), event.getComponentY());
			AlignmentCursor cursor = getOwner().getCursor();
			cursor.setColumn(lastClickColumnRow.x);
			cursor.setRow(lastClickColumnRow.y);
			cursor.setHeight(1);
		}
	}
	

	@Override
	public void mouseReleased(TICMouseEvent event) {
		lastClickColumnRow = null;
	}


	@Override
	public void mouseDragged(TICMouseEvent event) {
		if ((event.isMouseButton1Down()) && (event.getSource() instanceof SequenceArea)) {
			if (lastClickColumnRow == null) {  // By some fast mouse movements lastClickColumnRow could still be null here.
				mousePressed(event);
			}
			Point columnRow = calculateColumnRow((SequenceArea)event.getSource(), event.getComponentX(), event.getComponentY());
			
			SelectionModel selection = getOwner().getSelection();
			if (selection.isEmpty() && (lastClickColumnRow != null) && (columnRow.x != lastClickColumnRow.x)) { 
				selection.getColumnSelection().setNewSelection(lastClickColumnRow.x);
				selection.getRowSelection().setNewSelection(lastClickColumnRow.y);
		  }
		  if (!selection.isEmpty()) {  // Do not create new selection before the upper condition was met.
				selection.getColumnSelection().extendSelectionTo(columnRow.x);
				selection.getRowSelection().extendSelectionTo(columnRow.y);
		  }
			
			AlignmentCursor cursor = getOwner().getCursor();
			if (lastClickColumnRow.x < columnRow.x) {
				cursor.setColumn(columnRow.x + 1);
			}
			else {
				cursor.setColumn(columnRow.x);
			}
			if (lastClickColumnRow.y > columnRow.y) {
				cursor.setRow(columnRow.y);
				cursor.setHeight(lastClickColumnRow.y - cursor.getRow() + 1);
			}
			else {
				cursor.setHeight(columnRow.y - cursor.getRow() + 1);
			}
		}
	}


	@Override
	public void keyPressed(TICKeyEvent event) {
		AlignmentCursor cursor = getOwner().getCursor();
		switch (event.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				cursor.setColumn(cursor.getColumn() - 1);
				if (event.isShiftDown()) {  //TODO Does not work this way
					getOwner().getSelection().getColumnSelection().extendSelectionTo(cursor.getColumn());
				}
				break;
			case KeyEvent.VK_RIGHT:
				cursor.setColumn(cursor.getColumn() + 1);
				if (event.isShiftDown()) {  //TODO Does not work this way
					getOwner().getSelection().getColumnSelection().extendSelectionTo(cursor.getColumn());
				}
				break;
			case KeyEvent.VK_UP:
				cursor.setRow(cursor.getRow() - 1);
				if (event.isShiftDown()) {  //TODO Does not work this way
					getOwner().getSelection().getRowSelection().extendSelectionTo(cursor.getRow());
				}
				break;
			case KeyEvent.VK_DOWN:
				cursor.setRow(cursor.getRow() + 1);
				if (event.isShiftDown()) {  //TODO Does not work this way
					getOwner().getSelection().getRowSelection().extendSelectionTo(cursor.getRow());
				}
				break;
			default:
				break;
		}
	}


	@Override
	public void keyReleased(TICKeyEvent event) {}
}