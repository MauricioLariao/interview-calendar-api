package com.tamanna.api.interview.controllers;

import com.tamanna.api.interview.dtos.InterviewerSlotDto;
import com.tamanna.api.interview.dtos.InterviewerDto;
import com.tamanna.api.interview.entities.InterviewerEntity;
import com.tamanna.api.interview.services.InterviewerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/interviewers")
public class InterviewerController {

    @Autowired
    private InterviewerService interviewerService;

    @PostMapping
    public ResponseEntity<Object> createInterviewer(@Valid @RequestBody InterviewerDto interviewerDto){
        log.info("new interviewer registration {} ", interviewerDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(interviewerService.createInterviewer(interviewerDto));
    }
    @GetMapping
    public ResponseEntity<List<InterviewerEntity>> getAllInterviewers(){
        log.info("list interviewers ");
        return ResponseEntity.status(HttpStatus.OK).body(interviewerService.getAllInterviewers());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> getInterviewerByName(@PathVariable(value = "name") String name){
        log.info("list interviewer by name {} ", name);
        Optional<InterviewerEntity> interviewerEntityOptional = interviewerService.getInterviewerByName(name);
        if (!interviewerEntityOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interviewer not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(interviewerEntityOptional);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Object> deleteInterviewerByName(@PathVariable(value="name") String name){
        log.info("delete interviewer by name {} ", name);
        Optional<InterviewerEntity> interviewerEntityOptional = interviewerService.getInterviewerByName(name);
        if (!interviewerEntityOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Interviewer not found");
        }
        interviewerService.deleteInterviewerByName(name);
        return ResponseEntity.status(HttpStatus.OK).body("Interviewer deleted successfully");
    }

    @PostMapping("/slot")
    public ResponseEntity<Object> createInterviewerSlot (@Valid @RequestBody InterviewerSlotDto interviewSlotDto){
        log.info("new slot registration {} ", interviewSlotDto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(interviewerService.createInterviewerSlot(interviewSlotDto));
    }

    @GetMapping("/slot")
    public ResponseEntity<Object> getAllInterviewerSlot(){
        log.info("list slots");
        return ResponseEntity.status(HttpStatus.OK).body(interviewerService.getAllInterviewersSlot());
    }

    @GetMapping("/slot/{name}")
    public ResponseEntity<Object> getInterviewerSlotByName(@PathVariable(name = "name") String name){
        log.info("list slots by interviewer name {} ", name);
       return ResponseEntity.status(HttpStatus.OK).body(interviewerService.getInterviewerSlotByName(name));
    }

    @DeleteMapping("/slot/{name}")
    public ResponseEntity<Object> deleteInterviewerSlotByName(@PathVariable(name = "name") String name){
        log.info("delete slots by interviewer name {} ", name);
        interviewerService.deleteInterviewerSlotByName(name);
        return ResponseEntity.status(HttpStatus.OK).body("Interviewer Slot deleted successfully");
    }
}
