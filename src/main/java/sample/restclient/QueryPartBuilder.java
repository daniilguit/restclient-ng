package sample.restclient;
import java.util.Map;
/**
 * Created by dgitelson on 9/3/15.
 */
public class QueryPartBuilder {
    private static class Entry {
        public final String name;

        public final int index;

        public Entry(String name, int index) {
            this.name = name;
            this.index = index;
        }
    }

    private final Entry[] entries;

    public QueryPartBuilder(Map<String, Integer> queryParams) {
        entries = new Entry[queryParams.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : queryParams.entrySet()) {
            entries[i++] = new Entry(entry.getKey(), entry.getValue());
        }
    }

    public String queryPart(Object... args) {
        if (entries.length == 0) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (Entry entry : entries) {
            if (args[entry.index] != null) {
                result.append(entry.name).append("=").append(args[entry.index].toString()).append("&");
            }
        }
        result.setLength(result.length() - 1);
        return result.toString();
    }
}
