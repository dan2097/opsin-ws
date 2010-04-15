package uk.ac.cam.ch.opsin.ws;

import javax.vecmath.Point2d;

import org.openscience.cdk.MoleculeSet;
import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.graph.ConnectivityChecker;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.interfaces.IMoleculeSet;
import org.openscience.cdk.layout.StructureDiagramGenerator;

/** NOTE THAT THIS IS A CUTDOWN VERSION OF THIS CLASS (DL387)
 * A wrapper for StructureDiagramGenerator that can handle 
 * multi-fragment molecules.
 * 
 * @author ptc24
 *
 */
public final class MultiFragmentStructureDiagramGeneratorCutDown {
	/**Produces a molecule with 2D coordinates from a molecule.
	 * 
	 * @param inputMol The input molecule.
	 * @return 
	 * @return The molecule, with 2D coordinates.
	 * @throws Exception
	 */
	public static IMolecule getMoleculeWith2DCoords(IMolecule inputMol) throws Exception {
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		IMoleculeSet originalMols = ConnectivityChecker.partitionIntoMolecules(inputMol);
		MoleculeSet mols = new MoleculeSet();
		for(int i=0;i<originalMols.getMoleculeCount();i++) {
			IMolecule mol = originalMols.getMolecule(i);
			sdg.setMolecule(mol);
			sdg.generateCoordinates();
			mols.addMolecule(sdg.getMolecule());
		}
		if(mols.getMoleculeCount() == 0) return null;
		sdg = null;
		return getMoleculeForMoleculeRange(0, mols.getMoleculeCount()-1, mols);
	}
	
	private static IMolecule getMoleculeForMoleculeRange(int start, int end, MoleculeSet mols) throws Exception {
		if(start == end) {
			return mols.getMolecule(start);
		} else if(start + 1 == end) {
			return combineMolecules(mols.getMolecule(start), mols.getMolecule(end));
		} else {
			int midPoint = (start + end) / 2;
			return combineMolecules(getMoleculeForMoleculeRange(start, midPoint, mols),
					getMoleculeForMoleculeRange(midPoint+1, end, mols));
		}
	}
	
	private static IMolecule combineMolecules(IMolecule molA, IMolecule molB) throws Exception {
		GeometryToolsInternalCoordinates.translate2DCenterTo(molA, new Point2d(0,0));
		GeometryToolsInternalCoordinates.translate2DCenterTo(molB, new Point2d(0,0));
			
		/* XY - minX, minY, maxX, maxY */
		double [] interXY = GeometryToolsInternalCoordinates.getMinMax(molA);
		double [] outputXY = GeometryToolsInternalCoordinates.getMinMax(molB);
						
		double sideBySideWidth = (interXY[2] + outputXY[2] - interXY[0] - outputXY[0]) + 2;
		double onTopHeight = (interXY[3] + outputXY[3] - interXY[1] - outputXY[1]) + 2;
			
		if(sideBySideWidth < onTopHeight) {			
			GeometryToolsInternalCoordinates.translate2D(molA, outputXY[2] - interXY[0] + 1.0, 0.0);
		} else {
			GeometryToolsInternalCoordinates.translate2D(molA, 0.0, outputXY[3] - interXY[1] + 1.0);				
		}			
		molB.add(molA);

		return molB;
	}
	
}
