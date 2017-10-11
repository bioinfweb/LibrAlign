package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;




public class DeleteSequenceAction extends AbstractAlignmentAreaAction implements Action {	
	public DeleteSequenceAction(AlignmentArea area) {
		super(area);
		putValue(Action.NAME, "Delete sequence"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_D);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('D', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		String sequenceID = area.getAlignmentModel().sequenceIDsByName(JOptionPane.showInputDialog("Name of sequence to be deleted")).iterator().next();
		if (area.getAlignmentModel().containsSequence(sequenceID)) {
			area.getAlignmentModel().removeSequence(sequenceID);
		} else {
			JOptionPane.showMessageDialog(frame, "Sequence not found");
		}		
	}	
}
