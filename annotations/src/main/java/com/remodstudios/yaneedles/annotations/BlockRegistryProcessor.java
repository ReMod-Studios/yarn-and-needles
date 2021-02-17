package com.remodstudios.yaneedles.annotations;

import com.google.auto.service.AutoService;
//import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({
    "com.remodstudios.yaneedles.annotations.BlockRegistry",
    "com.remodstudios.yaneedles.annotations.BlockRegistry.Entry",
    "com.remodstudios.yaneedles.annotations.ResGen"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class BlockRegistryProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            for (Element e1 : roundEnv.getElementsAnnotatedWith(BlockRegistry.class)) {
                if (!e1.getKind().isClass()) {
                    throw new ProcessingException("Only classes and enums may be annotated with @BlockRegistry!", e1);
                }

                TypeElement classElement = (TypeElement) e1;

                Map<String, String> id2FieldName = new Object2ObjectLinkedOpenHashMap<>();
                handleEntries(classElement, id2FieldName);

                String packageName = ((PackageElement)classElement.getEnclosingElement()).getQualifiedName().toString();
                writeRegistry(
                    packageName,
                    classElement.getQualifiedName().toString(),
                    classElement.getSimpleName().toString(),
                    id2FieldName
                );
            }
        }
        catch (ProcessingException e) {
            messager.printMessage(
                Diagnostic.Kind.ERROR,
                e.getMessage(),
                e.culprit
            );
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    private void handleEntries(TypeElement classElement, Map<String, String> id2FieldName) throws ProcessingException, IOException {
        BlockRegistry registryAnnotation = classElement.getAnnotation(BlockRegistry.class);

        for (Element candidateField : classElement.getEnclosedElements()) {
            BlockRegistry.Entry entryAnnotation = candidateField.getAnnotation(BlockRegistry.Entry.class);
            if (registryAnnotation.onlyCheckMarkedEntries() && entryAnnotation == null)
                continue; // ignore
            if (!candidateField.getKind().isField())
                continue; // ignore

            if (!candidateField.getModifiers().contains(Modifier.STATIC))
                throw new ProcessingException(
                    "Candidate field does not meet expectations! " +
                        "(Expected a static field)", candidateField);

            VariableElement field = (VariableElement) candidateField;
            String fieldName = field.getSimpleName().toString();
            String path;
            if (entryAnnotation != null && !entryAnnotation.value().isEmpty())
                path = entryAnnotation.value();
            else {
                path = fieldName.toLowerCase();
            }
            Identifier id;
            try {
                id = new Identifier(registryAnnotation.namespace(), path);
            } catch (InvalidIdentifierException e) {
                throw new ProcessingException(
                    String.format(
                        "Deduced identifier (%s:%s) does not conform to the Minecraft identifier format: %s",
                        registryAnnotation.namespace(), path, e.getMessage()
                    ),
                    e, field
                );
            }
            id2FieldName.put(id.toString(), fieldName);
        }
    }

    private void writeRegistry(
        String packageName,
        String classQualifiedName,
        String classSimpleName,
        Map<String, String> id2FieldName)
        throws IOException {

        MethodSpec.Builder initMethodBuilder = MethodSpec
            .methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        id2FieldName.forEach((k,v) ->
            initMethodBuilder.addStatement("$T.register($T.BLOCK, $S, $L.$L)", Registry.class, Registry.class, k, classSimpleName, v)
        );

        TypeSpec registryType = TypeSpec.classBuilder(classSimpleName + "Registry")
            .addModifiers(Modifier.PUBLIC)
            .addMethod(initMethodBuilder.build())
            .build();

        JavaFile registryFile = JavaFile.builder(packageName, registryType)
            .addFileComment(
                "Generated file - all manual changes will be overwritten!\n" +
                "Generated from: $L.java\n",
                classSimpleName)
            .build();

        JavaFileObject registrantFile = filer.createSourceFile(classQualifiedName + "Registry");

        try (PrintWriter out = new PrintWriter(registrantFile.openWriter())) {
            registryFile.writeTo(out);
        }
    }
}
