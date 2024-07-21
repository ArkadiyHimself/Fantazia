package net.arkadiyhimself.fantazia.util.library.hierarchy;

import org.apache.commons.lang3.StringEscapeUtils;

public class HierarchyException extends RuntimeException {
    public HierarchyException(String message) {
        super(StringEscapeUtils.escapeJava(message));
    }
}
