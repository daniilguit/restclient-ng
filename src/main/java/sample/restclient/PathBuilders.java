package sample.restclient;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
/**
 * Created by dgitelson on 9/3/15.
 */
public class PathBuilders {
    public static String joinUrlParts(String u1, String u2) {
        if (u1.endsWith("/")) {
            if (u2.startsWith("/")) {
                return u1 + u2.substring(1);
            } else {
                return u1 + u2;
            }
        }
        if (u2.startsWith("/")) {
            return u1 + u2;
        }
        return u1 + "/" + u2;
    }

    public interface PathBuilder {
        String url(Object[] args);
    }
    public static class ConstantPathBuilder implements PathBuilder {
        private final String url;

        public ConstantPathBuilder(String url) {
            this.url = url;
        }

        @Override
        public String url(Object[] args) {
            return url;
        }
    }

    public static class ArgPathBuilder implements PathBuilder {
        private final int index;

        public ArgPathBuilder(int index) {
            this.index = index;
        }

        @Override
        public String url(Object[] args) {
            return args[index].toString();
        }
    }

    private static class PatternPathBuilder implements PathBuilder {
        private final PathBuilder[] builders;

        private PatternPathBuilder(PathBuilder[] builders) {
            this.builders = builders;
        }

        @Override
        public String url(Object[] args) {
            StringBuilder result = new StringBuilder();
            for (PathBuilder builder : builders) {
                result.append(builder.url(args));
            }
            return result.toString();
        }
    }

    public static PathBuilder compile(String pattern, Map<String, Integer> indicies) {
        if (!pattern.contains("{")) {
            return new ConstantPathBuilder(pattern);
        }
        int current = 0;
        List<PathBuilder> builderList = new ArrayList<>();
        do {
            int nextPlaceholder = pattern.indexOf('{', current);
            if (nextPlaceholder < 0) {
                builderList.add(new ConstantPathBuilder(pattern.substring(current)));
                break;
            }
            if (current != nextPlaceholder) {
                builderList.add(new ConstantPathBuilder(pattern.substring(current, nextPlaceholder)));
            }
            int nextPlaceholderEnds = pattern.indexOf('}', nextPlaceholder + 1);
            if (nextPlaceholderEnds < 0) {
                throw new IllegalArgumentException("Invalid url pattern: " + pattern);
            }
            String paramName = pattern.substring(nextPlaceholder + 1, nextPlaceholderEnds);
            Integer index = indicies.get(paramName);
            if (index == null) {
                throw new IllegalArgumentException("No param with name " + paramName + " in indicies map: " + indicies);
            }
            builderList.add(new ArgPathBuilder(index));
            current = nextPlaceholderEnds + 1;
        } while (current < pattern.length());
        return new PatternPathBuilder(builderList.toArray(new PathBuilder[builderList.size()]));
    }
}
