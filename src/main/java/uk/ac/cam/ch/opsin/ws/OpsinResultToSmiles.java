package uk.ac.cam.ch.opsin.ws;


import nu.xom.Element;

import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.smiles.SmilesGenerator;

import uk.ac.cam.ch.wwmm.opsin.OpsinResult;

public class OpsinResultToSmiles {
	
	private static SmilesGenerator sg = new SmilesGenerator();
	
	/**
	 * Converts an OPSIN result to SMILES. An exception is thrown if the conversion fails
	 * @param result
	 * @param verbose Whether to print lots of debugging information to stdin and stderr or not.
	 * @return String SMILES
	 * @throws SmilesGenerationException Thrown if conversion failed
	 */
	public static String convertResultToSMILES(OpsinResult result, boolean verbose){
		if (result.getCml() != null){
			String smiles = null;
			try{
				smiles = cmlToSmiles(result.getCml(), verbose);
			}
			catch (Exception e) {
				if (verbose){
					e.printStackTrace();
				}
				return null;
			}
			if (smiles ==null){
				return null;
			}
			if(verbose) System.out.println(smiles);
			return smiles;
		}
		return null;
	}

	private static String cmlToSmiles(Element cmlMol, boolean verbose) throws Exception{
		IMolecule molecule = CMLToCDK.cmlToMolecule(cmlMol); 
		
		/*
		 * Commented out is some code to prettify the SMILES
		 * Making the hydrogens not explicit removes the distinction between phosphane and phosphorane
		 * Not using the kekule representations means the output doesn't match what OPSIN actually produces
		 */
		
//		for (int i = molecule.getAtomCount() -1 ; i>=0; i--) {
//			if (molecule.getAtom(i).getSymbol().equals("H")){
//				IAtom hydrogen =molecule.getAtom(i);
//				List<IBond> bonds = molecule.getConnectedBondsList(hydrogen);
//				if (bonds.size()>1){//could be something like diborane
//					throw new Exception("Unexpected hydrogen connectivity");
//				}
//				IAtom connectedAtom = bonds.get(0).getConnectedAtom(hydrogen);
//				connectedAtom.setHydrogenCount(connectedAtom.getHydrogenCount()==null ? 0 : connectedAtom.getHydrogenCount()  +1);
//				for (IBond iBond : bonds) {
//					molecule.removeBond(iBond);
//				}
//				molecule.removeAtom(hydrogen);
//			}
//		}

		//sg.setUseAromaticityFlag(true);

		return sg.createSMILES(molecule);
	}
}
