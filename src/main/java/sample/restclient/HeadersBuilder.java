package sample.restclient;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
/**
 * Created by dgitelson on 9/3/15.
 */
public class HeadersBuilder {
    private static final MultiValueMap<String, String> NO_ATTRIBUTES = new LinkedMultiValueMap<>();

    private static class Entry {
        public final String header;

        public final int index;

        public Entry(String header, int index) {
            this.header = header;
            this.index = index;
        }
    }

    private final Entry[] entries;

    public HeadersBuilder(Map<String, Integer> headers) {
        entries = new Entry[headers.size()];
        int i = 0;
        for (Map.Entry<String, Integer> entry : headers.entrySet()) {
            entries[i++] = new Entry(entry.getKey(), entry.getValue());
        }
    }

    public MultiValueMap<String, String> headers(Object... args) {
        if (entries.length == 0) {
            return NO_ATTRIBUTES;
        }
        MultiValueMap<String, String> result = new LinkedMultiValueMap<>();
        for (Entry entry : entries) {
            if (args[entry.index] != null) {
                result.add(entry.header, args[entry.index].toString());
            }
        }
        return result;
    }
}
