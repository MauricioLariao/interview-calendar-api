package com.tamanna.api.interview.controllers;

import com.tamanna.api.interview.dtos.CandidateDto;
import com.tamanna.api.interview.dtos.CandidateSlotDto;
import com.tamanna.api.interview.entities.CandidateEntity;
import com.tamanna.api.interview.services.CandidateService;
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
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping
    public ResponseEntity<Object> createCandidate(@Valid @RequestBody CandidateDto candidateDto){
        log.info("new candidate registration {} ", candidateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(candidateService.createCandidate(candidateDto));
    }
    @GetMapping
    public ResponseEntity<List<CandidateEntity>> getAllCandidates(){
        log.info("list candidates ");
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.getAllCandidates());
    }

    @GetMapping("/{name}")
    public ResponseEntity<Object> getCandidateByName(@PathVariable(value = "name") String name){
        log.info("list candidate by name {} ", name);
        Optional<CandidateEntity> candidateEntityOptional = candidateService.getCandidateByName(name);
        if (!candidateEntityOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("candidate not found");
        }
        return ResponseEntity.status(HttpStatus.OK).body(candidateEntityOptional);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Object> deleteCandidateByName(@PathVariable(value="name") String name){
        log.info("delete candidate by name {} ", name);
        Optional<CandidateEntity> candidateEntityOptional = candidateService.getCandidateByName(name);
        if (!candidateEntityOptional.isPresent()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("candidate not found");
        }
        candidateService.deleteCandidateByName(name);
        return ResponseEntity.status(HttpStatus.OK).body("candidate deleted successfully");
    }

    @PostMapping("/slot")
    public ResponseEntity<Object> createCandidateSlot (@Valid @RequestBody CandidateSlotDto candidateSlotDto){
        log.info("new slot registration {} ", candidateSlotDto);
        return  ResponseEntity.status(HttpStatus.CREATED).body(candidateService.createCandidateSlot(candidateSlotDto));
    }

    @GetMapping("/slot")
    public ResponseEntity<Object> getAllCandidateSlot(){
        log.info("list slots");
        return ResponseEntity.status(HttpStatus.OK).body(candidateService.getAllCandidatesSlot());
    }

    @GetMapping("/slot/{name}")
    public ResponseEntity<Object> getCandidateSlotByName(@PathVariable(name = "name") String name){
        log.info("list slots by candidate name {} ", name);
       return ResponseEntity.status(HttpStatus.OK).body(candidateService.getCandidateSlotByName(name));
    }

    @DeleteMapping("/slot/{name}")
    public ResponseEntity<Object> deleteCandidateSlotByName(@PathVariable(name = "name") String name){
        log.info("delete slots by candidate name {} ", name);
        candidateService.deleteCandidateSlotByName(name);
        return ResponseEntity.status(HttpStatus.OK).body("candidate Slot deleted successfully");
    }
}
