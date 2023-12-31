package org.example.transformation;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Transform {

    private String string;
    private int level;

    public Transform(String string) {

        level = 0;
        this.string = Optional.ofNullable(string)
                .map(s -> s.replaceAll("\\n", ""))
                .orElse("{}");
    }


    public Map<String, Object> parse() {
        int start = 0;
        int end = 0;
        Map<String, Object> map = new HashMap<>();

        for (int i = 0; i < string.length(); i++) {
            level = getLevel(i);

            if (isEnd(i)) {
                end = i;
            }
            if (end > start) {
                String pair = string.substring(start + 1, end);
                String key;
                String value;
                if (pair.contains(":")) {
                    key = pair.substring(0, pair.indexOf(':')).trim().replaceAll("\"", "");
                    value = pair.substring(pair.indexOf(':') + 1).trim();
                } else {
                    value = pair.substring(pair.indexOf('=')).trim();
                    key = pair.substring(0, pair.indexOf('=')).trim().replaceAll("\"", "");
                }

                if (value.equals("null")) { // сравниваем с нулем
                    map.put(key, null);
                } else if (value.startsWith("\"") && value.endsWith("\"")) {
                    map.put(key, value.substring(1, value.length() - 1));
                } else if (value.startsWith("[") && value.endsWith("]")) {
                    map.put(key, TransformationJson.deserializeArray(value, Object.class));
                }
                if (value.startsWith("{") && value.endsWith("}")) {
                    Transform transform = new Transform(value);
                    Map<String, Object> objectMap = transform.parse();
                    map.put(key, objectMap);
                } else {
                    map.put(key, value);
                }
                start = end;
            }
        }
        return map;
    }

    public <T> T deserialize(Class<T> clazz) {

        Map<String, Object> map = new HashMap<>();
        T object = null;

        int start = 0;
        int end = 0;
        Pattern pattern = Pattern.compile("^[\\{\\[].*[\\}\\]]$");
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid JSON: " + string);
        }

        for (int i = 0; i < string.length(); i++) {
            level = getLevel(i);
            if (isEnd(i)) {
                end = i;
            }
            if (end > start) {
                String pair = string.substring(start + 1, end);
                String key;
                String value;
                if (pair.contains(":")) {
                    key = pair.substring(0, pair.indexOf(':')).trim().replaceAll("\"", "");
                    value = pair.substring(pair.indexOf(':') + 1).trim();
                } else {
                    key = pair.substring(0, pair.indexOf('=')).trim().replaceAll("\"", "");
                    value = pair.substring(pair.indexOf('=') + 1).trim();
                }
                if (value.equals("null")) {
                    map.put(key, null);
                } else if (value.startsWith("\"") && value.endsWith("\"")) {
                    map.put(key, value.substring(1, value.length() - 1));
                } else if (value.startsWith("[") && value.endsWith("]")) {
                    map.put(key, TransformationJson.deserializeArray(value, Object.class));
                }
                if (value.startsWith("{") && value.endsWith("}")) {
                    Transform transform = new Transform(value);
                    Map<String, Object> objectMap = transform.parse();
                    map.put(key, objectMap);
                } else {
                    map.put(key, value);
                }
                start = end;
            }
        }

        try {
            object = clazz.getDeclaredConstructor().newInstance();
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);

                String fieldName = field.getName();
                if (map.containsKey(fieldName)) {
                    Object fieldValue = map.get(fieldName);
                    if (fieldValue != null) {
                        if (field.getType().isPrimitive()) {
                            TransformationJson.setPrimitiveFieldValue(object, field, fieldValue);
                        } else if (field.getType().equals(String.class)) {
                            field.set(object, fieldValue.toString());
                        } else if (field.getType().equals(Date.class)) {
                            Date dateValue = TransformationJson.parseDate(fieldValue.toString());
                            field.set(object, dateValue);
                        } else if (field.getType().equals(LocalDate.class)) {
                            LocalDate localDateValue = TransformationJson.parseLocalDate(fieldValue.toString());
                            field.set(object, localDateValue);
                        } else if (field.getType().equals(ZonedDateTime.class)) {
                            ZonedDateTime zonedDateTimeValue = TransformationJson.parseZonedDateTime(fieldValue.toString());
                            field.set(object, zonedDateTimeValue);
                        } else if (field.getType().equals(LocalDateTime.class)) {
                            LocalDateTime localDateTimeValue = TransformationJson.parseLocalDateTime(fieldValue.toString());
                            field.set(object, localDateTimeValue);
                        } else if (field.getType().isArray()) {
                            Object array = TransformationJson.deserializeArray(fieldValue.toString(), field.getType());
                            field.set(object, array);
                        } else if (Map.class.isAssignableFrom(field.getType())) {
                            Map<?, ?> mapValue = (Map<?, ?>) fieldValue;
                            Map<Object, Object> convertedMap = new HashMap<>();

                            for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                                convertedMap.put(entry.getKey(), entry.getValue());
                                field.set(object, convertedMap);
                            }

                        } else if (Collection.class.isAssignableFrom(field.getType())) {
                            Class<?> componentType = getaParamerizeClass(field);
                            Collection<?> deserializeCollection = TransformationJson.deserializeCollection(fieldValue.toString(), componentType);
                            field.set(object, deserializeCollection);
                        } else {
                            string = fieldValue.toString();
                            Object nestedObject = deserialize(field.getType());
                            field.set(object, nestedObject);
                        }
                    }
                }
            }
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
        }

        return object;
    }

    private static Class<?> getaParamerizeClass(Field field) {
        Type genericType = field.getGenericType();
        Class<?> componentType = field.getClass();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) genericType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments.length > 0 && typeArguments[0] instanceof Class) {
                componentType = (Class<?>) typeArguments[0];
            }

        }
        return componentType;
    }


    private boolean isEnd(int i) {
        return (level == 1 && string.charAt(i) == ',') || (level == 0 && string.charAt(i) == '}');
    }

    private int getLevel(int i) {
        if ((string.charAt(i) == '{') || (string.charAt(i) == '[')) {
            level++;
        }
        if ((string.charAt(i) == '}') || (string.charAt(i) == ']')) {
            level--;
        }
        return level;
    }

}
