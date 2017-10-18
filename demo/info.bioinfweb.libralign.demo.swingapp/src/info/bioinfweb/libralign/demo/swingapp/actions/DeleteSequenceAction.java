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
