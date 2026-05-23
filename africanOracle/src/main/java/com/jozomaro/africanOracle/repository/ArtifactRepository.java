package com.jozomaro.africanOracle.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jozomaro.africanOracle.model.Artifact;

@Repository
public interface  ArtifactRepository extends JpaRepository<Artifact, Long> {
    @Query("SELECT a FROM Artifact a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Artifact> findTop5ByKeyword(@Param("keyword") String keyword);
}
