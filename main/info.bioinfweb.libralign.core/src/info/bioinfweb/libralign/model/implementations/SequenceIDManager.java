/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014-2017  Ben Stöver
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
package info.bioinfweb.libralign.model.implementations;


import info.bioinfweb.commons.IntegerIDManager;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;



/**
 * Manages mappings between sequence IDs and sequence names.
 * <p>
 * IDs are unique, while multiple sequences may have the same name.
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class SequenceIDManager {
	public static final String DEFAULT_ID_PREFIX = "id";
	
	
	private String idPrefix;
	private IntegerIDManager integerIDManager = new IntegerIDManager();
	private Map<String, Set<String>> idsByNameMap = new TreeMap<String, Set<String>>();  //TODO A simple map is not possible anymore, if null names and multiple identical names are allowed. Only a mapping to a set of IDs would be possible.
	private Map<String, String> nameByIDMap = new TreeMap<String, String>();

	
	public SequenceIDManager() {
		this(DEFAULT_ID_PREFIX);
	}
	
	
	public SequenceIDManager(String idPrefix) {
		super();
		if (idPrefix == null) {
			throw new NullPointerException("The ID prefix must not be null.");
		}
		else {
			this.idPrefix = idPrefix;
		}
	}

	
	private Set<String> getIDSetByName(String sequenceName) {
		Set<String> set = idsByNameMap.get(sequenceName);
		if (set == null) {
			set = new TreeSet<String>();
			idsByNameMap.put(sequenceName, set);
		}
		return set;
	}
	

	/**
   * Adds a new empty sequence to the underlying data source and generates an ID for it.
   * <p>
   * If a shared ID manager is used, a previously defined ID is used, if one exists.
   * 
   * @param sequenceName the name of the new sequence
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
   */
  public String addSequenceName(String sequenceName) {
  	if (sequenceName == null) {
  		sequenceName = "";  // null and "" shall be equivalent.
  	}
  	
		String sequenceID = idPrefix + integerIDManager.createNewID();
		getIDSetByName(sequenceName).add(sequenceID);
		nameByIDMap.put(sequenceID, sequenceName);
		return sequenceID;
  }
  
  
  /**
   * Renames a sequence in the underlying data source.
   * 
   * @param sequenceID the ID of the sequence to be renamed
   * @param newSequenceName the new name the sequence shall have
   * @param model the alignment model calling this method (This reference is only needed to set the source
   *        of a possible exceptions.)
   * @return the name the sequence had until now
	 * 
	 * @throws SequenceNotFoundException if a sequence with the specified ID is not present the underlying
	 *         data source
   */
  public String renameSequence(String sequenceID, String newSequenceName, AlignmentModel<?> model) 
  		throws SequenceNotFoundException {

  	if (newSequenceName == null) {
  		newSequenceName = "";
  	}
  	
  	String oldSequenceName = nameByIDMap.get(sequenceID);
	  if (oldSequenceName == null) {
	  	throw new SequenceNotFoundException(model, sequenceID);
	  }
	  else if (!newSequenceName.equals(oldSequenceName)) {
	  	getIDSetByName(oldSequenceName).remove(sequenceID);
	  	getIDSetByName(newSequenceName).add(sequenceID);
	  	nameByIDMap.put(sequenceID, newSequenceName);
  	}
  	return oldSequenceName;
  }

  
  /**
   * Returns a set of unique sequence IDs associated with the specified name. (Multiple sequences with the same
   * name are allowed to be present.)
   * 
   * @param sequenceName the name of the sequence that would be visible to the application user
   * @return the set of sequence IDs (The set maybe empty if no sequence with the specified name is contained in 
   *         this model. Returns sets are not modifiable.)
   */
  @SuppressWarnings("unchecked")
	public Set<String> sequenceIDsByName(String sequenceName) {
  	if (sequenceName == null) {
  		sequenceName = "";
  	}
  	Set<String> result = idsByNameMap.get(sequenceName);
  	if (result == null) {
  		return Collections.EMPTY_SET;
  	}
  	else {
  		return Collections.unmodifiableSet(result);
  	}
  }

  
  /**
   * Returns the sequence name (that would be visible to the application user) associated with the 
   * specified unique ID.
   * 
   * @param sequenceID the unique unmodifiable ID the sequence is identified by
   * @return the sequence name or {@code null} if no sequence with this ID is contained in this model
   */
  public String sequenceNameByID(String sequenceID) {
  	return nameByIDMap.get(sequenceID);
  }
}
