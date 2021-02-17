package com.remodstudios.yaneedles.annotations.block;

import com.google.auto.service.AutoService;
//import com.remodstudios.yaneedles.datagen.ResourceGenerator;
import com.remodstudios.yaneedles.annotations.AbstractRegistryProcessor;
import com.remodstudios.yaneedles.annotations.ProcessingException;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.Map;

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
    protected void processElement(TypeElement classElement, BlockRegistry annotation) throws ProcessingException, IOException {
        writeRegistry(classElement, annotation.outputClassName(), parseEntries(classElement, annotation));
    }

    @Override
    protected Map<String, String> parseEntries(TypeElement classElement, BlockRegistry annotation) throws ProcessingException {
        Map<String, String> id2FieldName = new Object2ObjectLinkedOpenHashMap<>();
        BlockRegistry registryAnnotation = classElement.getAnnotation(BlockRegistry.class);

        for (Element candidateField : classElement.getEnclosedElements()) {
            // Filter out eligible fields
            BlockRegistry.Entry entryAnnotation = candidateField.getAnnotation(BlockRegistry.Entry.class);
            if (registryAnnotation.onlyCheckMarkedEntries() && entryAnnotation == null)
                continue; // ignore
            processField(registryAnnotation.namespace(), (VariableElement) candidateField, id2FieldName);
        }
        return id2FieldName;
    }
}
