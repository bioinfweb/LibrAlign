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


import static org.junit.Assert.*;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.formats.nexus.NexusEventReader;
import info.bioinfweb.libralign.model.factory.StringAlignmentModelFactory;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;
import info.bioinfweb.libralign.model.io.DataModelKey;

import java.io.File;
import java.io.IOException;

import org.junit.Test;



public class CharSetEventReaderTest {
	public static void assertCharSet(int expectedStart, int expectedEnd, boolean expectedValue, CharSet set) {
		for (int i = expectedStart; i < expectedEnd; i++) {
			assertEquals(expectedValue, set.contains(i));
		}
	}
	
	
	@Test
	public void testReadingCharSets() {
		try {
			NexusEventReader eventReader = new NexusEventReader(new File("data/charSet/CharSet.nex"), new ReadWriteParameterMap());

			AlignmentDataReader mainReader = new AlignmentDataReader(eventReader, new StringAlignmentModelFactory());
			CharSetEventReader charSetReader = new CharSetEventReader(mainReader);
			mainReader.addDataModelReader(charSetReader);
			try {
				mainReader.readAll();
			}
			catch (Exception e) {
				e.printStackTrace();
				fail(e.getLocalizedMessage());
			}
			
			assertEquals(1, charSetReader.getCompletedModels().size());
//			DataModelReadInfo<CharSetDataModel> info = charSetReader.getCompletedModels().get(0);
//			assertNull(info.getAlignmentModel());
//			assertNull(info.getSequenceID());
			
			CharSetDataModel model = charSetReader.getFirstCompletedModel(new DataModelKey(null, null));
			assertNotNull(model);
			
			//CharSetDataModel model = charSetReader.getCompletedModels().values().iterator().next();  //info.getDataModel();			
			
			CharSet set = model.getByName("set01");
			assertNotNull(set);
			assertCharSet(0, 2, false, set);  // Nexus indices start with 1. The indices here start with 0.
			assertCharSet(2, 5, true, set);
			assertCharSet(5, 8, false, set);
			assertCharSet(8, 9, true, set);
			assertCharSet(9, 11, false, set);
			assertCharSet(11, 16, true, set);
			assertCharSet(16, 200, false, set);
			
			set = model.getByName("set02");
			assertNotNull(set);
			assertCharSet(0, 3, false, set);
			assertCharSet(3, 9, true, set);
			assertCharSet(9, 200, false, set);
			
			set = model.getByName("set03");
			assertNotNull(set);
			assertCharSet(0, 3, false, set);
			assertCharSet(3, 6, true, set);
			assertCharSet(6, 11, false, set);
			assertCharSet(11, 12, true, set);
			assertCharSet(12, 200, false, set);
			
			set = model.getByName("set04");
			assertNotNull(set);
			assertCharSet(0, 3, false, set);
			assertCharSet(3, 6, true, set);
			assertCharSet(6, 11, false, set);
			assertCharSet(11, 12, true, set);
			assertCharSet(12, 200, false, set);
			
			set = model.getByName("set05");
			assertNotNull(set);
			assertCharSet(0, 3, false, set);
			assertCharSet(3, 4, true, set);
			assertCharSet(4, 5, false, set);
			assertCharSet(5, 8, true, set);
			assertCharSet(8, 9, false, set);
			assertCharSet(9, 10, true, set);
			assertCharSet(10, 11, false, set);
			assertCharSet(11, 13, true, set);
			assertCharSet(13, 200, false, set);
			
			set = model.getByName("set06");
			assertNotNull(set);
			assertCharSet(0, 2, false, set);
			assertCharSet(2, 3, true, set);
			assertCharSet(3, 200, false, set);
			
			assertEquals(6, model.size());  // Check that no additional sets are present.
		}
		catch (IOException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
}
