package uk.ac.cam.ch.opsin.ws;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import uk.ac.cam.ch.wwmm.opsin.NameToStructure;

/**
 *
 * @author ojd20
 * @author dl387
 * @author sea36
 */
public class OPSINWebApp extends Application {

   public OPSINWebApp() {
	   this.setStatusService(new OpsinStatusService());
   }
	
   @Override
   public Restlet createRoot() {
      Router router = new Router(getContext());
      router.attachDefault(OPSINResource.class); 
      
      // Filter to override content negotiation by file extension
      // MUST be applied before template, otherwise file extension ends up in name!
      ContentFilter filter = new ContentFilter();
      filter.setNext(router);
      
      try {
		NameToStructure.getInstance();//initialise OPSIN early
	  } catch (Exception e) {
		e.printStackTrace();
		throw new RuntimeException("OPSIN failed to intialise");
	  }
      return filter;
   }
}
