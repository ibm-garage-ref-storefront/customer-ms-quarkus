package ibm.cn.application;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jose4j.json.internal.json_simple.JSONArray;
import org.jose4j.json.internal.json_simple.JSONObject;

import ibm.cn.application.couchdb.client.CouchDBClientService;

@Path("/micro/customer")
public class CustomerResource {
	
	@Inject
	@RestClient
	CouchDBClientService cdb;
	
	@Inject
    JsonWebToken jwt;
    
    @SuppressWarnings("unchecked")
	@GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user","admin"})
    public Response getCustomerByUsername() throws Exception{
        try {
            JSONObject body = new JSONObject();
            JSONObject selector = new JSONObject();
            
            selector.put("username", jwt.getName());
            
            body.put("selector", selector);
            JSONArray fields = new JSONArray();
            body.put("fields", fields);
            body.put("limit", 1);
          
            return cdb.getUsername(body);

        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println(Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getLocalizedMessage()).build());
            throw new Exception(e.toString());
        }
    }
}