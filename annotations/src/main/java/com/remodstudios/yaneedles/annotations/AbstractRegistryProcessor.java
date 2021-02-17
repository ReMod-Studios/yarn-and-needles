package com.remodstudios.yaneedles.annotations;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;

// TODO a lil messy atm, will clean later - leocth
public abstract class AbstractRegistryProcessor<A extends Annotation> extends AbstractProcessor {

    protected Messager messager;
    protected Filer filer;
    protected final Class<A> annoClass;
    protected final String registryName;

    public AbstractRegistryProcessor(Class<A> annoClass, String registryName) {
        this.annoClass = annoClass;
        this.registryName = registryName;
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.messager = processingEnv.getMessager();
        this.filer = processingEnv.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return safeExecute(roundEnv, (e, annotation) -> {
            if (!e.getKind().isClass()) {
                throw new ProcessingException("Only classes and enums may be annotated with @" + annoClass.getSimpleName(), e);
            }
            processElement((TypeElement) e, annotation); // safe
        });
    }

    /**
     * Some exception handling and logging wrappers
     *
     * @param roundEnv the round environment; used for getting all annotated elements
     * @param routine the routine/callback to call on each element and annotation pair
     */
    protected boolean safeExecute(RoundEnvironment roundEnv, SafeProcessRoutine<A> routine) {
        try {
            for (Element e : roundEnv.getElementsAnnotatedWith(annoClass))
                routine.run(e, e.getAnnotation(annoClass));
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

    protected abstract void processElement(TypeElement classElement, A annotation) throws ProcessingException, IOException;

    protected abstract Map<String, String> parseEntries(TypeElement classElement, A annotation) throws ProcessingException;

    /**
     * Processes a single field and adds the registry id to the field name to a map.
     *
     * @param namespace the namespace of the registry
     * @param field the field being processed
     * @param id2FieldName the map to add ID to field name pairs to
     * @throws ProcessingException when the processing failed due to a violation; read the error message for details
     */
    protected void processField(String namespace, VariableElement field, Map<String, String> id2FieldName) throws ProcessingException {
        if (!field.getKind().isField())
            return; // ignore

        if (!field.getModifiers().contains(Modifier.STATIC))
            throw new ProcessingException("Candidate field does not meet expectations! (Expected a static field)", field);


        // Deduce register IDs
        String path = tryParsePath(field);

        Identifier id;
        try {
            id = new Identifier(namespace, path);
        } catch (InvalidIdentifierException e) {
            // oopsie x(
            throw new ProcessingException(
                    String.format(
                            "Deduced identifier (%s:%s) does not conform to the Minecraft identifier format: %s",
                            namespace, path, e.getMessage()
                    ),
                    e, field
            );
        }

        id2FieldName.put(id.toString(), field.getSimpleName().toString());
    }

    /**
     * Generates the resultant registry file.
     *
     * @param classElement the containing class
     * @param outputClassName the simple name of the output class
     * @param id2FieldName the map containing registry IDs to their corresponding field name
     * @throws IOException see the error message for more details
     */
    protected void writeRegistry(
        TypeElement classElement,
        String outputClassName,
        Map<String, String> id2FieldName)
        throws IOException
    {
        String packageName = ((PackageElement)classElement.getEnclosingElement()).getQualifiedName().toString();
        String inputClassName = classElement.getSimpleName().toString();
        if (outputClassName.isEmpty())
            outputClassName = inputClassName + "Registry";


        String outputClassQualifiedName = packageName + "." + outputClassName;

        MethodSpec.Builder initMethodBuilder = MethodSpec
                .methodBuilder("init")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC);

        id2FieldName.forEach((k,v) ->
                initMethodBuilder.addStatement("$1T.register($1T.$2N, $3S, $4N.$5N)", net.minecraft.util.registry.Registry.class, registryName, k, inputClassName, v)
        );

        TypeSpec registryType = TypeSpec.classBuilder(outputClassName)
                .addModifiers(Modifier.PUBLIC)
                .addMethod(initMethodBuilder.build())
                .build();

        JavaFile registryFile = JavaFile.builder(packageName, registryType)
                .addFileComment(
                        "\nGenerated file - all manual changes will be overwritten!\n" +
                                "Generated from: $L.java\n",
                        inputClassName)
                .build();

        JavaFileObject registrantFile = filer.createSourceFile(outputClassQualifiedName);

        try (PrintWriter out = new PrintWriter(registrantFile.openWriter())) {
            registryFile.writeTo(out);
        }
    }

    /**
     * Attempts to parse an identifier path from metadata:
     * <p>If a @{@link CustomId} annotation exists then use the custom id specified,
     * otherwise attempts to guess the path by lower-casing the field name.
     *
     * @param element the element
     */
    public static String tryParsePath(Element element) {
        CustomId customIdAnno = element.getAnnotation(CustomId.class);
        String fieldName = element.getSimpleName().toString();
        if (customIdAnno != null && !customIdAnno.value().isEmpty())
            return customIdAnno.value();
        else {
            return fieldName.toLowerCase(); // guesswork
        }
    }
}
