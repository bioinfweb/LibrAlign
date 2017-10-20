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
package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;



@SuppressWarnings("serial")
public class RemoveGapsAction extends AbstractAlignmentEditorAction implements Action {
	public RemoveGapsAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "Remove gaps"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SelectionModel selection = getEditor().getAlignmentArea().getSelection();
		int indexFirstColumn = selection.getFirstColumn();

		for (int row = selection.getFirstRow(); row <= selection.getLastRow(); row++) {
			String id = getEditor().getAlignmentArea().getSequenceOrder().idByIndex(row);
			int indexLastColumn = Math.min(selection.getLastColumn(), getEditor().getAlignmentArea().getAlignmentModel().getSequenceLength(id) - 1);

			int columnPosition = indexFirstColumn;
			for (int i = indexFirstColumn; i <= indexLastColumn; i++) {				
				if (getEditor().getAlignmentArea().getAlignmentModel().getTokenAt(id, columnPosition).equals('-')) {
					getEditor().getAlignmentArea().getAlignmentModel().removeTokenAt(id, columnPosition);
				} 
				else {
					columnPosition++;
				}
			}				
		}
	}
}
