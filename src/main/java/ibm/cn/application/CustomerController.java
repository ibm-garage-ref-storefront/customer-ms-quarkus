package ibm.cn.application;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jboss.logging.Logger;

@Path("/micro/customer/resource")
public class CustomerController {
	
	private static final Logger LOG = Logger.getLogger(CustomerController.class);
	
	@GET
    @Produces(MediaType.TEXT_PLAIN)
    public String getResponse() {
		LOG.info("CustomerResource response");
        return "CustomerResource response";
    }

}
