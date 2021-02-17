package com.remodstudios.yaneedles.annotations.item;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import com.remodstudios.yaneedles.annotations.AbstractRegistryProcessor;
import com.remodstudios.yaneedles.annotations.ProcessingException;
import com.remodstudios.yaneedles.annotations.ResGen;
import com.remodstudios.yaneedles.annotations.block.BlockRegistry;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.util.Identifier;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes({
    "com.remodstudios.yaneedles.annotations.item.ItemRegistry",
    "com.remodstudios.yaneedles.annotations.item.ItemRegistry.Entry"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class ItemRegistryProcessor extends AbstractRegistryProcessor<ItemRegistry> {
    public ItemRegistryProcessor() {
        super(ItemRegistry.class, "ITEM");
    }

    @Override
    protected Map<VariableElement, String> parseEntries(TypeElement classElement, Set<VariableElement> fields, ItemRegistry annotation) throws ProcessingException {
        Map<VariableElement, String> field2Id = new Object2ObjectLinkedOpenHashMap<>();
        ItemRegistry registryAnnotation = classElement.getAnnotation(ItemRegistry.class);

        for (VariableElement candidateField : fields) {
            // Filter out eligible fields
            ItemRegistry.Entry entryAnnotation = candidateField.getAnnotation(ItemRegistry.Entry.class);
            if (registryAnnotation.onlyCheckMarkedEntries() && entryAnnotation == null)
                continue; // ignore
            field2Id.put(candidateField, processField(classElement.getSimpleName().toString(), registryAnnotation.namespace(), candidateField));
        }
        return field2Id;
    }

    @Override
    protected void parseResGen(TypeElement classElement, Set<VariableElement> fields, ItemRegistry annotation, Map<VariableElement, String> field2Id) {
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

    // this is the jank you have to deal with cos java does not allow inheritance in annotations
    @Override
    protected String getOutputClassName(ItemRegistry annotation) {
        return annotation.outputClassName();
    }
}
