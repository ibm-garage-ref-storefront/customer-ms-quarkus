package ibm.cn.application.couchdb.client;

import java.util.Base64;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jose4j.json.internal.json_simple.JSONObject;

@RegisterRestClient
@ClientHeaderParam(name = "Authorization", value = "{lookupAuth}")
@Produces("application/json")
@Path("/customers")
@RegisterProvider(UnknownCustomerExceptionManager.class)
public interface CouchDBClientService {
	
	default String lookupAuth() {
		Config config = ConfigProvider.getConfig();
		String user = config.getValue("couchuser", String.class);
		String password = config.getValue("couchpassword", String.class);
		String creds = user+":"+password;
	    return "Basic " + 
	         Base64.getEncoder().encodeToString(creds.getBytes());
	}
	
	@GET
	@Produces("application/json")
	public Response getInfo() throws UnknownCustomerException;
	
	@POST
    @Path("/_find")
    @Produces("application/json")
    @Consumes("application/json")
    public Response getUsername(JSONObject body) throws UnknownCustomerException;

}
