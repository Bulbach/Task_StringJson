package org.example.transformation;


import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@UtilityClass
public class TransformationJson {

    public static void setPrimitiveFieldValue(Object object, Field field, Object fieldValue) throws IllegalAccessException {
        Class<?> fieldType = field.getType();
        if (fieldType.equals(int.class)) {
            field.setInt(object, Integer.parseInt(fieldValue.toString()));
        } else if (fieldType.equals(short.class)) {
            field.setShort(object, Short.parseShort(fieldValue.toString()));
        } else if (fieldType.equals(long.class)) {
            field.setLong(object, Long.parseLong(fieldValue.toString()));
        } else if (fieldType.equals(float.class)) {
            field.setFloat(object, Float.parseFloat(fieldValue.toString()));
        } else if (fieldType.equals(double.class)) {
            field.setDouble(object, Double.parseDouble(fieldValue.toString()));
        } else if (fieldType.equals(boolean.class)) {
            field.setBoolean(object, Boolean.parseBoolean(fieldValue.toString()));
        } else if (fieldType.equals(byte.class)) {
            field.setByte(object, Byte.parseByte(fieldValue.toString()));
        } else if (fieldType.equals(char.class)) {
            field.setChar(object, fieldValue.toString().charAt(0));
        }
    }

    public static Object deserializeArray(String jsonArray, Class<?> componentType) {
        if (!jsonArray.startsWith("[") || !jsonArray.endsWith("]")) {
            throw new IllegalArgumentException("Invalid JSON array format");
        }

        String[] elements = jsonArray.substring(1, jsonArray.length() - 1).split(",");
        Object array = Array.newInstance(componentType, elements.length);

        for (int i = 0; i < elements.length; i++) {
            String element = elements[i].trim();

            if (element.equals("null")) {
                Array.set(array, i, null);
            } else if (componentType.isPrimitive()) {
                Array.set(array, i, parsePrimitiveValue(element, componentType));
            } else if (componentType.isAssignableFrom(String.class)) {
                Array.set(array, i, parseStringValue(element));
            } else if (componentType.isAssignableFrom(Number.class)) {
                Array.set(array, i, parseNumberValue(element, componentType));
            } else if (componentType.isAssignableFrom(Boolean.class)) {
                Array.set(array, i, parseBooleanValue(element));
            } else if (element.startsWith("{") && element.endsWith("}")) {
                Transform transform = new Transform(element);
                Array.set(array, i, transform.deserialize(componentType));
            }
        }

        return array;
    }

    public static Collection<?> deserializeCollection(String jsonArray, Class<?> componentType) {
        if (!jsonArray.startsWith("[") || !jsonArray.endsWith("]")) {
            throw new IllegalArgumentException("Invalid JSON array format");
        }

        String[] elements = jsonArray.substring(1, jsonArray.length() - 1).split(",");
        Collection<Object> collection = new ArrayList<>();

        for (String element : elements) {
            String trimmedElement = element.trim();

            if (trimmedElement.equals("null")) {
                collection.add(null);
            } else if (componentType.isPrimitive()) {
                collection.add(parsePrimitiveValue(trimmedElement, componentType));
            } else if (componentType.isAssignableFrom(String.class)) {
                collection.add(parseStringValue(trimmedElement));
            } else if (componentType.isAssignableFrom(Number.class)) {
                collection.add(parseNumberValue(trimmedElement, componentType));
            } else if (componentType.isAssignableFrom(Boolean.class)) {
                collection.add(parseBooleanValue(trimmedElement));
            } else if (trimmedElement.startsWith("{") && trimmedElement.endsWith("}")) {
                Transform transform = new Transform(trimmedElement);
                collection.add(transform.deserialize(componentType));
            }
        }

        return collection;
    }

    public static Map<String, Object> deserializeMap(String jsonObject, Class<?> componentType) {
        if (!jsonObject.startsWith("{") || !jsonObject.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON object format");
        }

        Map<String, Object> map = new HashMap<>();
        String innerJson = jsonObject.substring(1, jsonObject.length() - 1);
        String[] keyValuePairs = innerJson.split(",");

        for (String keyValuePair : keyValuePairs) {
            String[] keyValue = keyValuePair.split(":", 2);
            String key = keyValue[0].trim();
            String value = keyValue[1].trim();

            if (value.equals("null")) {
                map.put(key, null);
            } else if (value.startsWith("\"") && value.endsWith("\"")) {
                map.put(key, value.substring(1, value.length() - 1));
            } else if (value.startsWith("[") && value.endsWith("]")) {
                map.put(key, deserializeArray(value, componentType));
            } else if (value.startsWith("{") && value.endsWith("}")) {
                map.put(key, deserializeMap(value, componentType));
            } else {
                map.put(key, parsePrimitiveValue(value));
            }
        }

        return map;
    }

    private static Object parsePrimitiveValue(String element, Class<?> componentType) {
        if (componentType == boolean.class) {
            return Boolean.parseBoolean(element);
        } else if (componentType == byte.class) {
            return Byte.parseByte(element);
        } else if (componentType == short.class) {
            return Short.parseShort(element);
        } else if (componentType == int.class) {
            return Integer.parseInt(element);
        } else if (componentType == long.class) {
            return Long.parseLong(element);
        } else if (componentType == float.class) {
            return Float.parseFloat(element);
        } else if (componentType == double.class) {
            return Double.parseDouble(element);
        } else if (componentType == char.class) {
            if (element.length() != 1) {
                throw new IllegalArgumentException("Invalid char value: " + element);
            }
            return element.charAt(0);
        } else {
            throw new IllegalArgumentException("Unsupported primitive type: " + componentType.getName());
        }
    }

    private static String parseStringValue(String element) {
        if (element.length() < 2 || !element.startsWith("\"") || !element.endsWith("\"")) {
            throw new IllegalArgumentException("Invalid string value: " + element);
        }
        return element.substring(1, element.length() - 1);
    }

    private static Number parseNumberValue(String element, Class<?> componentType) {
        if (componentType == Byte.class) {
            return Byte.parseByte(element);
        } else if (componentType == Short.class) {
            return Short.parseShort(element);
        } else if (componentType == Integer.class) {
            return Integer.parseInt(element);
        } else if (componentType == Long.class) {
            return Long.parseLong(element);
        } else if (componentType == Float.class) {
            return Float.parseFloat(element);
        } else if (componentType == Double.class) {
            return Double.parseDouble(element);
        } else {
            throw new IllegalArgumentException("Unsupported number type: " + componentType.getName());
        }
    }

    private static Boolean parseBooleanValue(String element) {
        if (element.equalsIgnoreCase("true")) {
            return true;
        } else if (element.equalsIgnoreCase("false")) {
            return false;
        } else {
            throw new IllegalArgumentException("Invalid boolean value: " + element);
        }
    }


    public static Object parsePrimitiveValue(String value) {
        if (value.equalsIgnoreCase("true")) {
            return true;
        } else if (value.equalsIgnoreCase("false")) {
            return false;
        } else if (value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        } else if (value.contains(".") && value.toLowerCase().contains("e")) {
            return Double.parseDouble(value);
        } else if (value.matches("-?\\d+")) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ignored) {
                try {
                    return Long.parseLong(value);
                } catch (NumberFormatException ignored2) {
                    return new BigInteger(value);
                }
            }
        } else {
            return value;
        }
    }

    public static Date parseDate(String dateString) {
        try {
            String replaced = dateString.replaceAll("\"", "");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(replaced);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static LocalDate parseLocalDate(String dateString) {
        String replaced = dateString.replaceAll("\"", "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(replaced, formatter);
    }

    public static ZonedDateTime parseZonedDateTime(String dateString) {
        String replaced = dateString.replaceAll("\"", "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return ZonedDateTime.parse(replaced, formatter);
    }

    public static LocalDateTime parseLocalDateTime(String dateString) {
        String replaced = dateString.replaceAll("\"", "");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return LocalDateTime.parse(replaced, formatter);
    }

}
