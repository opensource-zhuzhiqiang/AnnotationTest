package com.example.annotation_compiler;

import com.example.annotations.RobustFragment;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class RobustFragmentProcessor extends AbstractProcessor {
    private Filer mFiler;
    private ProcessingEnvironment mProcessingEnvironment;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
        mProcessingEnvironment = processingEnvironment;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(RobustFragment.class.getCanonicalName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> annotatedElements = roundEnvironment.getElementsAnnotatedWith(RobustFragment.class);
        for (TypeElement typeElement : set) {
            System.out.println(mProcessingEnvironment.getOptions());
            ClassName activity = ClassName.get("androidx.appcompat.app", "AppCompatActivity");
            ClassName fragment = ClassName.get("androidx.fragment.app", "Fragment");
            MethodSpec create = MethodSpec.methodBuilder("addInto")
                    .returns(TypeName.VOID)
                    .addModifiers(Modifier.STATIC, Modifier.PUBLIC)
                    .addParameter(activity, "activity")
                    .addParameter(TypeName.INT, "containerId")
                    .addParameter(String.class, "tag")
                    .beginControlFlow("if (activity == null) ")
                    .addStatement("throw new NullPointerException(\"the activity cannot be null!\")")
                    .endControlFlow()
                    .addStatement("\n$T fragment = (tag == null || tag.trim().isEmpty())\n" +
                            "                ? activity.getSupportFragmentManager().findFragmentById(containerId)\n" +
                            "                : activity.getSupportFragmentManager().findFragmentByTag(tag)", fragment)
                    .beginControlFlow("\nif (fragment == null) ")
                    .addStatement("fragment = new AboutMeFragment()")
                    .addStatement("activity.getSupportFragmentManager().beginTransaction()\n" +
                            "                    .add(containerId, fragment, tag)\n" +
                            "                    .commit()")
                    .endControlFlow()
                    .build();
            TypeSpec classType = TypeSpec.classBuilder("AboutMeFragmentMaster")
                    .addModifiers(Modifier.PUBLIC)
                    .addMethod(create).build();
            try {
                JavaFile.builder("com.example.annotationtest", classType).build()

                        .writeTo(mFiler);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        return true;
    }
}
