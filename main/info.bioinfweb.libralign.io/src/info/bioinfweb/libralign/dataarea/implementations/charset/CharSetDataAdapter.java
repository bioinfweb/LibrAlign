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
package info.bioinfweb.libralign.dataarea.implementations.charset;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import info.bioinfweb.commons.collections.SimpleSequenceInterval;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.dataadapters.ObjectListDataAdapter;
import info.bioinfweb.jphyloio.events.CharacterSetIntervalEvent;
import info.bioinfweb.jphyloio.events.LinkedLabeledIDEvent;
import info.bioinfweb.jphyloio.events.PartEndEvent;
import info.bioinfweb.jphyloio.events.meta.URIOrStringIdentifier;
import info.bioinfweb.jphyloio.events.type.EventContentType;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;



/**
 * <i>JPhyloIO</i> data adapter implementation to write the contents of a {@link CharSetDataModel}.
 * <p>
 * The color of a character set can be written as metadata.
 * 
 * @author Ben Stöver
 * @since 0.6.0
 * @bioinfweb.module info.bioinfweb.libralign.io
 */
public class CharSetDataAdapter implements ObjectListDataAdapter<LinkedLabeledIDEvent> {
	public static final String COLOR_METADATA_ID_SUFFIX = "Col";
	public static final String COLOR_METADATA_LABEL = "Color";
	
	
	private String idPrefix;
	private CharSetDataModel model;
	private String linkedMatrixID;
	private URIOrStringIdentifier colorPredicate;
	private URIOrStringIdentifier colorDataType;
	
	
	/**
	 * Creates a new instance of this class.
	 * 
	 * @param idPrefix the <i>JPhyloIO</i> ID prefix to be used for the character set events to be written
	 *        (Maybe "".)
	 * @param model the model providing the data to be written
	 * @param linkedMatrixID the ID used by the start event of the associated matrix data
	 * @param colorPredicate the predicate to be used to write the color metadata event (Maybe {@code null}.)
	 * @param colorDataType the data type to be used to write the color metadata event (Maybe {@code null} 
	 *        only if {@code colorPredicate} is null as well.)
	 * @throws IllegalArgumentException if {@code idPrefix}, {@code model} or {@code linkedMatrixID} are
	 *         {@code null} or if {@code colorDataType} is {@code null} while {@code colorPredicate} is not
	 */
	public CharSetDataAdapter(String idPrefix, CharSetDataModel model, String linkedMatrixID,
			URIOrStringIdentifier colorPredicate, URIOrStringIdentifier colorDataType) {
		
		super();
		if (idPrefix == null) {
			throw new IllegalArgumentException("idprefix must not be null.");
		}
		else if (model == null) {
			throw new IllegalArgumentException("model must not be null.");
		}
		else if (linkedMatrixID == null) {
			throw new IllegalArgumentException("linkedMatrixID must not be null.");
		}
		else if ((colorPredicate != null) && (colorDataType == null)) {
			throw new IllegalArgumentException("colorDataType must not be null if colorPredicate is specified.");
		}
		else {
			this.idPrefix = idPrefix;
			this.model = model;
			this.linkedMatrixID = linkedMatrixID;
			this.colorPredicate = colorPredicate;
			this.colorDataType = colorDataType;
		}
	}


	/**
	 * Creates a new instance of this class that will not write character set colors.
	 * 
	 * @param idPrefix the <i>JPhyloIO</i> ID prefix to be used for the character set events to be written
	 *        (Maybe "".)
	 * @param model the model providing the data to be written
	 * @param linkedMatrixID the ID used by the start event of the associated matrix data
	 * @throws IllegalArgumentException if {@code idPrefix}, {@code model} or {@code linkedMatrixID} are
	 *         {@code null}
	 */
	public CharSetDataAdapter(String idPrefix, CharSetDataModel model, String linkedMatrixID) {
		this(idPrefix, model, linkedMatrixID, null, null);
	}
	
	
	private CharSet getCharSet(String id) {
		return model.get(id.substring(idPrefix.length()));
	}


	@Override
	public LinkedLabeledIDEvent getObjectStartEvent(ReadWriteParameterMap parameters, String id) throws IllegalArgumentException {
		CharSet charSet = getCharSet(id);
		if (charSet == null) {
			throw new IllegalArgumentException("No character set with the ID \"" + id + "\" available.");
		}
		else {
			return new LinkedLabeledIDEvent(EventContentType.CHARACTER_SET, id, charSet.getName(), linkedMatrixID);
		}
	}

	
	@Override
	public long getCount(ReadWriteParameterMap parameters) {
		return model.size();
	}

	
	@Override
	public Iterator<String> getIDIterator(ReadWriteParameterMap parameters) {
		List<String> ids = new ArrayList<String>(model.size());
		for (String id : model.keyList()) {
			ids.add(idPrefix + id);
		}
		return ids.iterator();
	}

	
	@Override
	public void writeContentData(ReadWriteParameterMap parameters, JPhyloIOEventReceiver receiver, String id)
			throws IOException, IllegalArgumentException {
		
		CharSet charSet = getCharSet(id);
		if (charSet == null) {
			throw new IllegalArgumentException("No character set with the ID \"" + id + "\" available.");
		}
		else {
			if (colorPredicate != null) {
				JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, id + COLOR_METADATA_ID_SUFFIX, COLOR_METADATA_LABEL, 
						colorPredicate, colorDataType, charSet.getColor(), charSet.getColor().toString());
			}
			
			for (SimpleSequenceInterval interval : charSet) {
				receiver.add(new CharacterSetIntervalEvent(interval.getFirstPos(), interval.getLastPos()));  //TODO lastPos + 1?
			}
			receiver.add(new PartEndEvent(EventContentType.CHARACTER_SET, true));
		}
	}
}
