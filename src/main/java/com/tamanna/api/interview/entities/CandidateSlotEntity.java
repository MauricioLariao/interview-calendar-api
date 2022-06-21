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
@Table(name = "candidate_slot")
public class CandidateSlotEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JsonProperty("candidateName")
    @JoinColumn(name = "name", nullable = false)
    private CandidateEntity candidateEntity;

    @NotNull
    @ElementCollection
    @Column(length = Integer.MAX_VALUE)
    private List<AvailableSlot> availableSlotList;

}
