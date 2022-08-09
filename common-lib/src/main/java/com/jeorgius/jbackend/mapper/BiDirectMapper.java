package com.jeorgius.jbackend.mapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.BiFunction;

public interface BiDirectMapper<SOURCE, TARGET> extends Mapper<SOURCE, TARGET>, RevertMapper<SOURCE, TARGET> {

    default void recreateList(Collection<SOURCE> entities, Collection<TARGET> dtos,
                              BiFunction<SOURCE, TARGET, Boolean> equalator) {
        final Collection<SOURCE> entitiesFinal = entities == null ? new ArrayList<>() : entities;
        final Collection<TARGET> dtosFinal = dtos == null ? new ArrayList<>() : dtos;
        entitiesFinal.removeIf(entity -> dtosFinal.stream().noneMatch(dto -> equalator.apply(entity, dto)));
        dtosFinal.forEach(dto -> {
            var existsEntity = entitiesFinal.stream().filter(entity -> equalator.apply(entity, dto))
                    .findAny().orElse(null);
            if (existsEntity == null) {
                existsEntity = revert(dto);
                entitiesFinal.add(existsEntity);
            } else {
                SOURCE finalExistsEntity = existsEntity;
                existsEntity = revert(dto, () -> finalExistsEntity);
            }
        });
    }
}
