package sample.restclient;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * Created by dgitelson on 9/3/15.
 */
public class RestReflectionUtils {
    public static class MethodInfo {
        public final HttpMethod method;
        public final String url;
        public final Map<String, Integer> pathVariables;
        public final Map<String, Integer> headerVariables;
        public final Map<String, Integer> requestParams;
        public final Integer bodyIndex;

        public MethodInfo(HttpMethod method, String url, Map<String, Integer> pathVariables,
                Map<String, Integer> headerVariables, Map<String, Integer> requestParams, Integer bodyIndex) {
            this.method = method;
            this.url = url;
            this.pathVariables = pathVariables;
            this.headerVariables = headerVariables;
            this.requestParams = requestParams;
            this.bodyIndex = bodyIndex;
        }
    }


    public static abstract class ReturnTypeWrapper {
        public final Class<?> returnTypeAdapter;

        public ReturnTypeWrapper(Class<?> returnTypeAdapter) {
            this.returnTypeAdapter = returnTypeAdapter;
        }

        public abstract Object translate(Object value);
    }

    public static class IdentityTypeWrapper extends ReturnTypeWrapper {
        public IdentityTypeWrapper(Class<?> returnTypeAdapter) {
            super(returnTypeAdapter);
        }

        public Object translate(Object value) {
            return value;
        }
    }

    public static class ListReturnWrapper extends ReturnTypeWrapper{
        public ListReturnWrapper(Class<?> elementClass) {
            super(Array.newInstance(elementClass, 0).getClass());
        }

        @Override
        public Object translate(Object value) {
            return Arrays.asList((Object [])value);
        }
    }

    public static MethodInfo buildPathParamsMap(Method method) {
        Map<String, Integer> pathVariables = new HashMap<>();
        Map<String, Integer> headerVariables = new HashMap<>();
        Map<String, Integer> requestParams = new HashMap<>();
        Integer bodyIndex = null;

        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int argIndex = 0; argIndex < parameterAnnotations.length; argIndex++) {
            for (Annotation annotation : parameterAnnotations[argIndex]) {
                if (annotation instanceof PathVariable) {
                    PathVariable pathVariable = (PathVariable) annotation;
                    pathVariables.put(pathVariable.value(), argIndex);
                }
                if (annotation instanceof RequestHeader) {
                    RequestHeader requestHeader = (RequestHeader) annotation;
                    headerVariables.put(requestHeader.value(), argIndex);
                }
                if (annotation instanceof RequestParam) {
                    requestParams.put(((RequestParam) annotation).value(), argIndex);
                }
                if (annotation instanceof RequestBody) {
                    bodyIndex = argIndex;
                }
            }
        }
        RequestMethod[] methods = method.getAnnotation(RequestMapping.class).method();
        HttpMethod httpMethod = translate(methods.length > 0 ? methods[0] : RequestMethod.GET);
        String url = PathBuilders.joinUrlParts(getMapping(method.getDeclaringClass()), getMapping(method));
        return new MethodInfo(httpMethod,
                url, pathVariables,
                headerVariables, requestParams, bodyIndex);
    }

    public static ReturnTypeWrapper buildReturnTypeWrapper(Method method) {
        if (method.getReturnType() == Collection.class || List.class.isAssignableFrom(method.getReturnType())) {
            ParameterizedType genericReturnType = (ParameterizedType) method.getGenericReturnType();
            if (genericReturnType != null) {
                return new ListReturnWrapper((Class<?>) genericReturnType.getActualTypeArguments()[0]);
            }
        }
        return new IdentityTypeWrapper(method.getReturnType());
    }


    private static HttpMethod translate(RequestMethod requestMethod) {
        switch (requestMethod) {
            case GET:
                return HttpMethod.GET;
            case HEAD:
                return HttpMethod.HEAD;
            case POST:
                return HttpMethod.POST;
            case PUT:
                return HttpMethod.PUT;
            case PATCH:
                return HttpMethod.PATCH;
            case DELETE:
                return HttpMethod.DELETE;
            case OPTIONS:
                return HttpMethod.OPTIONS;
            case TRACE:
                return HttpMethod.TRACE;
        }
        throw new IllegalArgumentException("Illegal method: " + requestMethod);
    }

    private static String getMapping(AnnotatedElement method) {
        return method.getAnnotation(RequestMapping.class).value()[0];
    }
}
