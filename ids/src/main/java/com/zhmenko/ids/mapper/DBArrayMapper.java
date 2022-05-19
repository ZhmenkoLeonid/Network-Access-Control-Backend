package com.zhmenko.ids.mapper;

import org.springframework.stereotype.Component;

import java.sql.Array;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DBArrayMapper {
    public List<Integer> toIntegerList(Array intArray) throws SQLException {
        int[] arr = (int[]) intArray.getArray();
        return Arrays.stream(arr).boxed().collect(Collectors.toList());
    }

    public List<String> toStringList(Array stringArray) throws SQLException {
        String[] arr = (String[]) stringArray.getArray();
        return List.of(arr);
    }

    public String fromStringCollection(Collection<String> stringList) {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (String s : stringList) {
            builder.append("'")
                    .append(s)
                    .append("', ");
        }
        builder.replace(builder.length() - 2, builder.length(), "]");
        System.out.println("String List to arr" + builder.toString());
        return builder.toString();
    }
}
