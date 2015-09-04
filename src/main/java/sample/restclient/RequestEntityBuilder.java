package sample.restclient;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
/**
 * Created by dgitelson on 9/3/15.
 */
public class RequestEntityBuilder {
    private final String baseUrl;
    private final HttpMethod method;
    private final Integer bodyIndex;
    private final PathBuilders.PathBuilder pathBuilder;
    private final HeadersBuilder headersBuilder;
    private final QueryPartBuilder queryPartBuilder;

    public RequestEntityBuilder(String baseUrl, HttpMethod method, Integer bodyIndex, PathBuilders.PathBuilder pathBuilder,
            HeadersBuilder headersBuilder, QueryPartBuilder queryPartBuilder) {
        this.baseUrl = baseUrl;
        this.method = method;
        this.bodyIndex = bodyIndex;
        this.pathBuilder = pathBuilder;
        this.headersBuilder = headersBuilder;
        this.queryPartBuilder = queryPartBuilder;
    }

    @SuppressWarnings("unchecked")
    public RequestEntity build(Object[] args) throws URISyntaxException {
        URI url = new URI(PathBuilders.joinUrlParts(baseUrl, pathBuilder.url(args)) + queryPartBuilder.queryPart(args));
        if (bodyIndex != null) {
            return new RequestEntity(args[bodyIndex], headersBuilder.headers(args), method, url);
        }
        return new RequestEntity(headersBuilder.headers(args), method, url);
    }
}
