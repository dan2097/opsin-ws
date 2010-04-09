package uk.ac.cam.ch.opsin.ws;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class RestletTest {
	 public static void main(String[] args) {
		 try {
		     // Create a new Component.
		    Component component = new Component();
		
		     // Add a new HTTP server listening on port 8180.
		     component.getServers().add(Protocol.HTTP, 8180);
		     component.getClients().add(Protocol.CLAP);
		     
		     // Attach the sample application.
		     component.getDefaultHost().attach(new OPSINApplication());
		
		     // Start the component.
		     component.start();
		 } catch (Exception e) {
		     // Something is wrong.
		     e.printStackTrace();
		 }
	}
}
