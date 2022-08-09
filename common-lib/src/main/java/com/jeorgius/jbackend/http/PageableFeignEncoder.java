package com.jeorgius.jbackend.http;

import feign.RequestTemplate;
import feign.codec.EncodeException;
import feign.codec.Encoder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Type;
import java.util.Optional;

public class PageableFeignEncoder implements Encoder {
    private final static String COMMA_ENCODED = "%2C";
    private final Encoder delegate;

    public PageableFeignEncoder(Encoder delegate) {
        this.delegate = delegate;
    }

    @Override
    public void encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException {
        if (object instanceof Pageable) {
            var pageable = (Pageable) object;
            template.query("page", String.valueOf(pageable.getPageNumber()));
            template.query("size", String.valueOf(pageable.getPageSize()));
            template.query("sort", sortToRequestParamString(pageable.getSort()));
        } else {
            delegate.encode(object, bodyType, template);
        }
    }

    public static String sortToRequestParamString(Sort sort) {
        var sortStringBuilder = new StringBuilder();
        Optional<Sort.Direction> firstDirection = Optional.empty();
        for (Sort.Order order : sort) {
            sortStringBuilder.append(sortStringBuilder.length() == 0 ? "" : COMMA_ENCODED).append(order.getProperty());
            if (firstDirection.isEmpty()) {
                firstDirection = Optional.of(order.getDirection());
            }
        }
        firstDirection.ifPresent(direction -> sortStringBuilder.append(COMMA_ENCODED).append(direction));
        return sortStringBuilder.toString();
    }
}
