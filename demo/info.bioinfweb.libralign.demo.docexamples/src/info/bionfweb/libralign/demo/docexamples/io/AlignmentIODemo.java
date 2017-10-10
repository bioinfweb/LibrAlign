package info.bionfweb.libralign.demo.docexamples.io;


import java.io.File;

import info.bioinfweb.jphyloio.JPhyloIOEventReader;
import info.bioinfweb.jphyloio.ReadWriteParameterMap;
import info.bioinfweb.jphyloio.factory.JPhyloIOReaderWriterFactory;
import info.bioinfweb.libralign.model.factory.BioPolymerCharAlignmentModelFactory;
import info.bioinfweb.libralign.model.io.AlignmentDataReader;



/**
 * Example that demonstrates reading and writing alignments with <i>LibrAlign</i>.
 * <p>
 * This example is used in the 
 * <a href="http://bioinfweb.info/LibrAlign/Documentation/wiki/Reading_and_writing_data">LibrAlign documentation</a>.
 * 
 * @author Ben St&ouml;ver
 */
public class AlignmentIODemo {
	public static void main(String[] args) {
		try {
			JPhyloIOReaderWriterFactory factory = new JPhyloIOReaderWriterFactory();
			JPhyloIOEventReader eventReader = factory.guessReader(new File("data/Test.nexml"), new ReadWriteParameterMap());
			
			AlignmentDataReader mainReader = new AlignmentDataReader(eventReader, new BioPolymerCharAlignmentModelFactory());
			mainReader.readAll();
			System.out.println(mainReader.getAlignmentModelReader().getCompletedModels().size());
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
