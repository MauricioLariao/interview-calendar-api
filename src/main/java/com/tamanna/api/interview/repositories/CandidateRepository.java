package com.tamanna.api.interview.repositories;

import com.tamanna.api.interview.entities.CandidateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateEntity, String> {
    @Query("select candidate.name from CandidateEntity candidate")
    List<String> getAllNames();
}
