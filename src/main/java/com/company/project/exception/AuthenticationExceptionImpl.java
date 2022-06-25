package com.company.project.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationExceptionImpl extends AuthenticationException {

    public AuthenticationExceptionImpl(String msg, Throwable t) {
        super(msg, t);
    }

    public AuthenticationExceptionImpl(String msg) {
        super(msg);
    }

    public AuthenticationExceptionImpl(Throwable t) {
        super(t.getMessage(), t);
    }
}
