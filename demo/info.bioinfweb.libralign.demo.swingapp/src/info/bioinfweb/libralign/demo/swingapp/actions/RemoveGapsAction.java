package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.alignmentarea.selection.SelectionModel;
import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;




public class RemoveGapsAction extends AbstractAlignmentEditorAction implements Action {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


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
