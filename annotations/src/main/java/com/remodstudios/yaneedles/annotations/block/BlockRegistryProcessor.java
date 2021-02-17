package com.remodstudios.yaneedles.annotations.block;

import com.google.auto.service.AutoService;
//import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.google.common.collect.Lists;
import com.remodstudios.yaneedles.annotations.AbstractRegistryProcessor;
import com.remodstudios.yaneedles.annotations.ProcessingException;
import com.remodstudios.yaneedles.annotations.ResGen;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.util.Identifier;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({
    "com.remodstudios.yaneedles.annotations.block.BlockRegistry",
    "com.remodstudios.yaneedles.annotations.block.BlockRegistry.Entry"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class BlockRegistryProcessor extends AbstractRegistryProcessor<BlockRegistry> {

    public BlockRegistryProcessor() {
        super(BlockRegistry.class, "BLOCK");
    }

    @Override
    protected Map<VariableElement, String> parseEntries(TypeElement classElement, Set<VariableElement> fields, BlockRegistry annotation) throws ProcessingException {
        Map<VariableElement, String> field2Id = new Object2ObjectLinkedOpenHashMap<>();
        BlockRegistry registryAnnotation = classElement.getAnnotation(BlockRegistry.class);

        for (VariableElement candidateField : fields) {
            // Filter out eligible fields
            BlockRegistry.Entry entryAnnotation = candidateField.getAnnotation(BlockRegistry.Entry.class);
            if (registryAnnotation.onlyCheckMarkedEntries() && entryAnnotation == null)
                continue; // ignore
            field2Id.put(candidateField, processField(classElement.getSimpleName().toString(), registryAnnotation.namespace(), candidateField));
        }
        return field2Id;
    }

    @Override
    protected void parseResGen(TypeElement classElement, Set<VariableElement> fields, BlockRegistry annotation, Map<VariableElement, String> field2Id) {
        Map<TypeMirror, List<VariableElement>> seenResGenClassesToFields = new Object2ObjectLinkedOpenHashMap<>();
        // Set<Pair<Class<? extends ResourceGenerator>, String>> seenUniqueClassArgPairs = new ObjectLinkedOpenHashSet<>();

        TypeMirror defaultGenClass = getResGenClass(annotation.defaultResourceGenerator());

        for (VariableElement field : fields) {
            TypeMirror genClass = defaultGenClass;
            ResGen resGen = field.getAnnotation(ResGen.class);
            if (resGen != null)
                genClass = getResGenClass(resGen);

            List<VariableElement> list = seenResGenClassesToFields.get(genClass);
            if (list == null) {
                seenResGenClassesToFields.put(genClass, Lists.newArrayList(field));
            }
            else {
                list.add(field);
            }
        }

        clinitBuilder.beginControlFlow("RESOURCE_GENERATORS = new ResourceGenerator[]"); // this actually isnt control flow but whatever

        // fuck i miss `auto` now - leocth
        int i = 0; // this is so cursed; please refactor
        Iterator<Map.Entry<TypeMirror, List<VariableElement>>> iterator = seenResGenClassesToFields.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<TypeMirror, List<VariableElement>> entry = iterator.next();
            clinitBuilder.add("new $T()", entry.getKey());
            if (iterator.hasNext()) clinitBuilder.add(",");
            clinitBuilder.add("\n");

            for (VariableElement field : entry.getValue()) {
                initMethodBuilder.addStatement("RESOURCE_GENERATORS[$L].generateData(pack, new $T($S))", i, Identifier.class, field2Id.get(field));
                clientInitMethodBuilder.addStatement("RESOURCE_GENERATORS[$L].generateAssets(pack, new $T($S))", i, Identifier.class, field2Id.get(field));
            }
            i++;
        }

        clinitBuilder.endControlFlow(""); // this is cheaty but it works
    }

    @Override
    protected String getOutputClassName(BlockRegistry annotation) {
        return annotation.outputClassName();
    }
}
