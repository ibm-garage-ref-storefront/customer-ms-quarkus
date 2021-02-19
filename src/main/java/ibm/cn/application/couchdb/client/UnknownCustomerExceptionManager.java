package ibm.cn.application.couchdb.client;

import java.util.logging.Logger;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

public class UnknownCustomerExceptionManager implements ResponseExceptionMapper<UnknownCustomerException> {

	Logger LOG = Logger.getLogger(UnknownCustomerExceptionManager.class.getName());
	
	@Override
	public UnknownCustomerException toThrowable(Response response) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	  public boolean handles(int status, MultivaluedMap<String, Object> headers) {
	    LOG.info("status = " + status);
	    return status == 404;
	}

}
