package in.vasista.vbiz.rest;


import java.util.Map;
 
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
 
import javolution.util.FastMap;
 
import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.DelegatorFactory;
import org.ofbiz.entity.GenericDelegator;
import org.ofbiz.service.GenericDispatcher;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.LocalDispatcher;
import org.ofbiz.service.ServiceUtil;
 
@Path("/ping")
public class PingResource {
 
    @Context
    HttpHeaders headers;
 
    @GET
    @Produces("text/plain")
    @Path("{message}")
    public Response sayHello(@PathParam("message") String message) {
 
        String username = null;
        String password = null;
 
        try {
            username = headers.getRequestHeader("login.username").get(0);
            password = headers.getRequestHeader("login.password").get(0);
        } catch (NullPointerException e) {
            return Response.serverError().entity("Problem reading http header(s): login.username or login.password").build();
        }
 
        if (username == null || password == null) {
            return Response.serverError().entity("Problem reading http header(s): login.username or login.password").build();
        }
 
        GenericDelegator delegator = (GenericDelegator) DelegatorFactory.getDelegator("default");
        LocalDispatcher dispatcher = GenericDispatcher.getLocalDispatcher("default",delegator);
 
        Map<String, String> paramMap = UtilMisc.toMap(
                "message", message,
                "login.username", username,
                "login.password", password
            );
 
        Map<String, Object> result = FastMap.newInstance();
        try {
            result = dispatcher.runSync("ping", paramMap);
        } catch (GenericServiceException e1) {
            Debug.logError(e1, PingResource.class.getName());
            return Response.serverError().entity(e1.toString()).build();
        }
 
        if (ServiceUtil.isSuccess(result)) {
            return Response.ok("RESPONSE: *** " + result.get("message") + " ***").type("text/plain").build();
        }
 
        if (ServiceUtil.isError(result) || ServiceUtil.isFailure(result)) {
            return Response.serverError().entity(ServiceUtil.getErrorMessage(result)).build();
        }
 
        // shouldn't ever get here ... should we?
        throw new RuntimeException("Invalid ");
    }
}