package com.tamanna.api.interview.dtos;

import com.tamanna.api.interview.utils.AvailableSlot;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterviewerAvailableSlotsDto {

    private String interviewerName;
    private List<AvailableSlot> interviewerAvailableSlotList;

}
