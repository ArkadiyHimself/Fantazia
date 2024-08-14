package net.arkadiyhimself.fantazia.data.talents;

import org.apache.commons.lang3.StringEscapeUtils;

public class TalentDataException extends RuntimeException {
    public TalentDataException(String message) {
        super(StringEscapeUtils.escapeJava(message));
    }
}
