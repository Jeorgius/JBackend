package com.jeorgius.jbackend.repository;

import com.jeorgius.jbackend.entities.VersionedEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface VersionedRepository<T extends VersionedEntity, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    @Override
    List<T> findAll(Specification<T> specification);

    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    @Override
    Page<T> findAll(Specification<T> specification, Pageable pageable);

    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    @Override
    List<T> findAll(Specification<T> specification, Sort sort);

    @QueryHints(@QueryHint(name = org.hibernate.jpa.QueryHints.HINT_PASS_DISTINCT_THROUGH, value = "false"))
    @Override
    long count(Specification<T> specification);

    @Override
    @Query("select e from #{#entityName} e where e.deleted = false")
    List<T> findAll(Sort sort);

    @Override
    @Query("select e from #{#entityName} e where e.deleted = false")
    Page<T> findAll(@Param("pageable") Pageable pageable);

    @Query("select e from #{#entityName} e where (:archived = false  and e.deleted = false) or :archived = true")
    Page<T> findAll(@Param("pageable") Pageable pageable, @Param("archived") boolean archived);

    @Override
    @Query("select e from #{#entityName} e where e.id = ?1 and e.deleted = false")
    Optional<T> findById(@Param("id") ID id);

    @Query("select e from #{#entityName} e where e.id = ?1")
    Optional<T> findByIdWithDeleted(@Param("id") ID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from #{#entityName} e where e.id = ?1 and e.deleted = false")
    Optional<T> findByIdWithLock(@Param("id") ID id);

    @Override
    @Query("select count(e)>0 from #{#entityName} e where e.id = ?1 and e.deleted = false")
    boolean existsById(@Param("id") ID id);

    @Override
    @Query("select e from #{#entityName} e where e.deleted = false")
    List<T> findAll();

    @Query("select e from #{#entityName} e")
    List<T> findAllWithDeleted();

    @Query("select e.id from #{#entityName} e where e.deleted = false")
    List<ID> findAllIds();

    @Query("select e.id from #{#entityName} e")
    List<ID> findAllIdsWithDeleted();

    @Override
    @Query("select e from #{#entityName} e where e.id in ?1 and e.deleted = false")
    List<T> findAllById(@Param("ids") Iterable<ID> ids);

    @Query("select e from #{#entityName} e where e.id in ?1")
    List<T> findAllByIdWithDeleted(@Param("ids") Iterable<ID> ids);

    @Override
    @Query("select count(e) from #{#entityName} e where e.deleted = false")
    long count();

    @Override
    @Modifying
    @Query("update #{#entityName} e set e.deleted = true")
    void deleteAll();

    @Modifying
    @Query("update #{#entityName} e set e.deleted = true where e.id = ?1 ")
    void softDeleteById(@Param("id") ID id);

    @Modifying
    default T softRestore(T entity) {
        entity.setDeleted(false);
        return this.save(entity);
    }

    @Modifying
    @Query("update #{#entityName} e set e.deleted = false where e.id = ?1 ")
    void softRestoreById(@Param("id") ID id);

    @Modifying
    default T softDelete(T entity) {
        entity.setDeleted(true);
        return this.save(entity);
    }

    @Query("select e from #{#entityName} e where e.deleted = true")
    Iterable<T> findAllSoftDeleted(Sort sort);

    @Query("select e from #{#entityName} e where e.deleted = true")
    Page<T> findAllSoftDeleted(Pageable pageable);

    @Query("select e from #{#entityName} e where e.id = ?1 and e.deleted = true")
    Optional<T> findSoftDeletedById(@Param("id") ID id);

    @Query("select count(e) > 0 from #{#entityName} e where e.id = ?1 and e.deleted = true")
    boolean existsSoftDeletedById(@Param("id") ID id);

    @Query("select e from #{#entityName} e where e.deleted = true")
    Iterable<T> findAllSoftDeleted();

    @Query("select e from #{#entityName} e where e.id in ?1 and e.deleted = true")
    Iterable<T> findAllSoftDeletedById(@Param("ids") Iterable<ID> ids);

    @Query("select count(e) from #{#entityName} e where e.deleted = true")
    long countSoftDeleted();

    @Query(value = "select pg_try_advisory_lock(:lockId)", nativeQuery = true)
    boolean tryAdvisoryLock(long lockId);

    @Query(value = "select pg_advisory_unlock(:lockId)", nativeQuery = true)
    boolean advisoryUnlock(long lockId);
}
