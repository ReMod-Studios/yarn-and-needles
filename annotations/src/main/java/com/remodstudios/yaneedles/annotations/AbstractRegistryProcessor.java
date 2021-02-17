package com.remodstudios.yaneedles.annotations;

import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.squareup.javapoet.*;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.registry.Registry;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO a lil messy atm, will clean later - leocth
public abstract class AbstractRegistryProcessor<A extends Annotation> extends AbstractProcessor {

    protected Messager messager;
    protected Filer filer;
    protected final Class<A> annoClass;
    protected final String registryName;
    protected final CodeBlock.Builder clinitBuilder;
    protected final MethodSpec.Builder initMethodBuilder;
    protected final MethodSpec.Builder clientInitMethodBuilder;

    public AbstractRegistryProcessor(Class<A> annoClass, String registryName) {
        this.annoClass = annoClass;
        this.registryName = registryName;
        this.clinitBuilder = CodeBlock.builder();
        this.initMethodBuilder = MethodSpec.methodBuilder("init")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(ArtificeResourcePack.ServerResourcePackBuilder.class, "pack");
        this.clientInitMethodBuilder = MethodSpec.methodBuilder("clientInit")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addParameter(ArtificeResourcePack.ClientResourcePackBuilder.class, "pack");
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

    protected void processElement(TypeElement classElement, A annotation) throws ProcessingException, IOException {
        Set<VariableElement> fields = getFields(classElement);
        parseResGen(classElement, fields, annotation, parseEntries(classElement, fields, annotation));
        writeRegistry(classElement, annotation);
    }

    protected abstract Map<VariableElement, String> parseEntries(
        TypeElement classElement,
        Set<VariableElement> fields,
        A annotation
    ) throws ProcessingException;

    protected abstract void parseResGen(
        TypeElement classElement,
        Set<VariableElement> fields,
        A annotation,
        Map<VariableElement, String> field2IdMap
    );

    /**
     * Processes a single field and adds the registry id to the field name to a map.
     */
    protected String processField(String inputClassName, String namespace, VariableElement field) throws ProcessingException {
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

        initMethodBuilder.addStatement(
                "$1T.register($1T.$2N, $3S, $4N.$5N)",
                Registry.class,
                registryName,
                id.toString(),
                inputClassName,
                field.getSimpleName().toString()
            );

        return id.toString();
    }

    protected abstract String getOutputClassName(A annotation);

    /**
     * Generates the resultant registry file.
     */
    protected void writeRegistry(TypeElement classElement, A annotation) throws IOException {
        String packageName = ((PackageElement)classElement.getEnclosingElement()).getQualifiedName().toString();
        String inputClassName = classElement.getSimpleName().toString();
        String outputClassName = getOutputClassName(annotation);
        if (outputClassName.isEmpty())
            outputClassName = inputClassName + "Registry";


        String outputClassQualifiedName = packageName + "." + outputClassName;

        TypeSpec registryType = TypeSpec.classBuilder(outputClassName)
                .addModifiers(Modifier.PUBLIC)
                .addField(
                    ResourceGenerator[].class,
                    "RESOURCE_GENERATORS",
                    Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL
                )
                .addStaticBlock(clinitBuilder.build())
                .addMethod(initMethodBuilder.build())
                .addMethod(clientInitMethodBuilder.build())
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

    public static Set<VariableElement> getFields(TypeElement classElement) {
        return classElement.getEnclosedElements().stream()
            .filter(e -> e.getKind().isField())
            .map(VariableElement.class::cast)
            .collect(Collectors.toSet());
    }

    public static TypeMirror getResGenClass(ResGen resGen) {
        // thank you java very cool
        try
        {
            //noinspection unused
            Class<?> whatever = resGen.value();
        }
        catch( MirroredTypeException mte )
        {
            return mte.getTypeMirror();
        }
        return null; // can this ever happen ??
    }
}
