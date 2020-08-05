/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2018  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.dataarea.implementations.charset;


import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColorChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetColumnChangeEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.events.CharSetRenamedEvent;
import info.bioinfweb.libralign.dataarea.implementations.charset.undo.CharSetDataModelUndoListener;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.data.DataModel;
import info.bioinfweb.libralign.model.undo.EditRecorder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.map.ListOrderedMap;



/**
 * The data model for a {@link CharSetArea}. It consists of an ordered map in which each character set is referenced by an
 * unique ID. Note that this ID differs from the mutable character set name that is returned by {@link CharSet#getName()}.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class CharSetDataModel extends ListOrderedMap<String, CharSet> implements DataModel<CharSetDataModelListener> {
	//TODO Should a link to the associated AlignmentModel be used to determine the length of the alignment and possibly cut character sets? 
	//     - Can NeXML store character sets that are longer than an alignment?
	//     - Otherwise this class would be an example for a DataModel that is fully independent of the AlignmentModel.
	
	private AlignmentModel<?> alignmentModel;
	protected Set<CharSetDataModelListener> modelListeners = new HashSet<CharSetDataModelListener>();
	private CharSetDataModelListener undoListener = null;
	private EditRecorder<?, ?> editRecorder = null;
	
	
	public CharSetDataModel(AlignmentModel<?> alignmentModel) {  //TODO Should an alternative constructor without alignmentModel be offered/may alignmentModel be null or will other classes in the future rely on a non-null value to be returned by getAlignmentModel()? (This class currently does not make use of this property.) 
		super();
		this.alignmentModel = alignmentModel;
		editRecorder = new EditRecorder(alignmentModel);
		this.ensureUndoListener(editRecorder);
	}


	@Override
	public AlignmentModel<?> getAlignmentModel() {
		return alignmentModel;
	}


	public CharSet getByName(String name) {
		for (CharSet charSet : valueList()) {
			if (charSet.getName().equals(name)) {
				return charSet;
			}
		}
		return null;
	}
	

	@Override
	public CharSet put(String key, CharSet value) {
		// All other methods that add content delegate here.

		value.getModels().add(this);
		CharSet result = super.put(key, value);
		if (result == null) {
			fireAfterCharSetChange(new CharSetChangeEvent(this, true, key, value, true));
		}
		else {
			fireAfterCharSetChange(new CharSetChangeEvent(this, true, key, value, result));
		}
		return result;
	}


	@Override
	public CharSet remove(Object key) {
		// remove(index) and valueList().subList().clear() delegate here.
		
		CharSet result = super.remove(key);
		if (result != null) {
			result.getModels().remove(this);
			fireAfterCharSetChange(new CharSetChangeEvent(this, true, key.toString(), result, false));
		}
		return result;
	}


	@Override
	public void clear() {
		// valueList().clear() delegates here.
		
		Map<String, CharSet> map = new HashMap<String, CharSet>();
		map.putAll(this);
		super.clear();
		
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()) {
			String id = iterator.next();
			fireAfterCharSetChange(new CharSetChangeEvent(this, !iterator.hasNext(), id, map.get(id), false));
		}
	}


	@Override
	public boolean addModelListener(CharSetDataModelListener listener) {
		return modelListeners.add(listener);
	}


	@Override
	public boolean removeModelListener(CharSetDataModelListener listener) {
		return modelListeners.remove(listener);
	}


	public CharSetDataModelListener getUndoListener() {
		return undoListener;
	}


	/**
	 * Informs all listeners that a character set has been inserted, removed or replaced.
	 */
	protected void fireAfterCharSetChange(CharSetChangeEvent e) {
		for (CharSetDataModelListener listener : modelListeners.toArray(new CharSetDataModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.afterCharSetChange(e);
		}
	}


	/**
	 * Informs all listeners that a character set has been renamed.
	 */
	protected void fireAfterCharSetRenamed(CharSetRenamedEvent e) {
		for (CharSetDataModelListener listener : modelListeners.toArray(new CharSetDataModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.afterCharSetRenamed(e);
		}
	}


	/**
	 * Informs all listeners that one or more neighboring columns have been added to or removed from a character set.
	 */
	protected void fireAfterCharSetColumnChange(CharSetColumnChangeEvent e) {
		for (CharSetDataModelListener listener : modelListeners.toArray(new CharSetDataModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.afterCharSetColumnChange(e);
		}
	}


	/**
	 * Informs all listeners that the color of a character set has been changed.
	 */
	protected void fireAfterCharSetColorChanged(CharSetColorChangeEvent e) {
		for (CharSetDataModelListener listener : modelListeners.toArray(new CharSetDataModelListener[modelListeners.size()])) {  // Copying the list is necessary to allow listeners to remove themselves from the list without a ConcurrentModificationException being thrown.
			listener.afterCharSetColorChange(e);
		}
	}


	@Override
	public String nextKey(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String previousKey(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public CharSetDataModelListener createUndoListener(EditRecorder<?, ?> recorder) {
		undoListener = new CharSetDataModelUndoListener(recorder);
		return undoListener;
	}


	@Override
	public void removeUndoListener() {
		if (undoListener != null) {
			removeModelListener(undoListener);
		}
	}


	@Override
	public boolean hasUndoListener() {
		if (undoListener != null) {
			return true;
		}
		else {
			return false;
		}
		
	}


	
}
