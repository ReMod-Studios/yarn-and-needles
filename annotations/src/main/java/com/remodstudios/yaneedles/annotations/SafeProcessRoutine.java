package com.remodstudios.yaneedles.annotations;

import javax.lang.model.element.Element;
import java.io.IOException;
import java.lang.annotation.Annotation;

@FunctionalInterface
public interface SafeProcessRoutine<A extends Annotation> {
    void run(Element e, A annotation) throws ProcessingException, IOException;
}
