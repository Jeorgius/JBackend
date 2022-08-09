package com.jeorgius.jbackend.config;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.spatial.dialect.postgis.PostgisPG95Dialect;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomPostgreSQL95Dialect extends PostgisPG95Dialect {

    public CustomPostgreSQL95Dialect() {
        super();
        registerFunction("translate", new SQLFunctionTemplate(StandardBasicTypes.STRING, "translate(?1, ?2, ?3)"));
        registerFunction("concat_like", new SQLFunctionTemplate(StandardBasicTypes.STRING, "concat('%', ?1, '%')"));
//        registerFunction(PrepareStringTrgm.NAME, new PrepareStringTrgm());
//        registerFunction(IntersectsFunction.NAME, new IntersectsFunction());
    }


}
