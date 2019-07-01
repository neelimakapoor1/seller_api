package com.goodlife.config;

import java.sql.Types;

import org.hibernate.dialect.PostgreSQL92Dialect;

/**
 * Wrap default PostgreSQL9Dialect with 'json' type.
 *
 * @author timfulmer
 */
public class CustomPostgreSQLDialect extends PostgreSQL92Dialect {

    public CustomPostgreSQLDialect() {

        super();

        this.registerColumnType(Types.JAVA_OBJECT, "json");
        
        this.registerColumnType(Types.JAVA_OBJECT, "jsonb");
        
        this.registerColumnType(Types.ARRAY, "array");
    }
}
