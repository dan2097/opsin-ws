package uk.ac.cam.ch.opsin.ws;
import java.io.ByteArrayInputStream;

import nu.xom.Element;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.interfaces.IChemFile;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.io.CMLReader;


public class CMLToCDK {
	
	/**Converts a CML molecule to a CDK molecule.
	 * 
	 * @param cmlMol The CML molecule.
	 * @return The CDK molecule.
	 * @throws Exception
	 */
	public static IMolecule cmlToMolecule(Element cmlMol) throws Exception {
		ByteArrayInputStream bais = new ByteArrayInputStream(cmlMol.toXML().getBytes());
		IChemFile cf = (IChemFile) new CMLReader(bais).read(new ChemFile());
		IMolecule mol = cf.getChemSequence(0).getChemModel(0).getMoleculeSet().getMolecule(0);
		return mol;
	}

}
