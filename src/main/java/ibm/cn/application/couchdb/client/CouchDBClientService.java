package ibm.cn.application.couchdb.client;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jose4j.json.internal.json_simple.JSONObject;

@RegisterRestClient
@ClientHeaderParam(name = "Authorization", value = "{ibm.cn.application.couchdb.client.CouchDBAuthHeader.authorizationHeader}")
@Produces("application/json")
@Path("/customers")
@RegisterProvider(UnknownCustomerExceptionManager.class)
public interface CouchDBClientService {
		
	@GET
	@Produces("application/json")
	public Response getInfo() throws UnknownCustomerException;
	
	@POST
    @Path("/_find")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getUsername(JSONObject body) throws UnknownCustomerException;

}
