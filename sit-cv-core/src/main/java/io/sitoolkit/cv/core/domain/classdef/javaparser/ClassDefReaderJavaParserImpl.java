package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.AccessSpecifier;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.nodeTypes.NodeWithAnnotations;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.javadoc.Javadoc;
import com.github.javaparser.javadoc.JavadocBlockTag;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedReferenceType;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.javaparsermodel.declarations.JavaParserMethodDeclaration;

import io.sitoolkit.cv.core.domain.classdef.ApiDocDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassType;
import io.sitoolkit.cv.core.domain.classdef.FieldDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ClassDefReaderJavaParserImpl implements ClassDefReader {
    private JavaParserFacade jpf;

    private StatementVisitor statementVisitor;

    @NonNull
    private ClassDefRepository reposiotry;

    @NonNull
    private ProjectManager projectManager;

    @NonNull
    private SitCvConfig config;

    @Override
    public ClassDefReader readDir() {

        if (jpf == null) {
            throw new IllegalStateException("Reader has not been initialized yet");
        }

        Pattern p = Pattern.compile(config.getJavaFilePattern());

        projectManager.getCurrentProject().getSrcDirs().stream().forEach(srcDir -> {
            try {
                List<Path> files = Files.walk(srcDir)
                        .filter(file -> p.matcher(file.toFile().getName()).matches())
                        .collect(Collectors.toList());

                files.stream().forEach(javaFile -> {
                    readSourceFile(javaFile).ifPresent(classDef -> reposiotry.save(classDef));

                    int readCount = reposiotry.countClassDefs();
                    if (readCount % 10 == 0) {
                        log.info("Processed java files : {} / {} ", readCount, files.size());
                    }
                });

                // JavaParserFacade seems to be NOT thread-safe
                // files.stream().parallel().forEach(javaFile -> {
                // readJava(javaFile).ifPresent(classDef ->
                // dictionary.add(classDef));
                // });
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });

        reposiotry.solveReferences();

        return this;
    }

    @Override
    public Optional<ClassDef> readJava(Path javaFile) {
        return readSourceFile(projectManager.getCurrentProject().getSrcFile(javaFile));
    }

    public Optional<ClassDef> readSourceFile(Path javaFile) {
        log.debug("Read java : {}", javaFile);

        try {
            CompilationUnit compilationUnit = JavaParser.parse(javaFile);
            ClassDef classDef = new ClassDef();
            classDef.setSourceId(javaFile.toFile().getAbsolutePath());
            String typeName = compilationUnit.getPrimaryTypeName().get();
            classDef.setName(typeName);
            classDef.setPkg(compilationUnit.getPackageDeclaration().get().getNameAsString());

            compilationUnit.getClassByName(typeName).ifPresent(clazz -> {
                classDef.setType(ClassType.CLASS);
                classDef.setMethods(readMethodDefs(clazz));
                classDef.setFields(readFieldDefs(clazz));
                classDef.setImplInterfaces(readInterfaces(clazz));
                classDef.setAnnotations(readAnnotations(clazz));
            });

            compilationUnit.getInterfaceByName(typeName).ifPresent(interfaze -> {
                classDef.setType(ClassType.INTERFACE);
                classDef.setMethods(readMethodDefs(interfaze));
                classDef.setFields(readFieldDefs(interfaze));
                classDef.setAnnotations(readAnnotations(interfaze));
            });

            compilationUnit.getEnumByName(typeName).ifPresent(enumz -> {
                classDef.setType(ClassType.ENUM);
            });

            classDef.getMethods().stream().forEach(method -> method.setClassDef(classDef));

            log.debug("Read class : {}", classDef);

            return Optional.of(classDef);
        } catch (IOException e) {
            log.warn("IOException", e);
            return Optional.empty();
        }
    }

    private Set<String> readInterfaces(ClassOrInterfaceDeclaration typeDec) {
        Set<String> interfaces = new HashSet<>();

        try {
            interfaces = jpf.getTypeDeclaration(typeDec).getAllAncestors().stream()
                    .map(ResolvedReferenceType::getTypeDeclaration)
                    .filter(ResolvedReferenceTypeDeclaration::isInterface)
                    .map(ResolvedReferenceTypeDeclaration::getQualifiedName)
                    .collect(Collectors.toSet());

            log.debug("{} implements interfaces: {}", typeDec.getNameAsString(), interfaces);

        } catch (Exception e) {
            log.debug("Unsolved : Ancestors of '{}', {}", typeDec.getName(), e);

        }

        return interfaces;
    }

    Set<String> readAnnotations(ClassOrInterfaceDeclaration typeDec) {

        Set<String> annotations = typeDec.getAnnotations().stream()
                .map(AnnotationExpr::getNameAsString).collect(Collectors.toSet());
        log.debug("{} has annotations : {}", typeDec.getNameAsString(), annotations);
        return annotations;
    }

    List<MethodDef> readMethodDefs(ClassOrInterfaceDeclaration typeDec) {

        List<MethodDef> methodDefs = new ArrayList<>();

        String classActionPath = getActionPath(typeDec);

        jpf.getTypeDeclaration(typeDec).getDeclaredMethods()
                .forEach((ResolvedMethodDeclaration declaredMethod) -> {
                    JavaParserMethodDeclaration jpDeclaredMethod = (JavaParserMethodDeclaration) declaredMethod;

                    try {
                        MethodDef methodDef = new MethodDef();

                        methodDef.setPublic(
                                declaredMethod.accessSpecifier() == AccessSpecifier.PUBLIC);
                        methodDef.setName(declaredMethod.getName());
                        methodDef.setSignature(declaredMethod.getSignature());
                        methodDef.setQualifiedSignature(declaredMethod.getQualifiedSignature());
                        methodDef.setReturnType(
                                TypeParser.getTypeDef(declaredMethod.getReturnType()));
                        methodDef.setParamTypes(TypeParser.getParamTypes(declaredMethod));
                        methodDef.setApiDoc(readApiDocDef(jpDeclaredMethod));
                        methodDefs.add(methodDef);

                        if (!typeDec.isInterface()) {
                            typeDec.getMethods().stream().forEach(method -> {
                                if (equalMethods(declaredMethod, method)) {
                                    method.accept(statementVisitor, methodDef.getStatements());
                                    methodDef
                                            .setActionPath(classActionPath + getActionPath(method));
                                }
                            });
                        }

                        log.debug("Add method declaration : {}", methodDef);

                    } catch (Exception e) {
                        log.debug("Unsolved: '{}()' in '{}', {}", declaredMethod.getName(),
                                typeDec.getName(), e);
                    }

                });

        return methodDefs;
    }

    boolean equalMethods(ResolvedMethodDeclaration m1, MethodDeclaration m2) {
        if (!m1.getName().equals(m2.getNameAsString())) {
            return false;
        }

        if (m1.getNumberOfParams() != m2.getParameters().size()) {
            return false;
        }

        for (int i = 0; i < m1.getNumberOfParams(); i++) {
            ResolvedParameterDeclaration p1 = m1.getParam(i);
            Parameter p2 = m2.getParameter(i);

            if (!p1.getName().endsWith(p2.getNameAsString())) {
                return false;
            }
            if (!equalTypes(p1.getType(), p2.getType())) {
                return false;
            }
        }

        return true;
    }

    boolean equalTypes(ResolvedType t1, Type t2) {
        String str1 = t1.describe();
        String str2 = t2.asString();
        boolean result = StringUtils.equals(removePackageName(str1), removePackageName(str2));
        log.debug("t1.describe={}, t2.asString={}, t1.equals(t2)={}", str1, str2, result);
        return result;
    }

    String removePackageName(String typeString) {
        return typeString.replaceAll("[^.,<>]+\\.", "");
    }

    private static final String[] ACTION_ANNOTATION_NAMES = new String[] { "RequestMapping",
            "PostMapping", "GetMapping" };

    String getActionPath(NodeWithAnnotations<?> nwa) {

        for (String annotationName : ACTION_ANNOTATION_NAMES) {
            Optional<AnnotationExpr> annotation = nwa.getAnnotationByName(annotationName);
            if (annotation.isPresent()) {
                return retrive(annotation.get());
            }
        }

        return "";
    }

    String retrive(AnnotationExpr annotation) {

        StringBuilder actionPath = new StringBuilder();

        annotation.toNormalAnnotationExpr().ifPresent(nae -> {
            nae.getPairs().forEach(mvp -> {
                if (StringUtils.equals(mvp.getNameAsString(), "path")) {
                    actionPath.append(adjust(mvp.getValue().toString()));
                }
            });
        });

        annotation.toSingleMemberAnnotationExpr().ifPresent(smae -> {
            actionPath.append(adjust(smae.getMemberValue().toString()));
        });
        return actionPath.toString();
    }

    String adjust(String path) {
        if (StringUtils.isEmpty(path)) {
            return "";
        }
        String adjust = StringUtils.strip(path, "\"");
        if (adjust.startsWith("/")) {
            return adjust;
        } else {
            return "/" + adjust;
        }
    }

    List<FieldDef> readFieldDefs(ClassOrInterfaceDeclaration typeDec) {
        return jpf.getTypeDeclaration(typeDec).getDeclaredFields().stream().map(declaredField -> {

            try {
                FieldDef fieldDef = new FieldDef();
                fieldDef.setName(declaredField.getName());

                ResolvedType type = declaredField.getType();
                fieldDef.setType(TypeParser.getTypeDef(type));
                return fieldDef;

            } catch (Exception e) {
                log.debug("Unsolved : '{}' in '{}', {}", declaredField.getName(), typeDec.getName(),
                        e);
                return null;
            }

        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    ApiDocDef readApiDocDef(JavaParserMethodDeclaration declaredMethod) {
        String qualifiedClassName = declaredMethod.getPackageName() + "."
                + declaredMethod.getClassName();

        Optional<Javadoc> javadoc = declaredMethod.getWrappedNode().getJavadoc();
        List<String> contents = new ArrayList<>();
        if (javadoc.isPresent()) {
            contents.add(javadoc.get().getDescription().toText());
            List<String> tagContents = javadoc.get().getBlockTags().stream()
                    .map(JavadocBlockTag::toText).collect(Collectors.toList());
            contents.addAll(tagContents);
        } else {
            contents.add("No API Document.");
        }

        return ApiDocDef.builder().qualifiedClassName(qualifiedClassName)
                .annotations(declaredMethod.getWrappedNode().getAnnotations().stream()
                        .map(AnnotationExpr::toString).collect(Collectors.toList()))
                .methodDeclaration(declaredMethod.getWrappedNode().getDeclarationAsString())
                .contents(contents).build();
    }

    @Override
    public ClassDefReader init() {
        Project project = projectManager.getCurrentProject();

        project.refresh();
        jpf = JavaParserFacadeBuilder.build(project);
        statementVisitor = StatementVisitor.build(jpf);

        return this;
    }
}
