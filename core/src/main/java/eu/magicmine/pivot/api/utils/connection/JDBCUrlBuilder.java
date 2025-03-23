package eu.magicmine.pivot.api.utils.connection;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
@Getter
@Setter
public class JDBCUrlBuilder {

    private String service;

    private String host;

    private int port = 3306;

    private String database;

    private Map<String,Object> properties = new HashMap<>();


    public JDBCUrlBuilder property(String property,Object value) {
        properties.put(property,value);
        return this;
    }

    public String build() {
        StringBuilder urlBuilder = new StringBuilder("jdbc:" + service + "://");
        urlBuilder
                .append(host)
                .append(":")
                .append(port)
                .append("/")
                .append(database);

        boolean first = true;

        for (Map.Entry<String, Object> property : properties.entrySet()) {

            char sep = first ? '?' : '&';
            urlBuilder
                    .append(sep)
                    .append(property.getKey())
                    .append("=")
                    .append(property.getValue());

            first = false;
        }

        return urlBuilder.toString();
    }

}
