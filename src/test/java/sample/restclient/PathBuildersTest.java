package sample.restclient;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.*;

import static org.junit.Assert.*;
/**
 * Created by dgitelson on 9/3/15.
 */
public class PathBuildersTest {
    @Test
    public void testCompileConst() throws Exception {
        assertEquals("/abc", PathBuilders.compile("/abc", Collections.<String, Integer>emptyMap())
                .url(new Object[]{"a", 1, "b"}));
    }

    @Test
    public void testCompileAttr() throws Exception {
        Map<String, Integer> indicies = new HashMap<>();
        indicies.put("x", 2);
        indicies.put("y", 1);
        indicies.put("z", 0);
        assertEquals("/abc/a/1/hello",
                PathBuilders.compile("/abc/{x}/{y}/hello", indicies).url(new Object[]{"b", 1, "a"}));
    }
}