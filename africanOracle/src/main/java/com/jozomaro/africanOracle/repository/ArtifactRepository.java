package com.jozomaro.africanOracle.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.jozomaro.africanOracle.model.Artifact;
import com.jozomaro.africanOracle.model.Analytics;

@Repository
public interface  ArtifactRepository extends JpaRepository<Artifact, Long> {
    // SELECT * FROM artifacts WHERE LOWER(title) LIKE '%keyword%' OR LOWER(description) LIKE '%keyword%';
    @Query("SELECT a FROM Artifact a WHERE LOWER(a.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(a.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Artifact> findTop5ByKeyword(@Param("keyword") String keyword);

    // SELECT materials, COUNT(*) FROM artifacts GROUP BY materials;
    @Query("SELECT new com.jozomaro.africanOracle.model.Analytics(a.materials, COUNT(a)) FROM Artifact a GROUP BY a.materials")
    List<Analytics> countArtifactsByMaterial();

    @Query("SELECT new com.jozomaro.africanOracle.model.Analytics(a.place, COUNT(a)) " +
           "FROM Artifact a GROUP BY a.place")
    List<Analytics> countArtifactsByPlace();
}
