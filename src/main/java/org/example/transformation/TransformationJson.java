package org.example.transformation;


import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class TransformationJson {

    public static Object deserializeObject(String element, Class<?> componentType) {
        if (!element.startsWith("{") || !element.endsWith("}")) {
            throw new IllegalArgumentException("Invalid JSON object format");
        }

        Map<String, Object> map = new HashMap<>();
        String innerJson = element.substring(2, element.length() - 1);
        String replaceAll = innerJson.replaceAll("\\n+", "");
        String[] keyValuePairs = replaceAll.split(",");// разделяем строку через запятую

        for (String keyValuePair : keyValuePairs) {
            // проходим по полученному массиву
            String[] keyValue = keyValuePair.substring(0, keyValuePair.length()).split(":", 2);// разделя каждую на 2 части
            String key = keyValue[0].trim(); // обрезаем пробелы
            String value = keyValue[1].trim();

            if (value.equals("null")) { // сравниваем с нулем
                map.put(key, null);
            } else if (value.startsWith("\"") && value.endsWith("\"")) { // если начинается и оканчивается кавычками
                map.put(key, value.substring(1, value.length() - 1));//обрезаем кавычки
            } else if (value.startsWith("[") && value.endsWith("]")) {// если квадратные скобки, то массив
                map.put(key, deserializeArray(value, Object.class)); // применяем метод для сериализации массива
            } else if (value.startsWith("{") && value.endsWith("\"")) {
                String concat = value.concat("}");
                String s ="org.example.entity." +key.replaceAll("\"", "").substring(0, 1).toUpperCase() + key.substring(2).replaceAll("\"", "");
                System.out.println( s);
                Class<?> clazz = null;
                try {
                    clazz = Class.forName(s);

                } catch (ClassNotFoundException e) {
                    // Обработка случая, когда класс не найден
                    e.printStackTrace();
                }
//                Class<?> clazz = ;
                System.out.println(clazz.getName());
                map.put(key, deserializeObject(concat, clazz));
            } else if (value.startsWith("{") && value.endsWith("}")) { // если фигурные, то это объект
                map.put(key, deserializeObject(value, Object.class));
            }
//            else if (value.startsWith(":{") && value.endsWith("}")) {
//                map.put(key, deserializeMap(value, Object.class));
//            }
            else {
                map.put(key, parsePrimitiveValue(value));
            }
        }

        if (componentType.isAssignableFrom(Map.class)) {
            return map;
        } else {
            try {
                Object object = componentType.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String fieldName = entry.getKey().replaceAll("\"", "");
                    Object fieldValue = entry.getValue();
                    System.out.println(fieldName);
                    Field field = componentType.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    field.set(object, fieldValue);
                }
                return object;
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize object: " + e.getMessage());
            }
        }
    }

//
//    public static <T> T deserialize(String json, Class<T> clazz) {
//        Map<String, Object> jsonMap = parseJson(json);
//
//        T object = null;
//        try {
//            object = clazz.getDeclaredConstructor().newInstance();
//            Field[] fields = clazz.getDeclaredFields();
//            for (Field field : fields) {
//                field.setAccessible(true);
//                String fieldName = field.getName();
//                if (jsonMap.containsKey(fieldName)) {
//                    Object fieldValue = jsonMap.get(fieldName);
//                    if (fieldValue != null) {
//                        if (field.getType().isPrimitive()) {
//                            setPrimitiveFieldValue(object, field, fieldValue);
//                        } else if (field.getType().equals(String.class)) {
//                            field.set(object, fieldValue.toString());
//                        } else if (field.getType().isArray()) {
//                            Object array = deserializeArray(fieldValue.toString(), field.getType().getComponentType());
//                            field.set(object, array);
//                        } else {
//                            Object nestedObject = deserialize(fieldValue.toString(), field.getType());
//                            field.set(object, nestedObject);
//                        }
//                    }
//                }
//            }
//        } catch (ReflectiveOperationException e) {
//            e.printStackTrace();
//        }
//
//        return object;
//    }


    //    private static Map<Wagon, Cargo> parseJson(String json) {
//        Map<Wagon, Cargo> map = new HashMap<>();
//
//        try {
//        JSONObject jsonObject = new JSONObject(json);
//            Iterator<String> keys = jsonObject.keys();
//
//            while (keys.hasNext()) {
//                String key = keys.next();
//            JSONObject cargoJson = jsonObject.getJSONObject(key);
//                Wagon wagon = new Wagon(key);
//                Cargo cargo = new Cargo(
//                        UUID.fromString(cargoJson.getString("uuid")),
//                        cargoJson.getString("invoiceNumber"),
//                        cargoJson.getString("cargoName"),
//                        cargoJson.getDouble("weight"),
//                        new BigDecimal(cargoJson.getString("transportationCost"))
//                );
//                map.put(wagon, cargo);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return map;
//    }
//
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
            } else if (componentType == String.class) {
                Array.set(array, i, parseStringValue(element));
            } else if (Number.class.isAssignableFrom(componentType)) {
                Array.set(array, i, parseNumberValue(element, componentType));
            } else if (componentType == Boolean.class) {
                Array.set(array, i, parseBooleanValue(element));
            } else {
                Array.set(array, i, deserializeObject(element, componentType));
            }
        }

        return array;
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


    private static Object parsePrimitiveValue(String value) {
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

    private static Class<?> transformationToClassName(String nameClass) {
        Class<?> className = null;
        try {
            className = Class.forName(nameClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (className != null) {
            System.out.println(className); // Выводит "class Cargo"
        } else {
            System.out.println("Class not found");
        }
        return className;
    }
}
