package uk.ac.cam.ch.opsin.ws;

import java.util.Arrays;
import java.util.HashSet;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.service.CorsService;

/**
 *
 * @author ojd20
 * @author dl387
 * @author sea36
 */
public class OpsinWebApp extends Application {

    public OpsinWebApp() {
        this.setStatusService(new OpsinStatusService());
        CorsService corsService = new CorsService();         
        corsService.setAllowedOrigins(new HashSet<String>(Arrays.asList("*")));
        corsService.setAllowedCredentials(true);
        this.getServices().add(corsService);
    }
    
    @Override
    public Restlet createInboundRoot() {
        Router router = new Router(getContext());
        router.attachDefault(OpsinResource.class); 

        // Filter to override content negotiation by file extension
        // MUST be applied before template, otherwise file extension ends up in name!
        ContentFilter filter = new ContentFilter();
        filter.setNext(router);
        return filter;
    }

}
