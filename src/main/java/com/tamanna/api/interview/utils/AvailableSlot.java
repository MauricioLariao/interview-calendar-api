package com.tamanna.api.interview.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AvailableSlot implements Serializable {

    private LocalDate day;
    private List<TimeSlot> timeSlotList;
}
