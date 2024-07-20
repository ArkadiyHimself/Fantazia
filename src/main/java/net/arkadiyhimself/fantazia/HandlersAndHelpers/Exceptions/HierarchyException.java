package net.arkadiyhimself.fantazia.HandlersAndHelpers.Exceptions;

import org.apache.commons.lang3.StringEscapeUtils;

public class HierarchyException extends RuntimeException {
    public HierarchyException(String message) {
        super(StringEscapeUtils.escapeJava(message));
    }
}
