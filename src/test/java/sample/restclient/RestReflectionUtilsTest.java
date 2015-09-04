package sample.restclient;
import java.util.Collections;

import org.junit.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.junit.Assert.assertEquals;
/**
 * Created by dgitelson on 9/3/15.
 */
@SuppressWarnings("unused")
public class RestReflectionUtilsTest {
    @RequestMapping("/abc")
    public static class SomeService {
        @RequestMapping("/{name}/doSomething")
        @ResponseBody
        public String doSomething(
                @PathVariable("name") String name,
                @RequestHeader("header") String header,
                @RequestParam("rparam") String rParam,
                @RequestBody String body
        ) {
            return null;
        }
    }

    @Test
    public void testMethodInfo() throws Exception {
        RestReflectionUtils.MethodInfo methodInfo = RestReflectionUtils
                .buildPathParamsMap(SomeService.class.getDeclaredMethods()[0]);

        assertEquals(Collections.singletonMap("header", 1), methodInfo.headerVariables);
        assertEquals(Collections.singletonMap("rparam", 2), methodInfo.requestParams);
        assertEquals(Collections.singletonMap("name", 0), methodInfo.pathVariables);
        assertEquals(Integer.valueOf(3), methodInfo.bodyIndex);
    }

}