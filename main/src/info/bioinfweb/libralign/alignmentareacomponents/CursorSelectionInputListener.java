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
package info.bioinfweb.libralign.alignmentareacomponents;


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
import info.bioinfweb.libralign.AlignmentContentArea;
import info.bioinfweb.libralign.selection.SelectionModel;
import info.bioinfweb.libralign.sequenceprovider.SequenceDataProvider;



/**
 * Mouse and key listener modifying the selection and cursor if a cursor is used.
 * 
 * @author Ben St&ouml;ver
 * @since 0.2.0
 */
public class CursorSelectionInputListener extends TICMouseAdapter implements TICMouseListener, TICKeyListener {
	private AlignmentContentArea owner;
	
	
	public CursorSelectionInputListener(AlignmentContentArea owner) {
		super();
		this.owner = owner;
	}


	public AlignmentContentArea getOwner() {
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
			Point columnRow = calculateColumnRow((SequenceArea)event.getSource(), event.getComponentX(), event.getComponentY());
			getOwner().getSelection().setNewCursorPosition(columnRow.x, columnRow.y, 1);  // Height is always set to 1 on a mouse click.
		}
	}


	@Override
	public void mouseDragged(TICMouseEvent event) {
		if ((event.isMouseButton1Down()) && (event.getSource() instanceof SequenceArea)) {
			Point columnRow = calculateColumnRow((SequenceArea)event.getSource(), event.getComponentX(), event.getComponentY());
			getOwner().getSelection().setSelectionEnd(columnRow.x, columnRow.y);
		}
	}

	
	@Override
	public void keyPressed(TICKeyEvent event) {
		SelectionModel selection = getOwner().getSelection();
		switch (event.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (event.isShiftDown()) {
					if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
						selection.setSelectionEnd(selection.getCursorColumn() - 1, selection.getCursorRow());
					}
					else { // Below selection start
						selection.setSelectionEnd(selection.getCursorColumn() - 1, 
								selection.getCursorRow() + selection.getCursorHeight() - 1);
					}
				}
				else {
					selection.setNewCursorColumn(selection.getCursorColumn() - 1);
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (event.isShiftDown()) {
					if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
						selection.setSelectionEnd(selection.getCursorColumn() + 1, selection.getCursorRow());
					}
					else { // Below selection start
						selection.setSelectionEnd(selection.getCursorColumn() + 1, 
								selection.getCursorRow() + selection.getCursorHeight() - 1);
					}
				}
				else {
					selection.setNewCursorColumn(selection.getCursorColumn() + 1);
				}
				break;
			case KeyEvent.VK_UP:
				if (event.isShiftDown()) {
					if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
						selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() - 1);
					}
					else {  // Below selection start
						selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() + selection.getCursorHeight() - 2);
					}
				}
				else {
					selection.setNewCursorRow(selection.getCursorRow() - 1);
				}
				break;
			case KeyEvent.VK_DOWN:
				if (event.isShiftDown()) {
					if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
						selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() + 1);
					}
					else {  // Below selection start
						selection.setSelectionEnd(selection.getCursorColumn(), selection.getCursorRow() + selection.getCursorHeight());  // - 1 + 1
					}
				}
				else {
					selection.setNewCursorRow(selection.getCursorRow() + 1);
				}
				break;
			case KeyEvent.VK_HOME:
				if (event.isShiftDown()) {
					if (event.isMenuShortcutKeyDown()) {
						selection.setSelectionEnd(0, 0);
					}
					else {
						if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
							selection.setSelectionEnd(0, selection.getCursorRow());
						}
						else { // Below selection start
							selection.setSelectionEnd(0, 
									selection.getCursorRow() + selection.getCursorHeight() - 1);
						}
					}
				}
				else if (event.isMenuShortcutKeyDown()) {
					selection.setNewCursorPosition(0, 0);
				}
				else {
					selection.setNewCursorColumn(0);
				}
				break;
			case KeyEvent.VK_END:
				SequenceDataProvider provider = getOwner().getSequenceProvider();
				if (event.isShiftDown()) {
					if (event.isMenuShortcutKeyDown()) {
						selection.setSelectionEnd(provider.getMaxSequenceLength(), provider.getSequenceCount() - 1);
					}
					else {
						if (selection.getCursorRow() < selection.getStartRow()) {  // Above selection start
							selection.setSelectionEnd(provider.getMaxSequenceLength(), selection.getCursorRow());
						}
						else { // Below selection start
							selection.setSelectionEnd(provider.getMaxSequenceLength(), 
									selection.getCursorRow() + selection.getCursorHeight() - 1);
						}
					}
				}
				else if (event.isMenuShortcutKeyDown()) {
					selection.setNewCursorPosition(provider.getMaxSequenceLength(), provider.getSequenceCount() - 1);
				}
				else {
					selection.setNewCursorColumn(provider.getMaxSequenceLength());
				}
			default:
				if (event.isMenuShortcutKeyDown() && (event.getKeyCode() == KeyEvent.VK_A)) {  // Select all
					selection.selectAll();
				}
				else {
					//TODO Delegate to TokenSet for inserting new tokens here
				}
				break;
		}
	}


	@Override
	public void keyReleased(TICKeyEvent event) {}
}
