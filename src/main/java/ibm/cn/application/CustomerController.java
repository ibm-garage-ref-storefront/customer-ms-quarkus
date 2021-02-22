package ibm.cn.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/micro/customer/resource")
public class CustomerController {
	
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getResponse() {
        return "CustomerResource response";
    }

}
