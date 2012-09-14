package com.rallydev.client.http;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Resource {
    private static final Pattern RESOURCE_PATTERN = Pattern.compile("/([^/]+)/([^/]+).js");

    private final String type;
    private final String id;

    public static boolean isValid(String resource) {
        if(resource == null) return false;

        return RESOURCE_PATTERN.matcher(resource).matches();
    }

    public static boolean isValidForType(String type, String resource) {
        if(isValid(resource)) {
            return parse(resource).getType().equals(type);
        }
        
        return false;
    }

    public static Resource parse(String resource) {
        Matcher matcher = RESOURCE_PATTERN.matcher(resource);

        if(matcher.find()) {
            return new Resource(matcher.group(1), matcher.group(2));
        } else {
            throw new InvalidResourceException(format("'%s' is not a valid resource", resource));
        }
    }

    public static String asString(String type, String id) {
        return format("/%s/%s.js", type, id);
    }

    public Resource(String type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public String asString() {
        return asString(type, id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Resource resource = (Resource) o;

        if (!id.equals(resource.id)) return false;
        if (!type.equals(resource.type)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }

    public static class InvalidResourceException extends RuntimeException {
        public InvalidResourceException(String message) {
            super(message);
        }
    }
}