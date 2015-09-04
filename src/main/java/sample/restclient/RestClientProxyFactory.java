package sample.restclient;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
/**
 * Created by dgitelson on 9/3/15.
 */
public class RestClientProxyFactory {
    private final String baseUrl;
    private final ClientHttpRequestFactory factory;

    public RestClientProxyFactory(String baseUrl, ClientHttpRequestFactory factory) {
        this.baseUrl = baseUrl;
        this.factory = factory;
    }

    @SuppressWarnings("unchecked")
    public <T> T createProxy(Class<T> service) {
        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class[]{service}, new InvocationHandler() {
            private final ConcurrentHashMap<Method, RestMethodTranslator> translators = new ConcurrentHashMap<>();

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return translators.computeIfAbsent(method, RestMethodTranslator::new).invoke(args);
            }
        });
    }

    private class RestMethodTranslator {
        private final RequestEntityBuilder entityBuilder;
        private final RestReflectionUtils.ReturnTypeWrapper returnTypeWrapper;

        public RestMethodTranslator(Method method) {
            RestReflectionUtils.MethodInfo methodInfo = RestReflectionUtils.buildPathParamsMap(method);
            entityBuilder = new RequestEntityBuilder(baseUrl, methodInfo.method, methodInfo.bodyIndex,
                    PathBuilders.compile(methodInfo.url, methodInfo.pathVariables),
                    new HeadersBuilder(methodInfo.headerVariables), new QueryPartBuilder(methodInfo.requestParams));
            returnTypeWrapper =  RestReflectionUtils.buildReturnTypeWrapper(method);
        }

        public Object invoke(Object[] args) {
            RestTemplate template = new RestTemplate(factory);
            try {
                ResponseEntity<?> exchange = template.exchange(entityBuilder.build(args), returnTypeWrapper.returnTypeAdapter);
                return returnTypeWrapper.translate(exchange.getBody());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
