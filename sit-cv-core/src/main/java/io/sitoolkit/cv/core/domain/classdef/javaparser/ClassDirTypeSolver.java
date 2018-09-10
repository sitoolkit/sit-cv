package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.symbolsolver.javassistmodel.JavassistFactory;
import com.github.javaparser.symbolsolver.model.resolution.SymbolReference;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import lombok.Getter;
import lombok.Setter;

public class ClassDirTypeSolver implements TypeSolver {

    private static ClassDirTypeSolver instance;

    @Getter
    @Setter
    private TypeSolver parent;
    /**
     * key:class name, value:path to class file
     */
    private Map<String, Path> classFileMap = new HashMap<>();
    private ClassPool classPool = new ClassPool(false);

    public static synchronized ClassDirTypeSolver get(Path classDir) {
        if (instance == null) {
            instance = new ClassDirTypeSolver();
        }
        instance.addClassDir(classDir);
        return instance;
    }

    private void addClassDir(Path classDir) {

        try {
            classPool.appendClassPath(classDir.toString());
            classPool.appendSystemPath();

            Files.walk(classDir).filter(path -> path.getFileName().toString().endsWith(".class"))
                    .forEach(path -> {
                        classFileMap.put(classFilePathToClassName(classDir, path), path);
                    });
        } catch (IOException | NotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private String classFilePathToClassName(Path classDir, Path classFilePath) {
        String classFilePathStr = classDir.relativize(classFilePath).toString();
        if (!classFilePathStr.endsWith(".class")) {
            throw new IllegalStateException();
        }
        String className = classFilePathStr.substring(0,
                classFilePathStr.length() - ".class".length());
        className = className.replace('/', '.');
        className = className.replace('$', '.');
        return className;
    }

    @Override
    public SymbolReference<ResolvedReferenceTypeDeclaration> tryToSolveType(String name) {
        try {
            Path classFilePath = classFileMap.get(name);
            if (classFilePath != null) {

                try (InputStream is = Files.newInputStream(classFilePath)) {
                    CtClass ctClass = classPool.makeClass(is);
                    return SymbolReference
                            .solved(JavassistFactory.toTypeDeclaration(ctClass, getRoot()));
                }

            } else {
                return SymbolReference.unsolved(ResolvedReferenceTypeDeclaration.class);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
