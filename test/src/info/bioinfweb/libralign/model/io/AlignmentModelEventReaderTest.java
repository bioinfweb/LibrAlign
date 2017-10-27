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
package info.bioinfweb.libralign.model.io;


import static org.junit.Assert.*;
import static info.bioinfweb.libralign.test.LibrAlignTestTools.*;

import java.io.File;
import java.io.IOException;

import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formats.nexus.NexusEventReader;
import info.bioinfweb.libralign.model.AlignmentModel;
import info.bioinfweb.libralign.model.factory.BioPolymerCharAlignmentModelFactory;

import org.junit.*;


public class AlignmentModelEventReaderTest {
	@Test
	public void testAdditionalDNATokens() throws IOException {
		NexusEventReader eventReader = new NexusEventReader(new File("data/alignments/AdditionalDNATokens.nex"), new ReadWriteParameterMap());
		try {
			AlignmentModelEventReader modelReader = new AlignmentModelEventReader(new BioPolymerCharAlignmentModelFactory()); 
			
			while (eventReader.hasNextEvent()) {
				modelReader.processEvent(eventReader, eventReader.next());
			}
			
			assertEquals(1, modelReader.getCompletedModels().size());
			AlignmentModel<?> alignmentModel = modelReader.getCompletedModels().get(0); 
			assertTrue(alignmentModel.getTokenSet().contains('#'));
			assertEquals('#', alignmentModel.getTokenAt(alignmentModel.sequenceIDsByName("A").iterator().next(), 1));
		}
		finally {
			eventReader.close();
		}
	}
}
