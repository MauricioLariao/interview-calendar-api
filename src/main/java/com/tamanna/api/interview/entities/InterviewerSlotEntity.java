package com.tamanna.api.interview.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tamanna.api.interview.utils.AvailableSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "interviewer_slot")
public class InterviewerSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JsonProperty("interviewerName")
    @JoinColumn(name = "name", nullable = false)
    private InterviewerEntity interviewerEntity;

    @NotNull
    @ElementCollection
    @Column(length = Integer.MAX_VALUE)
    private List<AvailableSlot> availableSlotList;

}