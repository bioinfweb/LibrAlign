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
package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.implementations.PackedAlignmentModel;
import info.bioinfweb.libralign.model.tokenset.CharacterTokenSet;



@SuppressWarnings("serial")
public class NewAction extends AbstractFileAction {
	public NewAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "New"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	
	
	public static AlignmentModel<Character> createAlignmentModel() {
		return new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance(false));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (handleUnsavedChanges()) {
			getEditor().getAlignmentArea().setAlignmentModel(createAlignmentModel(), true);
			getEditor().setFile(null);
			getEditor().setFormat(SwingAlignmentEditor.DEFAULT_FORMAT);
			getEditor().setChanged(false);
		}
	}
}
