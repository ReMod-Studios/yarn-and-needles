package com.remodstudios.yaneedles.annotations;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;

import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.lang.annotation.Annotation;

public final class ProcessingHelpers {
    private ProcessingHelpers() {}

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

    public static boolean hasAnnotation(Element e, Class<? extends Annotation> annoClass) {
        return e.getAnnotation(annoClass) != null;
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

    public static String getIdFromField(String namespace, VariableElement field) throws ProcessingException {
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
        return id.toString();
    }

    /**
     * Some exception handling and logging wrappers
     */
    public static <A extends Annotation> boolean safeExecute(Messager messager, RoundEnvironment roundEnv, Class<A> annoClass, SafeProcessRoutine<A> routine) {
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
}
