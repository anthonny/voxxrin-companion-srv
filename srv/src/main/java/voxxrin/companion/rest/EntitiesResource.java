package voxxrin.companion.rest;

import restx.annotations.DELETE;
import restx.annotations.Param;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.RolesAllowed;
import voxxrin.companion.persistence.DataService;
import voxxrin.companion.persistence.DataService;

import java.util.Set;

@Component
@RestxResource
public class EntitiesResource {

    private final Set<DataService> dataServices;

    public EntitiesResource(Set<DataService> dataServices) {
        this.dataServices = dataServices;
    }

    @RolesAllowed({"ADM", "restx-admin"})
    @DELETE("/entities/crawled")
    public void cleanCrawledData(@Param(kind = Param.Kind.QUERY) String eventId) {
        for (DataService dataService : dataServices) {
            dataService.removeCrawledEntities(eventId);
        }
    }
}
