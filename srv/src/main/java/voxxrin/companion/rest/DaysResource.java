package voxxrin.companion.rest;

import com.google.common.base.Optional;
import org.bson.types.ObjectId;
import restx.annotations.GET;
import restx.annotations.POST;
import restx.annotations.RestxResource;
import restx.factory.Component;
import restx.security.PermitAll;
import restx.security.RolesAllowed;
import voxxrin.companion.domain.Day;
import voxxrin.companion.persistence.DaysDataService;
import voxxrin.companion.domain.Day;
import voxxrin.companion.persistence.DaysDataService;

@Component
@RestxResource
public class DaysResource {

    private final DaysDataService daysDataService;

    public DaysResource(DaysDataService daysDataService) {
        this.daysDataService = daysDataService;
    }

    @GET("/days")
    @PermitAll
    public Iterable<Day> getDays() {
        return daysDataService.findAllAndSort("{ date : 1 }");
    }

    @GET("/days/{id}")
    @PermitAll
    public Optional<Day> getDay(String id) {
        if (ObjectId.isValid(id)) {
            return daysDataService.findById(id);
        } else {
            return daysDataService.findByAlias(id);
        }
    }

    @POST("/days")
    @RolesAllowed({"ADM", "restx-admin"})
    public Day saveDay(Day day) {
        return daysDataService.save(day);
    }

    @GET("/events/{eventId}/days")
    @PermitAll
    public Iterable<Day> getEventDays(String eventId) {
        if (ObjectId.isValid(eventId)) {
            return daysDataService.findByEvent(eventId);
        } else {
            return daysDataService.findByEventAlias(eventId);
        }
    }
}
