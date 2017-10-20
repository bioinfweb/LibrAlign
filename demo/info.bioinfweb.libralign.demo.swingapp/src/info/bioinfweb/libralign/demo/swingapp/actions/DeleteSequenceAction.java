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
public class DeleteSequenceAction extends AbstractAlignmentEditorAction implements Action {	
	public DeleteSequenceAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "Delete sequence"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		SelectionModel selection = getEditor().getAlignmentArea().getSelection();

		for (int row = selection.getFirstRow(); row <= selection.getLastRow(); row++) {
			String id = getEditor().getAlignmentArea().getSequenceOrder().idByIndex(selection.getFirstRow());
			getEditor().getAlignmentArea().getAlignmentModel().removeSequence(id);
		}
	}	
}
