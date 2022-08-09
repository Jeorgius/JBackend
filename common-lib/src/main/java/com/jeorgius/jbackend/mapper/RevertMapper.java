package com.jeorgius.jbackend.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface RevertMapper<SOURCE, TARGET> {

    /**
     * Преобразовать в SOURCE
     *
     * @param targ    object to map
     * @param factory factory for entity
     * @return entity
     */
    SOURCE revert(TARGET targ, Supplier<SOURCE> factory);

    /**
     * Преобразовать в SOURCE
     *
     * @param targ object to map
     * @return entity
     */
    SOURCE revert(TARGET targ);

    /**
     * Преобразовать список TARGET в список SOURCE
     *
     * @param targets список из TARGET
     * @return targets SOURCE
     */
    default List<SOURCE> revert(List<TARGET> targets) {
        if (targets == null) {
            return Collections.emptyList();
        }
        return targets.stream().map(this::revert).collect(Collectors.toList());
    }

    /**
     * Преобразовать множество TARGET в множество SOURCE
     *
     * @param targets множество из TARGET
     * @return множество SOURCE
     */
    default Set<SOURCE> revert(Set<TARGET> targets) {
        if (targets == null) {
            return Collections.emptySet();
        }
        return targets.stream().map(this::revert).collect(Collectors.toSet());
    }

    /**
     * Преобразовать страницу TARGET в страницу SOURCE
     *
     * @param targets множество из TARGET
     * @return множество SOURCE
     */
    default Page<SOURCE> revert(Page<TARGET> targets) {
        return targets != null ? new PageImpl<>(revert(targets.getContent()), targets.getPageable(), targets.getTotalElements()) : Page.empty();
    }
}
