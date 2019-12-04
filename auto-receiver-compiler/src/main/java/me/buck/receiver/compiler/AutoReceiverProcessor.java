package me.buck.receiver.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import me.buck.receiver.annotation.GlobalAction;
import me.buck.receiver.annotation.LocalAction;


@AutoService(Processor.class)
public class AutoReceiverProcessor extends AbstractProcessor {

    private final ClassName CONTEXT            = ClassName.get("android.content", "Context");
    private final ClassName INTENT             = ClassName.get("android.content", "Intent");
    private final ClassName BROADCAST_RECEIVER = ClassName.get("android.content", "BroadcastReceiver");
    private final ClassName SIMPLE_RECEIVER    = ClassName.get("me.buck.receiver", "SimpleReceiver");

    private Messager    messager;
    private Elements    elementUtils;
    private Set<String> messages = new LinkedHashSet<>();

    private int count = 0;

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        set.add(LocalAction.class.getCanonicalName());
        set.add(GlobalAction.class.getCanonicalName());
        return set;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        count++;
        log("process " + count);
        Set<? extends Element> localElements = roundEnv.getElementsAnnotatedWith(LocalAction.class);
        Set<? extends Element> globalElements = roundEnv.getElementsAnnotatedWith(GlobalAction.class);

        List<ActionItem> localItems = getItems(localElements,true);
        List<ActionItem> globalItems = getItems(globalElements,false);

        Map<String, List<ActionItem>> localMap = localItems.stream().collect(Collectors.groupingBy(item -> item.clazzFullName));
        Map<String, List<ActionItem>> globalMap = globalItems.stream().collect(Collectors.groupingBy(item -> item.clazzFullName));

        writeFile(localMap, true);
        writeFile(globalMap, false);
        return false;
    }

    private List<ActionItem> getItems(Set<? extends Element> elements, boolean isLocal) {
        return elements.stream()
                .filter(element -> checkAnnotationValid(element, isLocal ? LocalAction.class : GlobalAction.class))
                .map(element -> {
                    ExecutableElement executableElement = (ExecutableElement) element;
                    TypeElement typeElement = (TypeElement) element.getEnclosingElement();
                    String actionName = isLocal ? element.getAnnotation(LocalAction.class).value() : element.getAnnotation(GlobalAction.class).value();
                    String methodName = executableElement.getSimpleName().toString();
                    String clazzSimpleName = typeElement.getSimpleName().toString();
                    String clazzFullName = typeElement.getQualifiedName().toString();
                    String pkgName = elementUtils.getPackageOf(typeElement).toString();
                    return new ActionItem(actionName, methodName, clazzSimpleName, clazzFullName, pkgName);
                }).collect(Collectors.toList());
    }

    private void writeFile(Map<String, List<ActionItem>> map, boolean isLocal) {
        String endFix = isLocal ? "_LocalReceiver" : "_GlobalReceiver";
        map.entrySet().forEach(entry -> {
            String clazzFullName = entry.getKey();
            List<ActionItem> items = entry.getValue();
            String clazzSimpleName = items.get(0).clazzSimpleName;
            String pkgName = items.get(0).pkgName;

            MethodSpec.Builder constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(CONTEXT, "context")
                    .addStatement("super(context)")
                    .addStatement("mIsLocal = $L", true);
            items.forEach(item ->
                    constructor.addStatement("mFilter.addAction($S)", item.actionName)
            );

            MethodSpec.Builder onReceive = MethodSpec.methodBuilder("onReceive")
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(CONTEXT, "context")
                    .addParameter(INTENT, "intent");

            onReceive.addStatement("String action = intent.getAction()");
            onReceive.beginControlFlow("switch(action)");
            items.forEach(item ->
                    onReceive.addStatement("case $S: $L.$L(intent); break", item.actionName, "activity", item.methodName)
            );
            onReceive.endControlFlow();


            TypeSpec receiver = TypeSpec.classBuilder(clazzSimpleName + endFix)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(SIMPLE_RECEIVER)
                    .addField(ClassName.bestGuess(clazzFullName), "activity")
                    .addMethod(constructor.build())
                    .addMethod(onReceive.build())
                    .build();

            JavaFile javaFile = JavaFile.builder(pkgName, receiver).
                    build();

            Filer filer = processingEnv.getFiler();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private boolean checkAnnotationValid(Element element, Class clazz) {
        if (element.getKind() != ElementKind.METHOD) {
            error(element, "%s must be declared on method.", clazz.getSimpleName());
            return false;
        }

        if (ClassValidator.isPrivate(element)) {
            error(element, "%s() must can not be private.", element.getSimpleName());
            return false;
        }

        return true;
    }

    private void log(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }


}
