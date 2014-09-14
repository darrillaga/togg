package com.t3coode.togg.services.utils;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

public interface Jsonable {

    String toJSON();

    Map<String, JsonNode> toJsonNodes();

    Map<String, JsonNode> toJsonNodes(String wrapperName);

    Map<String, JsonNode> toJsonNodes(Class<?> view);

    Map<String, JsonNode> toJsonNodes(Class<?> view, String wrapperName);

    void loadFromJSON(String json);
}
