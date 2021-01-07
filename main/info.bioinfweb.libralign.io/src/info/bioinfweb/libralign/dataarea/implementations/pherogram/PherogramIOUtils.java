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
import java.net.URI;

import info.bioinfweb.commons.io.W3CXSConstants;
import info.bioinfweb.jphyloio.ReadWriteConstants;
import info.bioinfweb.jphyloio.dataadapters.JPhyloIOEventReceiver;
import info.bioinfweb.jphyloio.utils.JPhyloIOWritingUtils;
import info.bioinfweb.libralign.pherogram.model.PherogramAreaModel;
import info.bioinfweb.libralign.pherogram.provider.ReverseComplementPherogramProvider;



public class PherogramIOUtils {
	public static void writePherogramMetadata(JPhyloIOEventReceiver receiver, String jPhyloIOPrefixSequenceID, PherogramAreaModel reference, URI uri) throws IOException, IllegalArgumentException {
		if (reference != null) {
			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver,
					jPhyloIOPrefixSequenceID + ReadWriteConstants.DEFAULT_META_ID_PREFIX + "2", null,
					PherogramIOConstants.PREDICATE_IS_REVERSE_COMPLEMENTED, W3CXSConstants.DATA_TYPE_BOOLEAN, 
					reference.getPherogramProvider() instanceof ReverseComplementPherogramProvider); // TODO use boolean method of PhyDE2 or LibrAlign in the future
			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, 
					jPhyloIOPrefixSequenceID + ReadWriteConstants.DEFAULT_META_ID_PREFIX + "3", null, 
					PherogramIOConstants.PREDICATE_HAS_LEFT_CUT_POSITION, W3CXSConstants.DATA_TYPE_INT, 
					reference.getLeftCutPosition());
			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, 
					jPhyloIOPrefixSequenceID + ReadWriteConstants.DEFAULT_META_ID_PREFIX + "4", null, 
					PherogramIOConstants.PREDICATE_HAS_RIGHT_CUT_POSITION, W3CXSConstants.DATA_TYPE_INT, 
					reference.getRightCutPosition());
			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, 
					jPhyloIOPrefixSequenceID + ReadWriteConstants.DEFAULT_META_ID_PREFIX + "5", null, 
					PherogramIOConstants.PREDICATE_HAS_FIRST_SEQ_POSITION, W3CXSConstants.DATA_TYPE_INT, 
					reference.getFirstSeqPos());
			JPhyloIOWritingUtils.writeSimpleLiteralMetadata(receiver, 
					jPhyloIOPrefixSequenceID + ReadWriteConstants.DEFAULT_META_ID_PREFIX + "6", null, 
					PherogramIOConstants.PREDICATE_HAS_PHEROGRAM_ALIGNMENT, PherogramIOConstants.DATA_TYPE_SHIFT_LIST, 
					reference.getShiftChangeList()); //TODO: How to get to shifts without iterator? Update now an unmodifiable copy is given.
			JPhyloIOWritingUtils.writeTerminalResourceMetadata(receiver, 
					jPhyloIOPrefixSequenceID + ReadWriteConstants.DEFAULT_META_ID_PREFIX + "7", null, 
					PherogramIOConstants.PREDICATE_HAS_PHEROGRAM, uri);
		}
	}
}
