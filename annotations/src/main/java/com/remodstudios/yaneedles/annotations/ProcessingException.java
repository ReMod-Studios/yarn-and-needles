package com.remodstudios.yaneedles.annotations;

import javax.lang.model.element.Element;

public class ProcessingException extends IllegalStateException {
    public final Element culprit;

    public ProcessingException(String message, Element culprit) {
        super(message);
        this.culprit = culprit;
    }

    public ProcessingException(String message, Throwable cause, Element culprit) {
        super(message, cause);
        this.culprit = culprit;
    }
}