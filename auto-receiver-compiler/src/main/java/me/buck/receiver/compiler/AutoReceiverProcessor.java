package me.buck.receiver.compiler;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import me.buck.receiver.annotation.GlobalAction;
import me.buck.receiver.annotation.LocalAction;


@AutoService(Processor.class)
public class AutoReceiverProcessor extends AbstractProcessor {

    private final ClassName BROADCAST_RECEIVER = ClassName.get("android.content", "BroadcastReceiver");
    private final ClassName CONTEXT            = ClassName.get("android.content", "Context");
    private final ClassName INTENT             = ClassName.get("android.content", "Intent");

    private Messager    messager;
    private Elements    elementUtils;
    private Set<String> messages = new HashSet<>();

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
        log(String.format("size: localElements = %s , globalElements = %s", localElements.size(), globalElements.size()));

        List<ActionItem> actionItems = new ArrayList<>();
        for (Element element : localElements) {
            if (!checkAnnotationValid(element, LocalAction.class)) continue;

            LocalAction action = element.getAnnotation(LocalAction.class);
            ExecutableElement executableElement = (ExecutableElement) element;
            TypeElement typeElement = (TypeElement) element.getEnclosingElement();
            String actionName = action.value();
            String methodName = executableElement.getSimpleName().toString();
            String clazzSimpleName = typeElement.getSimpleName().toString();
            String clazzFullName = typeElement.getQualifiedName().toString();
            String pkgName = elementUtils.getPackageOf(typeElement).toString();

            ActionItem actionItem = new ActionItem(actionName, methodName, clazzSimpleName, clazzFullName, pkgName);
            actionItems.add(actionItem);
        }

        if (!actionItems.isEmpty()) {

            ActionItem item = actionItems.get(0);

            CodeBlock.Builder code = CodeBlock.builder();
            code.addStatement("String action = intent.getAction()");
            code.beginControlFlow("switch(action)");
            for (ActionItem actionItem : actionItems) {
                String action = actionItem.actionName;
                String method = actionItem.methodName;
                String activity = "activity";
                code.addStatement("case $S: $L.$L(intent); break", action, activity, method);
            }
            code.endControlFlow();



            MethodSpec onReceive = MethodSpec.methodBuilder("onReceive")
                    .addAnnotation(Override.class)
                    .returns(void.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(CONTEXT, "context")
                    .addParameter(INTENT, "intent")
                    .addCode(code.build())
                    .build();

            TypeSpec receiver = TypeSpec.classBuilder(item.clazzSimpleName + "_LocalReceiver")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                    .superclass(BROADCAST_RECEIVER)
                    .addField(ClassName.bestGuess(item.clazzFullName),"activity")
                    .addMethod(onReceive)
                    .build();


            JavaFile javaFile = JavaFile.builder(actionItems.get(0).pkgName, receiver).
                    build();

            Filer filer = processingEnv.getFiler();
            try {
                javaFile.writeTo(filer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return false;
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
