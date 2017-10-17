package info.bioinfweb.libralign.demo.swingapp.actions;


import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.KeyStroke;

import info.bioinfweb.libralign.demo.swingapp.SwingAlignmentEditor;



@SuppressWarnings("serial")
public class SaveAsAction extends AbstractFileAction{
	public SaveAsAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "Save As..."); 
		KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK);
		putValue(Action.ACCELERATOR_KEY, key);
	}
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		save();
	}
}
