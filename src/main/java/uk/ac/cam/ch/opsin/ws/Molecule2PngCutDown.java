package uk.ac.cam.ch.opsin.ws;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import org.openscience.cdk.geometry.GeometryToolsInternalCoordinates;
import org.openscience.cdk.interfaces.IMolecule;
import org.openscience.cdk.renderer.Renderer2D;
import org.openscience.cdk.renderer.Renderer2DModel;

/** NOTE THAT THIS IS A CUTDOWN VERSION OF THIS CLASS (DL387)
 * Produces png images for CDK molecules. Configure this by setting the
 * public variables.
 * 
 * @author ptc24
 *
 */
public class Molecule2PngCutDown {
	
	public Color backgroundColour = Color.WHITE;
	public String fontName = "MonoSpaced";
	public int fontStyle = Font.BOLD;
	public int fontSize = 16;
	public boolean colourAtoms = true;
	
	public boolean fixedWidthAndHeight = false;
	/* If fixedWidthAndHeight... */
	public int width = 500;
	public int height = 500;
	public double occupationFactor = 0.8; /* 1.0 = no border */
	/* else ... */
	public double scaleFactor = 20.0;
	public int borderWidth = 20; /* Pixels. This is *after* a sensible margin for lettering */
	/* ...endif */
	
	/**Set up a new Molecule2Png with default options.
	 * 
	 */
	public Molecule2PngCutDown() {
	}
	
	/**Renders molecule
	 *  
	 * @param mol The molecule.
	 * @return 
	 * @throws Exception
	 */
	public BufferedImage renderMolecule(IMolecule mol) throws Exception {
		Renderer2DModel r2dm = new Renderer2DModel();
		Renderer2D r2d = new Renderer2D(r2dm);
		GeometryToolsInternalCoordinates.translateAllPositive(mol);
		if(fixedWidthAndHeight) {
			r2dm.setBackgroundDimension(new Dimension(width, height));
			GeometryToolsInternalCoordinates.scaleMolecule(mol, r2dm.getBackgroundDimension(), occupationFactor);        	
		} else {
			double [] cvals = GeometryToolsInternalCoordinates.getMinMax(mol);
			width = (int) Math.round(((cvals[2] - cvals[0]) * scaleFactor) + (fontSize*3)/2 + 3 + borderWidth * 2);
			height = (int) Math.round(((cvals[3] - cvals[1]) * scaleFactor) + fontSize/2 + 1 + borderWidth * 2);
			GeometryToolsInternalCoordinates.scaleMolecule(mol, scaleFactor);
			r2dm.setBackgroundDimension(new Dimension(width, height));
		}
		GeometryToolsInternalCoordinates.center(mol, r2dm.getBackgroundDimension());
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		Graphics g = img.getGraphics();
		g.setColor(backgroundColour);		
		g.fillRect(0, 0, width, height);
		r2dm.setBackColor(backgroundColour);
		r2dm.setFont(new Font(fontName, fontStyle, fontSize));
		r2dm.setColorAtomsByType(colourAtoms);
		r2d.paintMolecule(mol, img.createGraphics(), true, true);
		return img;
	}
}
