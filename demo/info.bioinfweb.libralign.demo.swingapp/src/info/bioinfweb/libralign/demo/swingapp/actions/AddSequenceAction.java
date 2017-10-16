package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;




@SuppressWarnings("serial")
public class AddSequenceAction extends AbstractAlignmentEditorAction implements Action {
	public AddSequenceAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "Add sequence"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('A', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		getEditor().getAlignmentArea().getAlignmentModel().addSequence(JOptionPane.showInputDialog("New sequence name"));
		getEditor().setChanged(true);
	}
}
