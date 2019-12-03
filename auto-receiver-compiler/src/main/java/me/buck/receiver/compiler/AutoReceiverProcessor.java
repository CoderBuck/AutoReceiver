package me.buck.receiver.compiler;

import com.google.auto.service.AutoService;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import me.buck.receiver.annotation.GlobalAction;
import me.buck.receiver.annotation.LocalAction;


@AutoService(Processor.class)
public class AutoReceiverProcessor extends AbstractProcessor {

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
        Set<? extends Element> elements1 = roundEnv.getElementsAnnotatedWith(LocalAction.class);
        Set<? extends Element> elements2 = roundEnv.getElementsAnnotatedWith(GlobalAction.class);
        return false;
    }

    private void log(String msg) {
        messager.printMessage(Diagnostic.Kind.NOTE, msg);
    }


}
