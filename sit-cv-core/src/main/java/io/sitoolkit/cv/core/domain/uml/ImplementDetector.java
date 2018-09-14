package io.sitoolkit.cv.core.domain.uml;

import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImplementDetector {

    public MethodDef detectImplMethod(MethodDef methodCall) {

        ClassDef classOrInterface = methodCall.getClassDef();
        if (classOrInterface != null) {
            ClassDef impleClass = detectImplClass(classOrInterface);
            Optional<MethodDef> methodImpl = findMethod(impleClass, methodCall.getSignature());
            if (methodImpl.isPresent()) {
                return methodImpl.get();
            }
        }
        return methodCall;
    }

    public ClassDef detectImplClass(ClassDef classOrInterface) {

        if (classOrInterface != null && classOrInterface.isInterface()) {
            ClassDef interfaze = classOrInterface;
            Set<ClassDef> knownImplClasses = interfaze.getKnownImplClasses();
            log.debug("Interface {} has KnownImplements : {}", interfaze.getName(), knownImplClasses);

            if (knownImplClasses.size() == 1) {
                ClassDef onlyImpl = knownImplClasses.iterator().next();
                log.debug("{}'s the only knoun impl found : {} ", interfaze.getName(), onlyImpl.getName());
                return onlyImpl;
            }
        }
        return classOrInterface;
    }

    Optional<MethodDef> findMethod(ClassDef classDef, String signature) {
        log.debug("Finding '{}' from '{}'", signature, classDef.getName());
        Optional<MethodDef> foundMethod = classDef.getMethods().stream()
                .filter(m -> StringUtils.equals(signature, m.getSignature()))
                .findFirst();

        if (foundMethod.isPresent()) {
            log.debug("Method found : {}", foundMethod.get().getQualifiedSignature());
        } else {
            log.debug("'{}' not found from '{}'", signature, classDef.getName());
        }
        return foundMethod;
    }
}
