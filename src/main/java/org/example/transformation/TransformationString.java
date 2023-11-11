package org.example.transformation;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public class TransformationString {

    public static String serialize(Object object) {
        StringBuilder json = new StringBuilder("{");

        Field[] fields = object.getClass().getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);

            String fieldName = field.getName();
            Object fieldValue = null;
            try {
                fieldValue = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            json.append("\"").append(fieldName).append("\":");
            if (fieldValue != null) {
                if (field.getType().isPrimitive() || fieldValue instanceof Number || fieldValue instanceof Boolean) {
                    json.append(fieldValue);
                } else if (field.getType().equals(String.class)) {
                    json.append("\"").append(fieldValue).append("\"");
                } else if (field.getType().isArray()) {
                    json.append(serializeArray(fieldValue));
                } else if (field.getType().equals(LocalDate.class)) {
                    json.append("\"").append(fieldValue.toString()).append("\"");
                } else if (field.getType().equals(LocalDateTime.class)) {
                    json.append("\"").append(fieldValue.toString()).append("\"");
                } else if (fieldValue instanceof Collection<?>) {
                    json.append(serializeCollection((Collection<?>) fieldValue));
                } else if (fieldValue instanceof Map<?, ?>) {
                    json.append(serializeHashMap((Map<?, ?>) fieldValue));
                } else {
                    json.append(serialize(fieldValue));
                }
            } else {
                json.append("null");
            }

            if (i < fields.length - 1) {
                json.append(", \n ");
            }
        }

        json.append("}");
        return json.toString();
    }

    private static String serializeArray(Object array) {
        StringBuilder jsonArray = new StringBuilder("[");
        int length = java.lang.reflect.Array.getLength(array);
        for (int i = 0; i < length; i++) {
            Object element = java.lang.reflect.Array.get(array, i);
            if (element != null) {
                if (element.getClass().isPrimitive() || element instanceof Number || element instanceof Boolean) {
                    jsonArray.append(element);
                } else if (element.getClass().equals(String.class)) {
                    jsonArray.append("\"").append(element).append("\"");
                } else {
                    jsonArray.append(serialize(element));
                }
            } else {
                jsonArray.append("null");
            }

            if (i < length - 1) {
                jsonArray.append(",");
            }
        }
        jsonArray.append("]");
        return jsonArray.toString();
    }

    private static String serializeHashMap(Map<?, ?> hashMap) {
        StringBuilder jsonArray = new StringBuilder("{");
        int count = 0;

        for (Map.Entry<?, ?> entry : hashMap.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key != null && value != null) {
                if (key.getClass().equals(String.class)) {
                    jsonArray.append("\"").append(key).append("\"");
                } else {
                    jsonArray.append(serialize(key));
                }
                jsonArray.append(":");
                if (value.getClass().isPrimitive() || value instanceof Number || value instanceof Boolean) {
                    jsonArray.append(value);
                } else if (value.getClass().equals(String.class)) {
                    jsonArray.append("\"").append(value).append("\"");
                } else {
                    jsonArray.append(serialize(value));
                }
                count++;
            }

            if (count < hashMap.size()) {
                jsonArray.append(",");
            }
        }

        jsonArray.append("}");
        return jsonArray.toString();
    }

    private static String serializeCollection(Collection<?> collection) {
        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (Object item : collection) {
            json.append(serialize(item));
            if (i < collection.size() - 1) {
                json.append(", ");
            }
            i++;
        }
        json.append("]");
        return json.toString();
    }
}

