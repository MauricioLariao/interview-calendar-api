package com.tamanna.api.interview.repositories;

import com.tamanna.api.interview.entities.InterviewerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InterviewerRepository extends JpaRepository<InterviewerEntity, String> {
    @Query("select interviewer.name from InterviewerEntity interviewer")
    List<String> getAllNames();
}
