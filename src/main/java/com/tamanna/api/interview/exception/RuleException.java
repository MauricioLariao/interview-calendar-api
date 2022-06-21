package com.tamanna.api.interview.exception;

import java.util.Arrays;

public class RuleException extends RuntimeException {
    private String messageKey;
    private String[] arguments;

    private RuleException() {
    }

    public RuleException(String messageKey, String... arguments) {
        super(messageKey);
        this.messageKey = messageKey;
        this.arguments = arguments == null ? new String[0] : arguments;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public String[] getArguments() {
        return arguments;
    }

    @Override
    public String toString() {
        return super.toString() + ", arguments=" + Arrays.toString(arguments);
    }
}
