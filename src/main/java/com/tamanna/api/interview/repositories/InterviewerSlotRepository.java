package com.tamanna.api.interview.repositories;

import com.tamanna.api.interview.entities.InterviewerSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface InterviewerSlotRepository extends JpaRepository<InterviewerSlotEntity, Long> {

    @Query("select interviewerSlot from InterviewerSlotEntity interviewerSlot where interviewerSlot.interviewerEntity.name = :name")
    InterviewerSlotEntity getInterviewerSlotByInterviewerName(String name);

}
