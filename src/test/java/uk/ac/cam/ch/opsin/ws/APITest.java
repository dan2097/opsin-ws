/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.cam.ch.opsin.ws;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 *
 * @author ojd20
 */
public class APITest {

   private static MediaType TYPE_INCHI = new MediaType(
           "chemical/x-inchi");

   @Test
   public void apiTest() throws IOException {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET,
                                "http://localhost:8989/opsin/methane");
      req.getClientInfo().getAcceptedMediaTypes().clear();
      req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(
    		  TYPE_INCHI));
      Response res = client.handle(req);
      String inchi = res.getEntity().getText();
      Assert.assertEquals("InChI=1/CH4/h1H4", inchi);
   }

   @Test
   public void apiTestBadName() throws IOException {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET,
                                "http://localhost:8989/opsin/BilboBaggins");
      req.getClientInfo().getAcceptedMediaTypes().clear();
      req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(
    		  TYPE_INCHI));
      Response res = client.handle(req);
      Assert.assertEquals(Status.CLIENT_ERROR_NOT_FOUND, res.getStatus());
   }
}
