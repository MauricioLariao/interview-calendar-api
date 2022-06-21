package com.tamanna.api.interview.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interviewer")
public class InterviewerEntity {

    @Id
    private String name;

    @JsonIgnore
    @OneToOne(mappedBy = "interviewerEntity", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private InterviewerSlotEntity interviewerSlotEntity;

}
