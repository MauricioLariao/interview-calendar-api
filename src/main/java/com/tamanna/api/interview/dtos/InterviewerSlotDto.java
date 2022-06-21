package com.tamanna.api.interview.dtos;

import com.tamanna.api.interview.utils.AvailableSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InterviewerSlotDto {

    private String interviewerName;
    private List<AvailableSlot> availableSlotList;

}
