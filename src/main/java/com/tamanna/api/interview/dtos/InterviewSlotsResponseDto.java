package com.tamanna.api.interview.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewSlotsResponseDto {

    private String candidateName;
    private List<String> interviewersNames;
    private List<InterviewerSlotDto> interviewerSlotList;

}
