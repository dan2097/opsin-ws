package uk.ac.cam.ch.opsin.ws;

import org.restlet.Component;
import org.restlet.data.Protocol;

public class RestletTest {
	 public static void main(String[] args) {
		 int port = 8180;
		 try {
		     // Create a new Component.
		    Component component = new Component();
		
		     // Add a new HTTP server
		     component.getServers().add(Protocol.HTTP, port);
		     component.getClients().add(Protocol.CLAP);
		     
		     // Attach the sample application.
		     component.getDefaultHost().attach(new OPSINApplication());
		
		     // Start the component.
		     component.start();
		     System.out.println("Server started on port: " + port + " This can typically be accessed by going to http://localhost:" + port + "/");
		 } catch (Exception e) {
		     // Something is wrong.
		     e.printStackTrace();
		 }
	}
}
