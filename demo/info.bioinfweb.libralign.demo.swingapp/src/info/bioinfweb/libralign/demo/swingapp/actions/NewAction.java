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
public class NewAction extends AbstractAlignmentEditorAction {
	public NewAction(SwingAlignmentEditor editor) {
		super(editor);
		putValue(Action.NAME, "New"); 
		putValue(Action.MNEMONIC_KEY, KeyEvent.VK_N);
		putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke('N', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
	}
	
	
	public static AlignmentModel<Character> createAlignmentModel() {
		return new PackedAlignmentModel<Character>(CharacterTokenSet.newNucleotideInstance());
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		//TODO Ask to save
		
		getEditor().getAlignmentArea().setAlignmentModel(createAlignmentModel(), true);
		getEditor().setFile(null);
		getEditor().setFormat(SwingAlignmentEditor.DEFAULT_FORMAT);
		getEditor().setChanged(false);
	}
}
