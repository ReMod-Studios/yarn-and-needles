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
        super(ItemRegistry.class, "ITEM", AnnotationParser.INSTANCE);
    }

    enum AnnotationParser implements AbstractRegistryProcessor.AnnotationParser<ItemRegistry> {
        INSTANCE;

        @Override
        public String outputClassName(ItemRegistry anno) {
            return anno.outputClassName();
        }

        @Override
        public String namespace(ItemRegistry anno) {
            return anno.namespace();
        }

        @Override
        public boolean onlyCheckMarkedEntries(ItemRegistry anno) {
            return anno.onlyCheckMarkedEntries();
        }

        @Override
        public ResGen defaultResourceGenerator(ItemRegistry anno) {
            return anno.defaultResourceGenerator();
        }
    }
}
