package com.jeorgius.jbackend.mapper;

import com.jeorgius.jbackend.dto.IdDto;
import com.jeorgius.jbackend.entities.VersionedEntity;
import com.jeorgius.jbackend.repository.VersionedRepository;
import com.jeorgius.jbackend.validation.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public interface Mapper<SOURCE, TARGET> {

    /**
     * Преобразовать в TARGET
     *
     * @param src     object to map
     * @param factory factory for entity
     * @return entity
     */
    TARGET convert(SOURCE src, Supplier<TARGET> factory);

    /**
     * Преобразовать в TARGET
     *
     * @param src object to map
     * @return entity
     */
    TARGET convert(SOURCE src);

    /**
     * Преобразовать список SOURCE в список TARGET
     *
     * @param sources список из SOURCE
     * @return список TARGET
     */
    default List<TARGET> convert(List<? extends SOURCE> sources) {
        if (sources == null) {
            return Collections.emptyList();
        }
        return sources.stream().map(this::convert).collect(Collectors.toList());
    }

    /**
     * Преобразовать множество SOURCE в множество TARGET
     *
     * @param sources множество из SOURCE
     * @return множество TARGET
     */
    default Set<TARGET> convert(Set<SOURCE> sources) {
        if (sources == null) {
            return Collections.emptySet();
        }
        return sources.stream().map(this::convert).collect(Collectors.toSet());
    }

    /**
     * Преобразовать страницу SOURCE в страницу TARGET
     *
     * @param sources множество из SOURCE
     * @return множество TARGET
     */
    default Page<TARGET> convert(Page<? extends SOURCE> sources) {
        return sources != null ? new PageImpl<>(convert(sources.getContent()), sources.getPageable(), sources.getTotalElements()) : Page.empty();
    }

    default <T> T wrapToMappingException(CheckedFunction<T> func) {
        try {
            return func.apply();
        } catch (Exception e) {
            throw new RuntimeException("Mapping exception occurred", e);
        }
    }

    default <T extends VersionedEntity, ID> T resolveEntity(ID id, VersionedRepository<T, ID> repository) {
        if (id == null) {
            return null;
        } else {
            return repository.findByIdWithDeleted(id).orElseThrow(
                    () -> new RuntimeException(String.format("Failed to resolve entity [%s] with id %s", getResolveEntityClass(repository).get(), id)));
        }
    }

    default <T extends VersionedEntity> T resolveEntity(IdDto idDto, VersionedRepository<T, Long> repository) {
        if (idDto == null) {
            return null;
        } else {
            return resolveEntity(idDto.getId(), repository);
        }
    }

    default <T extends VersionedEntity, E extends IdDto> List<T> resolveEntities(List<E> idDtos, VersionedRepository<T, Long> repository) {
        if (CollectionUtils.isEmpty(idDtos)) {
            return Collections.emptyList();
        } else {
            var ids = idDtos.stream().map(IdDto::getId).collect(Collectors.toSet());
            return resolveEntitiesByIds(ids, repository);
        }
    }

    default <T extends VersionedEntity> List<T> resolveEntitiesByIds(Collection<Long> ids, VersionedRepository<T, Long> repository) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        } else {
            var entities = repository.findAllById(ids);
            if (ids.size() != entities.size()) {
                var idsToSearch = ids.stream().map(x -> x != null ? x.toString() : "null").collect(Collectors.joining(", "));
                var idsReturned = entities.stream().map(x -> x.getId().toString()).collect(Collectors.joining(", "));
                throw new RuntimeException(String.format("Inconsistent entity resolving result. Search ids - [%s], returned ids - [%s]. Entity class - %s", idsToSearch, idsReturned, getResolveEntityClass(repository).get()));
            }
            return entities;
        }
    }

    default <T extends VersionedEntity, ID> T resolveEntityNotDeleted(ID id, VersionedRepository<T, ID> repository) {
        if (id == null) {
            return null;
        } else {
            var className = getResolveEntityClass(repository).get();
            return repository.findById(id).orElseThrow(
                    () -> new ValidationException(String.format("Запись \"%s\" c id %s не найдена", className, id)));
        }
    }

    default <T extends VersionedEntity> T resolveEntityNotDeleted(IdDto idDto, VersionedRepository<T, Long> repository) {
        if (idDto == null) {
            return null;
        } else {
            return resolveEntityNotDeleted(idDto.getId(), repository);
        }
    }


    private <T extends VersionedEntity, ID> Supplier<String> getResolveEntityClass(VersionedRepository<T, ID> repository) {
        return () -> {
            final String basePackage = "com.jeorgius.jbackend";
            Optional<Class<?>> repoInterfaceOpt = Arrays.stream(repository.getClass().getInterfaces())
                    .filter(x -> x.getPackage().getName().startsWith(basePackage))
                    .findFirst();
            if (repoInterfaceOpt.isPresent()) {
                Type[] genericInterfaces = repoInterfaceOpt.get().getGenericInterfaces();
                if (genericInterfaces != null && genericInterfaces.length > 0) {
                    Class firstArgClass = (Class) ((ParameterizedType) genericInterfaces[0]).getActualTypeArguments()[0];
                    if (firstArgClass.getPackage().getName().startsWith(basePackage)) {
                        return firstArgClass.getSimpleName();
                    }
                }
            }

            return "[UNRESOLVED, MAPPER IS - " + this.getClass().getSimpleName() + "]";
        };
    }

    default <T> T getResultFromClientIfOkOrNull(Supplier<ResponseEntity<T>> supplier) {
        var response = supplier.get();
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        }
        return null;
    }

    @FunctionalInterface
    interface CheckedFunction<T> {
        T apply() throws Exception;
    }

}
