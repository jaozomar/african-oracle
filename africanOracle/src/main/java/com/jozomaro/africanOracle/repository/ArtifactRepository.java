package com.jozomaro.africanOracle.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jozomaro.africanOracle.model.Artifact;

@Repository
public interface  ArtifactRepository extends JpaRepository<Artifact, Long> {
    
    
}
