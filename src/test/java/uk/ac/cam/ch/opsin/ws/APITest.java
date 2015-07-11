package uk.ac.cam.ch.opsin.ws;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.restlet.Client;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Preference;
import org.restlet.data.Protocol;
import org.restlet.Component;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;

/**
 *
 * @author ojd20
 * @author dl387
 */
public class APITest {

   private static MediaType TYPE_INCHI = new MediaType("chemical/x-inchi");
   private static Component component;
   
   @BeforeClass
   public static void setup() throws Exception{
     component = new Component();
     component.getServers().add(Protocol.HTTP, 8989);
     component.getDefaultHost().attachDefault(new OpsinWebApp());
     component.start();
   }
   
   @AfterClass
   public static void cleanup() throws Exception{
     component.stop();
   }

   @Test
   public void apiTestContentNegotiation() throws Exception {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET, "http://localhost:8989/opsin/methane");
      req.getClientInfo().getAcceptedMediaTypes().clear();
      req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(TYPE_INCHI));
      Response res = client.handle(req);
      String inchi = res.getEntity().getText();
      Assert.assertEquals("InChI=1/CH4/h1H4", inchi);
   }

   @Test
   public void apiTestBadName() throws IOException {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET, "http://localhost:8989/opsin/BilboBaggins");
      req.getClientInfo().getAcceptedMediaTypes().clear();
      req.getClientInfo().getAcceptedMediaTypes().add(new Preference<MediaType>(TYPE_INCHI));
      Response res = client.handle(req);
      Assert.assertEquals(Status.CLIENT_ERROR_NOT_FOUND, res.getStatus());
   }
   
   @Test
   public void apiTestExtension() throws Exception {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET, "http://localhost:8989/opsin/methane.smi");
      Response res = client.handle(req);
      String smiles = res.getEntity().getText();
      Assert.assertEquals("C", smiles);
   }
   
   @Test
   public void apiCmlWithCoords() throws Exception {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET, "http://localhost:8989/opsin/toluene.cml");
      Response res = client.handle(req);
      String cml = res.getEntity().getText();
      Assert.assertTrue(cml.length() > 0);
      Matcher m = Pattern.compile("<atom [^<]*x2=\"[^<\"]*\\d[^<\"]*\"[^<]+y2=\"[^<\"]*\\d[^<\"]*\"").matcher(cml);
      int matches = 0;
      while(m.find()) {
          matches++;
      }
      Assert.assertEquals(15, matches);
   }
   
   @Test
   public void apiCmlNoCoords() throws Exception {
      Client client = new Client(Protocol.HTTP);
      Request req = new Request(Method.GET, "http://localhost:8989/opsin/toluene.no2d.cml");
      Response res = client.handle(req);
      String cml = res.getEntity().getText();
      Assert.assertTrue(cml.length() > 0);

      Assert.assertFalse(cml.contains(" x2=\""));
      Assert.assertFalse(cml.contains(" y2=\""));
      Matcher m = Pattern.compile("<atom ").matcher(cml);
      int matches = 0;
      while(m.find()) {
          matches++;
      }
      Assert.assertEquals(15, matches);
   }
}
