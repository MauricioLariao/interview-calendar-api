package com.tamanna.api.interview.repositories;

import com.tamanna.api.interview.entities.CandidateSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CandidateSlotRepository extends JpaRepository<CandidateSlotEntity, Long> {

    @Query("select candidateSlot from CandidateSlotEntity candidateSlot where candidateSlot.candidateEntity.name = :name")
    CandidateSlotEntity getCandidateSlotByCandidateName(String name);

}
