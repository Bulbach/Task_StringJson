package org.example.transformation;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

//        System.out.println(string);

        for (int i = 0; i < string.length(); i++) {
            level = getLevel(i);

            if (isEnd(i)) {
                end = i;
            }
            if (end > start) {
                String pair = string.substring(start + 1, end);
                String key = pair.substring(0, pair.indexOf(':')).trim().replaceAll("\"", "");
                String value = pair.substring(pair.indexOf(':') + 1).trim();

                if (value.equals("null")) { // сравниваем с нулем
                    map.put(key, null);
                }else if (value.startsWith("\"") && value.endsWith("\"")) { // если начинается и оканчивается кавычками
                    map.put(key, value.substring(1, value.length() - 1));//обрезаем кавычки
                }else if (value.startsWith("[") && value.endsWith("]")) {// если квадратные скобки, то массив
                    map.put(key,TransformationJson.deserializeArray(value, Object.class)); // применяем метод для сериализации массива
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
    public <T> T parse(Class<T> clazz) {
        T object = (T) Optional.empty();
        int start = 0;
        int end = 0;
        Map<String, Object> map = parse();
        System.out.println(string);

        for (int i = 0; i < string.length(); i++) {
            level = getLevel(i);

            if (isEnd(i)) {
                end = i;
            }
            if (end > start) {
                String pair = string.substring(start + 1, end);
                String key = pair.substring(0, pair.indexOf(':')).trim().replaceAll("\"", "");
                String value = pair.substring(pair.indexOf(':') + 1).trim();

                if (value.equals("null")) { // сравниваем с нулем
                    map.put(key, null);
                }else if (value.startsWith("\"") && value.endsWith("\"")) { // если начинается и оканчивается кавычками
                    map.put(key, value.substring(1, value.length() - 1));//обрезаем кавычки
                }else if (value.startsWith("[") && value.endsWith("]")) {// если квадратные скобки, то массив
                    map.put(key,TransformationJson.deserializeArray(value, Object.class)); // применяем метод для сериализации массива
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

//        try {
//            object = clazz.getDeclaredConstructor().newInstance();
//            Map<String, Object> parsedMap = parse();
//            Field[] fields = clazz.getDeclaredFields();
//
//            for (Field field : fields) {
//                field.setAccessible(true);
//                String fieldName = field.getName();
//                Object fieldValue = parsedMap.get(fieldName);
//
//                if (fieldValue != null) {
//                    fieldValue= deserialize(fieldValue.getClass());
//                    field.set(object, fieldValue);
//                }
//            }
//       }
        try {
                object = clazz.getDeclaredConstructor().newInstance();
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    String fieldName = entry.getKey().replaceAll("\"", "");
                    Object fieldValue = entry.getValue();
                    System.out.println(fieldName);
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    fieldValue= deserialize(fieldValue.getClass());
                    field.set(object, fieldValue);
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Failed to deserialize object: " + e.getMessage());
            }

         return object;
    }

//    public Object deserializeObject(String element, Class<?> componentType) {
//        if (!element.startsWith("{") || !element.endsWith("}")) {
//            throw new IllegalArgumentException("Invalid JSON object format");
//        }
//
//        Map<String, Object> map = parse();;
//        String innerJson = element.substring(2, element.length() - 1);
//        String replaceAll = innerJson.replaceAll("\\n+", "");
//        String[] keyValuePairs = replaceAll.split(",");// разделяем строку через запятую
//
//        for (Map.Entry<String, Object> entry : map.entrySet()) {
//            // проходим по полученному массиву
//            String key = entry.getKey(); // обрезаем пробелы
//            Object value = entry.getValue();
//
//            if (value.equals("null")) { // сравниваем с нулем
//                map.put(key, null);
//            } else if (value.startsWith("\"") && value.endsWith("\"")) { // если начинается и оканчивается кавычками
//                map.put(key, value.substring(1, value.length() - 1));//обрезаем кавычки
//            } else if (value.startsWith("[") && value.endsWith("]")) {// если квадратные скобки, то массив
//                map.put(key, deserializeArray(value, Object.class)); // применяем метод для сериализации массива
//            } else if (value.startsWith("{") && value.endsWith("\"")) {
//                String concat = value.concat("}");
//                String s ="org.example.entity." +key.replaceAll("\"", "").substring(0, 1).toUpperCase() + key.substring(2).replaceAll("\"", "");
//                System.out.println( s);
//                Class<?> clazz = null;
//                try {
//                    clazz = Class.forName(s);
//
//                } catch (ClassNotFoundException e) {
//                    // Обработка случая, когда класс не найден
//                    e.printStackTrace();
//                }
////                Class<?> clazz = ;
//                System.out.println(clazz.getName());
//                map.put(key, deserializeObject(concat, clazz));
//            } else if (value.startsWith("{") && value.endsWith("}")) { // если фигурные, то это объект
//                map.put(key, deserializeObject(value, Object.class));
//            }
////            else if (value.startsWith(":{") && value.endsWith("}")) {
////                map.put(key, deserializeMap(value, Object.class));
////            }
//            else {
//                map.put(key, parsePrimitiveValue(value));
//            }
//        }
//
//        if (componentType.isAssignableFrom(Map.class)) {
//            return map;
//        } else {
//            try {
//                Object object = componentType.getDeclaredConstructor().newInstance();
//                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    String fieldName = entry.getKey().replaceAll("\"", "");
//                    Object fieldValue = entry.getValue();
//                    System.out.println(fieldName);
//                    Field field = componentType.getDeclaredField(fieldName);
//                    field.setAccessible(true);
//                    field.set(object, fieldValue);
//                }
//                return object;
//            } catch (Exception e) {
//                throw new IllegalArgumentException("Failed to deserialize object: " + e.getMessage());
//            }
//        }
//    }

        public <T> T deserialize(Class<T> clazz) {
            Map<String, Object> map = parse();

        T object = null;
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
                        } else if (field.getType().isArray()) {
                            Object array = TransformationJson.deserializeArray(fieldValue.toString(), field.getType().getComponentType());
                            field.set(object, array);
                        } else if (fieldValue instanceof Map<?, ?>){
                            Map<?,?> deserializeMap = TransformationJson.deserializeMap(fieldValue.toString(),field.getType().getComponentType());
                            field.set(object, deserializeMap);
                        }
                        else {

                            Object nestedObject = deserialize(Class.forName(fieldName));
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


    private boolean isEnd(int i) {
        return (level == 1 && string.charAt(i) == ',') || (level == 0 && string.charAt(i) == '}');
    }


    private int getLevel(int i) {
        if (string.charAt(i) == '{') {
            level++;
        }
        if (string.charAt(i) == '}') {
            level--;
        }
        return level;
    }

}
