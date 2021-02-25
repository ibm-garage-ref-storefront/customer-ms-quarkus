package ibm.cn.application.couchdb.client;

import java.util.Base64;

import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.ConfigProvider;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CouchDBAuthHeader {
	
	// https://github.com/quarkusio/quarkus/issues/13660
	public static String authorizationHeader() {
		Config config = ConfigProvider.getConfig();
		String user = config.getValue("couchuser", String.class);
		String password = config.getValue("couchpassword", String.class);
		String creds = user+":"+password;
	    return "Basic " + 
	         Base64.getEncoder().encodeToString(creds.getBytes());
    }

}
