package uk.ac.cam.ch.opsin.ws;

import java.awt.image.RenderedImage;

import java.util.List;

import nu.xom.Element;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;

import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

public class OpsinResultToDepiction {
	
	/**
	 * Converts an OPSIN result to a rendered image. An exception is thrown if the conversion fails
	 * @param result
	 * @param verbose Whether to print lots of debugging information to stdin and stderr or not.
	 * @return RenderedImage depiction of molecule
	 * @throws DepictionGenerationException Thrown if conversion failed
	 */
	public static RenderedImage convertResultToDepction(OpsinResult result, boolean verbose){
		verbose=true;
		if (result.getCml() != null){
			RenderedImage depiction = null;
			try{
				depiction = CMLToDepiction(result.getCml(), verbose);
			}
			catch (Exception e) {
				if (verbose){
					e.printStackTrace();
				}
				return null;
			}
			if (depiction ==null){
				return null;
			}
			return depiction;
		}
		return null;
	}

	private static RenderedImage CMLToDepiction(Element cml, boolean verbose) throws Exception {

		IMolecule molecule = CMLToCDK.cmlToMolecule(cml);
		for (int i = molecule.getAtomCount() -1 ; i>=0; i--) {
			if (molecule.getAtom(i).getSymbol().equals("H")){
				IAtom hydrogen =molecule.getAtom(i);
				List<IBond> bonds = molecule.getConnectedBondsList(hydrogen);
				if (bonds.size()>1){//could be something like diborane
					throw new Exception("Unexpected hydrogen connectivity");
				}
				IAtom connectedAtom = bonds.get(0).getConnectedAtom(hydrogen);
				connectedAtom.setHydrogenCount(connectedAtom.getHydrogenCount()==null ? 1 : connectedAtom.getHydrogenCount()  +1);
				for (IBond iBond : bonds) {
					molecule.removeBond(iBond);
				}
				molecule.removeAtom(hydrogen);
			}
		}
		molecule = MultiFragmentStructureDiagramGeneratorCutDown.getMoleculeWith2DCoords(molecule);
		Molecule2PngCutDown m2PNG = new Molecule2PngCutDown();
		return (RenderedImage) m2PNG.renderMolecule(molecule);
	}
}
