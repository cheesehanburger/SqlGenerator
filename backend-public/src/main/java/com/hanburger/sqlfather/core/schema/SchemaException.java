package com.hanburger.sqlfather.core.schema;

/**
 * SQL 相关异常
 *
 * @author hanburger
 */
public class SchemaException extends RuntimeException {

    public SchemaException() {
    }

    public SchemaException(String message) {
        super(message);
    }

    public SchemaException(String message, Throwable cause) {
        super(message, cause);
    }

    public SchemaException(Throwable cause) {
        super(cause);
    }

    public SchemaException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
