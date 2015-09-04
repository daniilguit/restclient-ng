package sample;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import sample.restclient.RestClientProxyFactory;
/**
 * Created by dgitelson on 9/3/15.
 */
@Configuration
public class SampleClient {
    @Bean
    public HttpClient httpClient() {
        CloseableHttpClient build = HttpClientBuilder.create().build();
        return build;
    }

    @Bean
    public ClientHttpRequestFactory httpRequestFactory(HttpClient httpClient) {
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    public RestClientProxyFactory restClientProxyFactory(@Value("http://localhost:8080") String baseUrl,
            ClientHttpRequestFactory factory) {
        return new RestClientProxyFactory(baseUrl, factory);
    }

    @Bean
    public SampleServiceApi sampleServiceStab(RestClientProxyFactory factory) {
        return factory.createProxy(SampleServiceApi.class);
    }

    public static void main(String[] args) {
        CommandLinePropertySource propertySource = new SimpleCommandLinePropertySource(args);
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.getEnvironment().getPropertySources().addFirst(propertySource);
        context.register(SampleClient.class);
        context.refresh();

        SampleServiceApi bean = context.getBean(SampleServiceApi.class);

        System.out.printf("Fetching sample: %s", bean.sample());
        System.out.printf("Fetching single point from path /10/20: %s", bean.singlePointAsArray(10, 20));
        System.out.printf("Transposing vector (20, 10) : %s", bean.transpose(new SampleServiceApi.Input(20, 10)));
    }
}
