package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;



@SuppressWarnings("serial")
public class SaveAction extends AbstractSaveAction{
	public SaveAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "Save"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_S);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (getEditor().getFile() == null) {
			if (promptFileName()) {
				writeFile();
			}
		}
		else {
			writeFile();
		}
	}
}
