package com.example.annotation_compiler;

import com.example.annotations.MyInterface;
import com.google.auto.service.AutoService;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;

@AutoService(Processor.class)
public class MyProcessor extends AbstractProcessor {
    private Filer mFiler;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        mFiler = processingEnvironment.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(MyInterface.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        MethodSpec methodSpec = MethodSpec.methodBuilder("main")
                .returns(TypeName.VOID)
                .addParameter(String[].class, "args")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)

                .addCode("System.out.println(\"hello,world\");")
                .build();

        TypeSpec classType = TypeSpec.classBuilder("HelloWorld")
                .addMethod(methodSpec)
                .addModifiers(Modifier.PUBLIC)
                .build();
        try {
            JavaFile.builder("com", classType)
                    .build()
                    .writeTo(mFiler);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
