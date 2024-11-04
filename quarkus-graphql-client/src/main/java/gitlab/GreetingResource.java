package gitlab;

import java.util.List;

import gitlab.api.WorkitemClientApi;
import gitlab.model.WorkItemConnection;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("")
public class GreetingResource {

    @Inject
    WorkitemClientApi api;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/hardcoded")
    @Produces(MediaType.TEXT_PLAIN)
    public String read() {
        String p = "stage/main/product-management";
        List<String> r = List.of(
                "&1");
        WorkItemConnection response = api.workItemsByReference(p, r);
        return response.toString();
    }

    @GET
    @Path("/read")
    @Produces(MediaType.TEXT_PLAIN)
    public String read(@QueryParam("path") String path, @QueryParam("refs") List<String> refs) {
        WorkItemConnection response = api.workItemsByReference(path, refs);
        return response.toString();
    }
}
