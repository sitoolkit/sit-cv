package io.sitoolkit.cv.core.domain.crud;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrudFindResult {
	// key:table
    private Map<String, Set<CrudType>> map = new HashMap<>();
    private String errMsg;

    public void put(String table, CrudType crud) {
        Set<CrudType> cruds = map.computeIfAbsent(table, key -> new HashSet<>());
        cruds.add(crud);
    }

    public Set<String> getCrud(String table) {
        return map.getOrDefault(table, new HashSet<>()).stream().map(crud -> crud.toString())
                .collect(Collectors.toSet());
    }

    public boolean isError() {
    	return StringUtils.isNotEmpty(errMsg);
    }
}