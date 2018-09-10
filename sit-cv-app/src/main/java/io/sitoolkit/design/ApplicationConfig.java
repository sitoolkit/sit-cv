package io.sitoolkit.design;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@PropertySource(ignoreResourceNotFound = true, value = { "classpath:application.properties" })
public class ApplicationConfig {

    @Autowired
    ApplicationArguments appArgs;

    @Value("${targetProject.path}")
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private String targetProjectPath;

    @PostConstruct
    public void init() {
        processAppArg(0, this::setTargetProjectPath);
    }

    private void processAppArg(int argIndex, Consumer<String> argSetter) {
        List<String> args = appArgs.getNonOptionArgs();
        if (args.size() > argIndex) {
            String arg = args.get(argIndex);
            argSetter.accept(arg);
            log.debug("appArg({}):{} set", argIndex, arg);

        } else {
            log.debug("aapArg({}) is not exist", argIndex);
        }
    }
}
