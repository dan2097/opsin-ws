/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.ch.opsin.ws;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

/**
 *
 * @author ojd20
 */
public class OPSINApplication extends Application {
//	public OPSINApplication() {
//	    this.setStatusService(new OpsinStatusService());
//	}

   @Override
   public Restlet createRoot() {
      Router router = new Router(getContext());
      
      
      router.attach("/opsin/", OPSINResource.class);
      
      router.attachDefault(new Directory(getContext(), "clap://thread/")); 
      
      // Filter to override content negotiation by file extension
      // MUST be applied before template, otherwise file extension ends up in name!
      ContentFilter filter = new ContentFilter();
      filter.setNext(router);

      // Nasty hack so Daniel can run this without a proper servlet container
      IndexFilter indexFilter = new IndexFilter();
      indexFilter.setNext(filter);
      
      
      try {
		NameToStructure.getInstance();//initialise OPSIN early
	  } catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException("OPSIN failed to intialise");
	  }
      return indexFilter;
   }
}
