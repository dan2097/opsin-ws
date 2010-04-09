package uk.ac.cam.ch.opsin.ws;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.ArrayList;
import java.util.List;

import nu.xom.Element;

import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.layout.StructureDiagramGenerator;
import org.openscience.cdk.renderer.AtomContainerRenderer;
import org.openscience.cdk.renderer.font.AWTFontManager;

import org.openscience.cdk.renderer.generators.BasicBondGenerator;
import org.openscience.cdk.renderer.generators.BasicSceneGenerator;
import org.openscience.cdk.renderer.generators.ExtendedAtomGenerator;
import org.openscience.cdk.renderer.generators.IAtomContainerGenerator;
import org.openscience.cdk.renderer.visitor.AWTDrawVisitor;
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
		int width = 640;
		int height = 480;

		// the draw area and the image should be the same size
		Rectangle drawArea = new Rectangle(width, height);
		Image image = new BufferedImage(
		  width, height, BufferedImage.TYPE_INT_RGB
		);

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
		StructureDiagramGenerator sdg = new StructureDiagramGenerator();
		sdg.setMolecule(molecule);
		sdg.generateCoordinates();
		molecule = sdg.getMolecule();

		// generators make the image elements
		List<IAtomContainerGenerator> generators = new ArrayList<IAtomContainerGenerator>();
		generators.add(new BasicSceneGenerator());
		generators.add(new BasicBondGenerator());
		generators.add(new ExtendedAtomGenerator());

		// the renderer needs to have a toolkit-specific font manager
		AtomContainerRenderer renderer = new AtomContainerRenderer(generators, new AWTFontManager());

		// the call to 'setup' only needs to be done on the first paint
		renderer.setup(molecule, drawArea);

		// paint the background
		Graphics2D g2 = (Graphics2D)image.getGraphics();
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, width, height);

		// the paint method also needs a toolkit-specific renderer
		renderer.paint(molecule, new AWTDrawVisitor(g2));
		System.out.println("!!!");
		return (RenderedImage) image;
	}
}
