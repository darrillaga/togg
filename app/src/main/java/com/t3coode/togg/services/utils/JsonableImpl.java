package com.t3coode.togg.services.utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.t3coode.togg.services.TogglBaseService;
import com.t3coode.togg.services.dtos.BaseDTO;
import com.t3coode.togg.services.utils.DateUtil.JodaDateFormat;

public abstract class JsonableImpl implements Jsonable {

    @Override
    public String toJSON() {
        ObjectMapper mapper = createMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Map<String, JsonNode> toJsonNodes() {
        return toJsonNodes(null, null);
    }

    @Override
    public Map<String, JsonNode> toJsonNodes(String wrapperName) {
        return toJsonNodes(null, wrapperName);
    }

    @Override
    public Map<String, JsonNode> toJsonNodes(Class<?> view) {
        return toJsonNodes(view, null);
    }

    @Override
    public Map<String, JsonNode> toJsonNodes(Class<?> view, String wrapperName) {
        try {

            ObjectMapper mapper = createMapper();

            if (view != null) {
                mapper = mapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION,
                        false);
            }

            Map<String, JsonNode> fieldsMap = new HashMap<String, JsonNode>();
            JsonNode node;

            if (view != null) {
                node = mapper.readValue(mapper.writerWithView(view)
                        .writeValueAsString(this), JsonNode.class);
            } else {
                node = mapper.convertValue(this, JsonNode.class);
            }

            Iterator<Entry<String, JsonNode>> it = node.fields();
            while (it.hasNext()) {
                Entry<String, JsonNode> field = it.next();
                fieldsMap.put(field.getKey(), field.getValue());
            }

            if (wrapperName != null) {
                Map<String, JsonNode> wrapper = new HashMap<String, JsonNode>();
                wrapper.put(wrapperName,
                        mapper.convertValue(fieldsMap, JsonNode.class));
                fieldsMap = wrapper;
            }

            return fieldsMap;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void loadFromJSON(String json) {
        try {
            ObjectMapper mapper = createMapper();

            new NullAwareBeanUtils().copyProperties(this,
                    mapper.readValue(json, this.getClass()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> List<T> loadAllFromJSON(String json) {
        try {
            return createMapper().readValue(json, new TypeReference<T>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String allToJSON(List<? extends BaseDTO> list) {
        try {
            return createMapper().writeValueAsString(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static ObjectMapper createMapper() {
        return new ObjectMapper().setDateFormat(new JodaDateFormat(
                TogglBaseService.DATE_FORMAT));
    }
}
