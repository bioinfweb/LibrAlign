package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;




public class DeleteSequenceAction extends AbstractAlignmentEditorAction implements Action {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public DeleteSequenceAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "Delete sequence"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String sequenceID = getEditor().getAlignmentArea().getAlignmentModel().sequenceIDsByName(JOptionPane.showInputDialog("Name of sequence to be deleted")).iterator().next();
		if (getEditor().getAlignmentArea().getAlignmentModel().containsSequence(sequenceID)) {
			getEditor().getAlignmentArea().getAlignmentModel().removeSequence(sequenceID);
		} else {
			JOptionPane.showMessageDialog(getEditor().getFrame(), "Sequence not found");
		}		
	}	
}
