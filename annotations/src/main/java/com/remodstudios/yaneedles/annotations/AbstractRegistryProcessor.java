package com.remodstudios.yaneedles.annotations;

import com.remodstudios.yaneedles.annotations.block.BlockRegistry;
import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.squareup.javapoet.*;
import com.swordglowsblue.artifice.api.ArtificeResourcePack;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO a lil messy atm, will clean later - leocth
public abstract class AbstractRegistryProcessor<A extends Annotation> extends AbstractProcessor {

    protected Messager messager;
    protected Filer filer;
    protected Types types;
    protected final Class<A> annoClass;
    protected final String registryName;
    protected final AnnotationParser<A> annotationParser;
    protected final CodeBlock.Builder clinitBuilder;
    protected final MethodSpec.Builder initMethodBuilder;
    protected final MethodSpec.Builder clientInitMethodBuilder;

    public AbstractRegistryProcessor(Class<A> annoClass, String registryName, AnnotationParser<A> annotationParser) {
        this.annoClass = annoClass;
        this.registryName = registryName;
        this.annotationParser = annotationParser;
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
        this.types = processingEnv.getTypeUtils();
    }

    // aaaaaaaaaaaaaaaaa
    protected interface AnnotationParser<A extends Annotation> {
        String outputClassName(A anno);
        String namespace(A anno);
        boolean onlyCheckMarkedEntries(A anno);
        ResGen defaultResourceGenerator(A anno);
    }

    protected GeneratorEntry parseGeneratorEntry(ResGen resGen) {
        TypeMirror resGenClass = ProcessingHelpers.getResGenClass(resGen);
        String args = resGen.args();
        if (!args.isEmpty())
            return new GeneratorEntry.WithArgs(types, resGenClass, args);
        else
            return new GeneratorEntry.Simple(types, resGenClass);
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return safeExecute(roundEnv, (e, anno) -> {
            if (!e.getKind().isClass()) {
                throw new ProcessingException("Only classes and enums may be annotated with @" + annoClass.getSimpleName(), e);
            }
            TypeElement classElement = (TypeElement) e;
            List<CompactedEntry> entries = getEntries(classElement, annotationParser.onlyCheckMarkedEntries(anno), BlockRegistry.Entry.class);

            List<String> ids = new ObjectArrayList<>();
            // TODO: use `Either` to support arged generators?
            List<GeneratorEntry> seenResGens = new ObjectArrayList<>();
            GeneratorEntry defaultResGen = parseGeneratorEntry(annotationParser.defaultResourceGenerator(anno));

            entries.forEach(entry -> {
                entry.idIndex = ids.size();
                ids.add(ProcessingHelpers.getIdFromField(annotationParser.namespace(anno), entry.field));

                GeneratorEntry genEntry = defaultResGen;
                ResGen resGen = entry.field.getAnnotation(ResGen.class);
                if (resGen != null) genEntry = parseGeneratorEntry(resGen);

                entry.resGenIndex = seenResGens.indexOf(genEntry);
                if (entry.resGenIndex == -1) {
                    entry.resGenIndex = seenResGens.size();
                    seenResGens.add(genEntry);
                }
            });

            String inputClassName = classElement.getSimpleName().toString();

            // uh so this is my way of making arrays; im sorry lol - leocth
            clinitBuilder.beginControlFlow("RESOURCE_GENERATORS = new $T[]", ResourceGenerator.class);

            ListIterator<GeneratorEntry> resGenIter = seenResGens.listIterator();
            while (resGenIter.hasNext()) {
                GeneratorEntry resGen = resGenIter.next();
                resGen.writeToClinit(clinitBuilder);
                if (resGenIter.hasNext()) clinitBuilder.add(",");
                clinitBuilder.add("\n");
            }
            //TODO arged resgens
            clinitBuilder.endControlFlow("");

            clinitBuilder.beginControlFlow("IDENTIFIERS = new $T[]", Identifier.class);
            ListIterator<String> idIter = ids.listIterator();
            while (idIter.hasNext()) {
                String id = idIter.next();
                clinitBuilder.add("new $T($S)", Identifier.class, id);
                if (idIter.hasNext()) clinitBuilder.add(",");
                clinitBuilder.add("\n");
            }
            clinitBuilder.endControlFlow("");

            for (CompactedEntry entry : entries) {
                initMethodBuilder
                    .addStatement(
                        "$1T.register($1T.$2L, IDENTIFIERS[$3L], $4N.$5L)",
                        Registry.class, registryName, entry.idIndex,
                        inputClassName,
                        entry.getFieldName()
                    )
                    .addStatement("RESOURCE_GENERATORS[$L].generateData(pack, IDENTIFIERS[$L])", entry.resGenIndex, entry.idIndex);

                clientInitMethodBuilder
                    .addStatement("RESOURCE_GENERATORS[$L].generateAssets(pack, IDENTIFIERS[$L])", entry.resGenIndex, entry.idIndex);
            }

            writeRegistry(classElement, anno);
        });
    }

    public static class CompactedEntry {
        public final VariableElement field;
        public int idIndex;
        public int resGenIndex;

        public CompactedEntry(VariableElement field) { this.field = field; }

        public String getFieldName() { return field.getSimpleName().toString(); }
    }

    /**
     * Generates the resultant registry file.
     */
    protected void writeRegistry(TypeElement classElement, A annotation) throws IOException {
        String packageName = ((PackageElement)classElement.getEnclosingElement()).getQualifiedName().toString();
        String inputClassName = classElement.getSimpleName().toString();
        String outputClassName = annotationParser.outputClassName(annotation);
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
            .addField(
                Identifier[].class,
                "IDENTIFIERS",
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
     * Wrappers around wrappers
     */
    public boolean safeExecute(RoundEnvironment roundEnv, SafeProcessRoutine<A> routine) {
        return ProcessingHelpers.safeExecute(messager, roundEnv, annoClass, routine);
    }

    public <AE extends Annotation> List<CompactedEntry> getEntries(TypeElement classElement, boolean onlyCheckMarked, Class<AE> entryClass) {
        Stream<VariableElement> stream = classElement.getEnclosedElements().stream()
            .filter(e -> e.getKind().isField() && e.getModifiers().contains(Modifier.STATIC))
            .map(VariableElement.class::cast);

        if (onlyCheckMarked)
            stream = stream.filter(f -> ProcessingHelpers.hasAnnotation(f, entryClass));

        return stream
            .map(CompactedEntry::new)
            .collect(Collectors.toList());
    }

}
