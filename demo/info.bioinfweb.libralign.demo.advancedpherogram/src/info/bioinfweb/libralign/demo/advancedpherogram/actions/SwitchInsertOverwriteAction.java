package info.bioinfweb.libralign.demo.advancedpherogram.actions;

import java.awt.event.ActionEvent;

import info.bioinfweb.libralign.demo.advancedpherogram.Application;

import javax.swing.Action;

public class SwitchInsertOverwriteAction extends AbstractAdvancedPherogramAction implements Action{

	public SwitchInsertOverwriteAction(Application app) {
		super(app);
		putValue(Action.NAME, "Switch insert/overwrite"); 
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getApplication().getAlignmentArea().getEditSettings().toggleInsert();
		
	}

}
