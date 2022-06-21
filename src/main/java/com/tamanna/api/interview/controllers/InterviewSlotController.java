package com.tamanna.api.interview.controllers;

import com.tamanna.api.interview.dtos.InterviewConsultSlotDto;
import com.tamanna.api.interview.dtos.InterviewSlotsResponseDto;
import com.tamanna.api.interview.services.InterviewSlotService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/api/interview-slot")
public class InterviewSlotController {

    @Autowired
    private InterviewSlotService interviewSlotService;

    @GetMapping
    public ResponseEntity<InterviewSlotsResponseDto> getAllInterviewSlots(@Valid @RequestBody InterviewConsultSlotDto interviewSlotDto){
        log.info("list interview slots");
        return ResponseEntity.status(HttpStatus.OK).body(interviewSlotService.getInterviewSlots(interviewSlotDto));
    }
}
