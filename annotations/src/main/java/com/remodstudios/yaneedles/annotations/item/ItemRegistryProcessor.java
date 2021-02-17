package com.remodstudios.yaneedles.annotations.item;

import com.google.auto.service.AutoService;
import com.remodstudios.yaneedles.annotations.AbstractRegistryProcessor;
import com.remodstudios.yaneedles.annotations.ProcessingException;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
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
    protected void processElement(TypeElement classElement, ItemRegistry annotation) throws ProcessingException, IOException {
        writeRegistry(classElement, annotation.outputClassName(), parseEntries(classElement, annotation));
    }

    protected Map<String, String> parseEntries(TypeElement classElement, ItemRegistry annotation) throws ProcessingException {
        Map<String, String> id2FieldName = new Object2ObjectLinkedOpenHashMap<>();

        for (Element candidateField : classElement.getEnclosedElements()) {
            // Filter out eligible fields
            ItemRegistry.Entry entryAnnotation = candidateField.getAnnotation(ItemRegistry.Entry.class);
            if (annotation.onlyCheckMarkedEntries() && entryAnnotation == null)
                continue; // ignore
            processField(annotation.namespace(), (VariableElement) candidateField, id2FieldName);
        }
        return id2FieldName;
    }
}
