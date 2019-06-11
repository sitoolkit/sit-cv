package io.sitoolkit.cv.core.infra.config;

import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StartEndPattern {

    private String start;
    private String end;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Pattern startPattern;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private Pattern endPattern;
    
}
