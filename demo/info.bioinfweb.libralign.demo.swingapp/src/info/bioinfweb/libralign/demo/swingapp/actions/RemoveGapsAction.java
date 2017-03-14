package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;



public class RemoveGapsAction extends AbstractAlignmentAreaAction implements Action {

	public RemoveGapsAction(AlignmentArea area) {
		super(area);
		putValue(Action.NAME, "Remove gaps"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

//	@Override
//	public void actionPerformed(ActionEvent e) {
//		if (sequence.contains("-")) {
//			String removeGaps = area.getAlignmentModel().removeTokenAt(sequenceID, index);
//		}
		
		
	}

}
