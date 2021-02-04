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
package info.bioinfweb.libralign.dataarea.implementations.pherogram;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import info.bioinfweb.commons.io.XMLUtils;
import info.bioinfweb.jphyloio.ReaderStreamDataProvider;
import info.bioinfweb.jphyloio.WriterStreamDataProvider;
import info.bioinfweb.jphyloio.objecttranslation.InvalidObjectSourceDataException;
import info.bioinfweb.jphyloio.objecttranslation.implementations.AbstractXMLObjectTranslator;
import info.bioinfweb.libralign.pherogram.model.ShiftChange;



@SuppressWarnings("rawtypes")
public class ShiftListObjectTranslator extends AbstractXMLObjectTranslator<List> implements PherogramIOConstants {
	@Override
	public Class<List> getObjectClass() {
		return List.class;
	}

	
	private int readAttribute(StartElement element, QName name) throws IOException {
  	Attribute attibute = element.getAttributeByName(name);
  	if (attibute == null) {
  		throw new IOException("Shift declaration with missing attribute " + name.getLocalPart() + " found.");
  	}
  	else {
  		try {
  			return Integer.parseInt(attibute.getValue());
  		}
  		catch (NumberFormatException e) {
  			throw new IOException("Value \"" + attibute.getValue() + "\" for the attribute " + name.getLocalPart() + " is not a valid integer.", e);
  		}
  	}
	}
	
	
	private void readShiftList(XMLEventReader reader, List<ShiftChange> list) throws XMLStreamException, IOException {
		XMLEvent event = reader.nextEvent();
		while (event.getEventType() != XMLStreamConstants.END_ELEMENT) {
			if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
				StartElement element = event.asStartElement();
				if (element.getName().getLocalPart().equals(TAG_SHIFT.getLocalPart())) {
					list.add(new ShiftChange(readAttribute(element, ATTR_POSITION), readAttribute(element, ATTR_SHIFT)));
				}
				XMLUtils.reachElementEnd(reader);  
			}
			event = reader.nextEvent();
		}
	}
	
	
	@Override
	public List readXMLRepresentation(XMLEventReader reader, ReaderStreamDataProvider<?> streamDataProvider)
			throws IOException, XMLStreamException, InvalidObjectSourceDataException {

		List<ShiftChange> result = new ArrayList<>();
		XMLEvent event = reader.nextEvent();
		if (event.getEventType() == XMLStreamConstants.START_ELEMENT) {
			StartElement element = event.asStartElement();
			if (element.getName().getLocalPart().equals(TAG_SHIFTS.getLocalPart())) {
				readShiftList(reader, result);
			}
			else {
				XMLUtils.reachElementEnd(reader);  
			}
		}
		XMLUtils.reachElementEnd(reader);  // Skip final end element but do not read further.  
		return result;
	}

	
	@Override
	public void writeXMLRepresentation(XMLStreamWriter writer, Object object, WriterStreamDataProvider<?> streamDataProvider)
			throws IOException, XMLStreamException, ClassCastException {

		List<ShiftChange> shifts = (List<ShiftChange>) object;
//		ShiftChange[] shifts = test.toArray(new ShiftChange[test.size()]);
		//ShiftChange[] shifts = (ShiftChange[]) object;
		XMLUtils.writeStartElement(writer, TAG_SHIFTS);
		for (ShiftChange shift : shifts) {
			XMLUtils.writeStartElement(writer, TAG_SHIFT);
			XMLUtils.writeAttribute(writer, ATTR_POSITION, Integer.toString(shift.getBaseCallIndex()));
			XMLUtils.writeAttribute(writer, ATTR_SHIFT, Integer.toString(shift.getShiftChange()));
			writer.writeEndElement();
		}
		writer.writeEndElement();
	}
}