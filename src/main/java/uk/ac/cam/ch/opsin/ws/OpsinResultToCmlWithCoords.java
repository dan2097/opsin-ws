package uk.ac.cam.ch.opsin.ws;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import com.ctc.wstx.stax.WstxEventFactory;
import com.ctc.wstx.stax.WstxInputFactory;
import com.ctc.wstx.stax.WstxOutputFactory;
import com.epam.indigo.Indigo;
import com.epam.indigo.IndigoObject;

import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

public class OpsinResultToCmlWithCoords {
	
	private static final XMLInputFactory inputFactory = new WstxInputFactory();
	private static final XMLOutputFactory outputFactory = new WstxOutputFactory();
	private static final XMLEventFactory eventFactory = new WstxEventFactory();

	/**
	 * Converts an OPSIN result to a CML element with coordinates provided by Indigo. An exception is thrown if Indigo fails to initialise
	 * An assumption is made that Indigo will create a molecule with atoms in the same order as in the CML.
	 * @param result
	 * @return
	 * @throws IOException
	 * @throws XMLStreamException 
	 */
	public static String convertResultToCmlWithCoords(OpsinResult result) throws IOException, XMLStreamException {
		String cml = result.getPrettyPrintedCml();
		if (cml != null){
			Indigo indigo = new Indigo();
			IndigoObject mol = indigo.loadMolecule(cml);
			mol.layout();
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLEventReader xmlReader = inputFactory.createXMLEventReader(new StringReader(cml));
			XMLEventWriter xmlWriter = outputFactory.createXMLEventWriter(out, "UTF-8");

			int atomCount = mol.countAtoms();
			int atomIndex = 0;
			while (xmlReader.hasNext()) {
				XMLEvent event = xmlReader.nextEvent();
				xmlWriter.add(event);
				if (event.isStartElement() && 
						event.asStartElement().getName().getLocalPart().equals("atom")){
					if (atomIndex >= atomCount){
						throw new RuntimeException("Indigo molecule has a different number of atoms to CML");
					}
					IndigoObject indigoAtom = mol.getAtom(atomIndex++);
					xmlWriter.add(eventFactory.createAttribute("x2", String.valueOf(indigoAtom.xyz()[0])));
					xmlWriter.add(eventFactory.createAttribute("y2", String.valueOf(indigoAtom.xyz()[1])));
				}
			}
			xmlReader.close();
			xmlWriter.close();
			if (atomIndex != atomCount){
				throw new RuntimeException("Indigo molecule has a different number of atoms to CML");
			}
			try {
				return out.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException("JVM doesn't support UTF-8...but it should do!");
			}
		}
		return cml;
	}
}
