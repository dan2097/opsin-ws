/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.ch.opsin.ws;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.data.Status;

/**
 *
 * @author ojd20
 */
public class APITest {

   private static MediaType TYPE_SMILES = new MediaType(
           "chemical/x-daylight-smiles");

   @Test
   public void apiTest() throws IOException {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET,
                                "http://localhost:8989/opsin-ws/methane");
      req.getClientInfo().getAcceptedMediaTypes().clear();
      req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(
              TYPE_SMILES));
      Response res = client.handle(req);
      String smiles = res.getEntity().getText();
      Assert.assertEquals("C(H)(H)(H)H", smiles);
   }

   @Test
   public void apiTestBadName() throws IOException {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET,
                                "http://localhost:8989/opsin-ws/BilboBaggins");
      req.getClientInfo().getAcceptedMediaTypes().clear();
      req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(
              TYPE_SMILES));
      Response res = client.handle(req);
      Assert.assertEquals(Status.CLIENT_ERROR_NOT_FOUND, res.getStatus());
   }
}
