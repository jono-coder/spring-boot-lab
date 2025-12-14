package com.jono.core.repository;

import com.jono.core.entity.ClientEntity;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Integer> {

    @Query("from ClientEntity order by accountNo")
    @QueryHints(@QueryHint(name = "javax.persistence.query.timeout", value = "5000"))
    Slice<ClientEntity> findAllSortedByAccountNo(Pageable pageable);

    Optional<ClientEntity> findByAccountNo(String accountNo);

}
