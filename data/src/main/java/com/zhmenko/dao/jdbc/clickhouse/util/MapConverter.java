package com.zhmenko.dao.jdbc.clickhouse.util;

import com.clickhouse.client.internal.google.common.primitives.Ints;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapConverter {
    public static Map<String, List<Integer>> fromDbType(Object map){
        Map<String,Object> localMap = (Map<String, Object>) map;
        Map<String,List<Integer>> resultMap = new HashMap<>();
        for (String key : localMap.keySet()) {
            int[] ints = (int[]) localMap.get(key);
            resultMap.put(key, Arrays.stream(ints).boxed().collect(Collectors.toList()));
        }
        return resultMap;
    }
    public static String toDbType(Map<String, List<Integer>> map){
        StringBuilder stringBuilder = new StringBuilder();
        for (String key : map.keySet()) {
            stringBuilder
                    .append("'")
                    .append(key)
                    .append("'")
                    .append(", ")
                    .append(map.get(key).toString())
                    .append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length()-2);
    }
}
