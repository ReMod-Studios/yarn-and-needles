package com.remodstudios.yaneedles.annotations.block;

import com.google.auto.service.AutoService;
import com.remodstudios.yaneedles.annotations.AbstractRegistryProcessor;
import com.remodstudios.yaneedles.annotations.ResGen;

import javax.annotation.processing.Processor;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;

@SupportedAnnotationTypes({
    "com.remodstudios.yaneedles.annotations.block.BlockRegistry",
    "com.remodstudios.yaneedles.annotations.block.BlockRegistry.Entry"
})
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Processor.class)
@SuppressWarnings("unused")
public class BlockRegistryProcessor extends AbstractRegistryProcessor<BlockRegistry> {

    public BlockRegistryProcessor() {
        super(BlockRegistry.class, "BLOCK", AnnotationParser.INSTANCE);
    }

    enum AnnotationParser implements AbstractRegistryProcessor.AnnotationParser<BlockRegistry> {
        INSTANCE;

        @Override
        public String outputClassName(BlockRegistry anno) {
            return anno.outputClassName();
        }

        @Override
        public String namespace(BlockRegistry anno) {
            return anno.namespace();
        }

        @Override
        public boolean onlyCheckMarkedEntries(BlockRegistry anno) {
            return anno.onlyCheckMarkedEntries();
        }

        @Override
        public ResGen defaultResourceGenerator(BlockRegistry anno) {
            return anno.defaultResourceGenerator();
        }
    }
}
