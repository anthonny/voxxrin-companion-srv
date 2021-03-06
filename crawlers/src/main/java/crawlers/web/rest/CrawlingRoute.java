package crawlers.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import crawlers.AbstractHttpCrawler;
import crawlers.CrawlingResult;
import crawlers.HttpDataFiller;
import crawlers.configuration.CrawlingConfiguration;
import crawlers.configuration.CrawlingConfigurationManager;
import crawlers.utils.Functions;
import org.slf4j.Logger;
import restx.*;
import restx.factory.Component;
import restx.http.HttpStatus;
import restx.jackson.FrontObjectMapperFactory;

import javax.inject.Named;
import java.io.IOException;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

@Component
public class CrawlingRoute extends StdRoute {

    private static final Logger logger = getLogger(CrawlingRoute.class);

    private final ImmutableMap<String, AbstractHttpCrawler> crawlers;
    private final CrawlingConfigurationManager configurationManager;
    private final HttpDataFiller httpDataFiller;
    private final ObjectMapper objectMapper;

    public CrawlingRoute(Set<AbstractHttpCrawler> crawlers,
                         CrawlingConfigurationManager configurationManager,
                         HttpDataFiller httpDataFiller,
                         @Named(FrontObjectMapperFactory.MAPPER_NAME) ObjectMapper objectMapper) {

        super("crawling", new StdRestxRequestMatcher("PUT", "/crawl"));
        this.configurationManager = configurationManager;
        this.httpDataFiller = httpDataFiller;
        this.objectMapper = objectMapper;
        this.crawlers = Maps.uniqueIndex(crawlers, Functions.CRAWLERS_MAP_INDEXER);
    }

    @Override
    public void handle(RestxRequestMatch match, RestxRequest req, RestxResponse resp, RestxContext ctx) throws IOException {

        String eventId = req.getQueryParam("eventId").get();

        CrawlingConfiguration configuration = findConfigurationOrThrow(eventId);

        AbstractHttpCrawler crawler = findCrawlerOrThrow(configuration);

        checkSecurity(req, configuration);

        CrawlingResult result;
        try {
            logger.info("Start crawling event {} with crawler {}. Token used {}", eventId, crawler.getId(), req.getQueryParam("token").get());
            result = crawler.setup(crawler.crawl(configuration), configuration);
        } catch (IOException e) {
            logger.error("Error occured during crawling (eventId = " + eventId + ")", e);
            throw new WebException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        sendResultToVoxxrin(result, eventId);

        sendResultToClient(resp, result);
    }

    private void checkSecurity(RestxRequest req, CrawlingConfiguration configuration) {
        String token = req.getQueryParam("token").get();
        if (!configuration.getTokens().contains(token)) {
            throw new WebException(HttpStatus.UNAUTHORIZED);
        }
    }

    private AbstractHttpCrawler findCrawlerOrThrow(CrawlingConfiguration configuration) {

        AbstractHttpCrawler crawler = crawlers.get(configuration.getCrawlerId());
        if (crawler == null) {
            logger.error("No crawler named {} found (handling event = {})", configuration.getCrawlerId(), configuration.getEventId());
            throw new WebException(HttpStatus.NOT_FOUND);
        }

        return crawler;
    }

    private CrawlingConfiguration findConfigurationOrThrow(String eventId) {

        CrawlingConfiguration configuration = configurationManager.findConfiguration(eventId);
        if (configuration == null) {
            logger.error("No configuration related to event {} found", eventId);
            throw new WebException(HttpStatus.BAD_REQUEST);
        }

        return configuration;
    }

    private void sendResultToVoxxrin(CrawlingResult result, String crawlerId) {
        try {
            httpDataFiller.fill(result);
        } catch (JsonProcessingException e) {
            logger.error("Error occured during filling to backend (crawlerId = " + crawlerId + ")", e);
        }
    }

    private void sendResultToClient(RestxResponse resp, CrawlingResult result) throws IOException {
        resp.setStatus(HttpStatus.OK);
        resp.setContentType("application/json");

        objectMapper.writer().writeValue(resp.getOutputStream(), result);
    }
}
