package voxxrin.companion.persistence;

import com.google.common.base.Optional;
import org.joda.time.DateTime;
import restx.WebException;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.jongo.JongoCollection;
import voxxrin.companion.auth.AuthModule;
import voxxrin.companion.domain.Presentation;
import voxxrin.companion.domain.Rating;
import voxxrin.companion.domain.Type;
import voxxrin.companion.domain.technical.Reference;
import voxxrin.companion.auth.AuthModule;
import voxxrin.companion.domain.Presentation;
import voxxrin.companion.domain.Rating;
import voxxrin.companion.domain.Type;
import voxxrin.companion.domain.technical.Reference;
import voxxrin.companion.utils.PresentationRef;

import javax.inject.Named;

@Component
public class RatingService {

    private final JongoCollection ratings;

    public RatingService(@Named(Rating.COLLECTION) JongoCollection ratings) {
        this.ratings = ratings;
    }

    public Iterable<Rating> findPresentationRatings(String presentationId) {
        Presentation presentation = Reference.<Presentation>of(Type.presentation, presentationId).get();
        return ratings.get()
                .find("{ presentationRef: # }", PresentationRef.buildPresentationBusinessRef(presentation))
                .as(Rating.class);
    }

    public Iterable<Rating> findEventRatings(String eventId) {
        return ratings.get()
                .find("{ presentationRef: { $regex: # } }", String.format("%s:.*", eventId))
                .as(Rating.class);
    }

    public Rating ratePresentation(String presentationId, int rate) {

        if (!AuthModule.currentUser().isPresent()) {
            throw new WebException(HttpStatus.UNAUTHORIZED);
        }

        Optional<Presentation> presentation = Reference.<Presentation>of(Type.presentation, presentationId).maybeGet();
        if (!presentation.isPresent()) {
            throw new WebException(HttpStatus.NOT_FOUND);
        }

        String userId = AuthModule.currentUser().get().getId();

        String presentationRef = PresentationRef.buildPresentationBusinessRef(presentation.get());
        Rating rating = new Rating()
                .setDateTime(DateTime.now())
                .setPresentationRef(presentationRef)
                .setRate(rate)
                .setUserId(userId);

        ratings.get()
                .update("{ presentationRef: #, userId: # }", presentationRef, userId)
                .upsert()
                .with(rating);

        return rating;
    }
}
