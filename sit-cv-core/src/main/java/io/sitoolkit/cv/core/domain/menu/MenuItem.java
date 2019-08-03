package io.sitoolkit.cv.core.domain.menu;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {
    private String id;
    private String name;
    private String endpoint;
    @Default
    private SortedSet<MenuItem> children = new TreeSet<>(Comparator.comparing(MenuItem::getName));
}
