package org.example.transformation;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

public class TransformationString {

    public static String serialize(Object object) {
        if (object==null){
            throw new IllegalArgumentException("Object is null");
        }
        StringBuilder json = new StringBuilder("{");
            processingClass(object, json);
        return json.toString();
    }

    private static void processingClass(Object object, StringBuilder json) {
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
            processingField(fieldValue, field, json);

            if (i < fields.length - 1) {
                json.append(", \n ");
            }
        }

        json.append("}");
    }

    private static void processingField(Object fieldValue, Field field, StringBuilder json) {
        if (fieldValue != null) {
            if (field.getType().isPrimitive() || fieldValue instanceof Number || fieldValue instanceof Boolean) {
                json.append(fieldValue);
            } else if (field.getType().equals(String.class)) {
                json.append("\"").append(fieldValue).append("\"");
            }
            else if (fieldValue instanceof Collection<?>) {
                json.append(serializeCollection((Collection<?>) fieldValue));
            }
            else if (fieldValue instanceof Map<?, ?>) {
                json.append(serializeHashMap((Map<?, ?>) fieldValue));
            }
            else if (field.getType().isArray()) {
                json.append(serializeArray(fieldValue));
            } else if (field.getType().equals(LocalDateTime.class)) {
                json.append("\"").append(((LocalDateTime) fieldValue).toString()).append("\"");
            } else if (field.getType().equals(LocalDate.class)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String formattedDate = dateFormat.format(LocalDate.parse(fieldValue.toString()));
                json.append("\"").append(formattedDate).append("\"");
            } else if (field.getType().equals(ZonedDateTime.class)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                String formattedDate = dateFormat.format(((ZonedDateTime) fieldValue).toInstant());
                json.append("\"").append(formattedDate).append("\"");
            } else if (field.getType().equals(Date.class)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String formattedDate = dateFormat.format((Date) fieldValue);
                json.append("\"").append(formattedDate).append("\"");
            } else {
                json.append(serialize(fieldValue));
            }
        } else {
            json.append("null");
        }
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
        StringBuilder json = new StringBuilder("{");
        int count = 0;

        for (Map.Entry<?, ?> entry : hashMap.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();

            if (key != null && value != null) {
                if (key.getClass().equals(String.class)) {
                    json.append("\"").append(key).append("\"");
                } else {
                    json.append(serialize(key));
                }
                json.append(":");
                if (value.getClass().isPrimitive() || value instanceof Number || value instanceof Boolean) {
                    json.append(value);
                } else if (value.getClass().equals(String.class)) {
                    json.append("\"").append(value).append("\"");
                } else {
                    json.append(serialize(value));
                }
                count++;
            }

            if (count < hashMap.size()) {
                json.append(",");
            }
        }

        json.append("}");
        return json.toString();
    }

    private static String serializeCollection(Collection<?> collection) {
        StringBuilder json = new StringBuilder("[");
        int i = 0;
        for (Object item : collection) {
            if (item.getClass().isPrimitive() || item instanceof Number || item instanceof Boolean) {
                json.append(item);
            } else if (item.getClass().equals(String.class)) {
                json.append("\"").append(item).append("\"");
            }
            if (i < collection.size() - 1) {
                json.append(", ");
            }
            i++;
        }
        json.append("]");
        return json.toString();
    }
}

