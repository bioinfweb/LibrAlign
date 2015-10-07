/*
 * LibrAlign - A GUI library for displaying and editing multiple sequence alignments and attached data
 * Copyright (C) 2014 - 2015  Ben Stöver
 * <http://bioinfweb.info/LibrAlign>
 * 
 * This file is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This file is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package info.bioinfweb.libralign.model.implementations;


import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.exception.AlignmentSourceNotWritableException;
import info.bioinfweb.libralign.model.exception.DuplicateSequenceNameException;
import info.bioinfweb.libralign.model.exception.SequenceNotFoundException;

import java.util.Map;
import java.util.TreeMap;



/**
 * Manages mappings between sequence IDs and sequence names.
 * <p>
 * Mapping once added to an instance cannot be removed later on to ensure that all IDs stored in any 
 * object remain valid. 
 * 
 * @author Ben St&ouml;ver
 * @since 0.4.0
 */
public class SequenceIDManager {  //TODO What if the same name is added several times (especially if it has been removed from a model and then added again)?
	private Map<String, Integer> idByNameMap = new TreeMap<String, Integer>();
	private Map<Integer, String> nameByIDMap = new TreeMap<Integer, String>();
	private int nextID = 0;
	
	
	/**
	 * Returns a new sequence identifier that has not been returned before.
	 * 
	 * @return a value greater or equal to zero
	 */
	private int createNewID() {
		nextID++;
		return nextID - 1;
	}
	
	
  /**
   * Adds a new empty sequence to the underlying data source and generates an ID for it.
   * 
   * @param sequenceName - the name of the new sequence
   * @return the unique ID of the new sequence
	 * 
	 * @throws AlignmentSourceNotWritableException if the underlying data source is not writable for sequences
   */
  public int addSequenceName(String sequenceName) {
		int sequenceID = createNewID();
		idByNameMap.put(sequenceName, sequenceID);
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
	 * @throws DuplicateSequenceNameException if a sequence with the specified new name is already present in 
	 *         the underlying data source 
	 * @throws SequenceNotFoundException if a sequence with the specified ID is not present the underlying
	 *         data source
   */
  public String renameSequence(int sequenceID, String newSequenceName, AlignmentModel<?> model) 
  		throws DuplicateSequenceNameException, SequenceNotFoundException {

  	String oldSequenceName = nameByIDMap.get(sequenceID);
	  if (oldSequenceName == null) {
	  	throw new SequenceNotFoundException(model, sequenceID);
	  }
	  else if (!newSequenceName.equals(oldSequenceName)) {
  		if (idByNameMap.get(newSequenceName) == sequenceID) {
		  	idByNameMap.remove(oldSequenceName);
		  	idByNameMap.put(newSequenceName, sequenceID);
		  	nameByIDMap.put(sequenceID, newSequenceName);
  		}
  		else {
  			throw new DuplicateSequenceNameException(model, newSequenceName);
  		}
  	}
  	return oldSequenceName;
  }

  
  /**
   * Returns the unique sequence ID associated with the specified name.
   * 
   * @param sequenceName - the name of the sequence that would be visible to the application user
   * @return the sequence ID or {@code -1} if no sequence with the specified name is contained in this model
   */
  public int sequenceIDByName(String sequenceName) {
  	Integer result =  idByNameMap.get(sequenceName);
  	if (result == null) {
  		return -1;
  	}
  	else {
  		return result;
  	}
  }

  
  /**
   * Returns the sequence name (that would be visible to the application user) associated with the 
   * specified unique ID.
   * 
   * @param sequenceID - the unique unmodifiable ID the sequence is identified by
   * @return the sequence name or {@code null} if no sequence with this ID is contained in this model
   */
  public String sequenceNameByID(int sequenceID) {
  	return nameByIDMap.get(sequenceID);
  }
}
