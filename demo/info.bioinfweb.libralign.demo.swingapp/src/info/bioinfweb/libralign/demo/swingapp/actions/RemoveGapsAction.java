package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;


import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.alignmentarea.AlignmentArea;
import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;




public class RemoveGapsAction extends AbstractAlignmentAreaAction implements Action {
	public RemoveGapsAction(AlignmentArea area) {
		super(area);
		putValue(Action.NAME, "Remove gaps"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_R);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('R', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		SelectionModel selection = area.getSelection();
		int indexFirstColumn = selection.getFirstColumn();

		for (int row = selection.getFirstRow(); row <= selection.getLastRow(); row++) {
			String id = area.getSequenceOrder().idByIndex(row);
			int indexLastColumn = Math.min(selection.getLastColumn(), area.getAlignmentModel().getSequenceLength(id) - 1);

			int columnPosition = indexFirstColumn;
			for (int i = indexFirstColumn; i <= indexLastColumn; i++) {				
				if (area.getAlignmentModel().getTokenAt(id, columnPosition).equals('-')) {
					area.getAlignmentModel().removeTokenAt(id, columnPosition);
				} 
				else {
					columnPosition++;
				}
			}				
		}
	}
}
