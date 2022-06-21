package com.tamanna.api.interview.services;

import com.tamanna.api.interview.dtos.CandidateDto;
import com.tamanna.api.interview.dtos.CandidateSlotDto;
import com.tamanna.api.interview.exception.RuleException;
import com.tamanna.api.interview.entities.CandidateEntity;
import com.tamanna.api.interview.entities.CandidateSlotEntity;
import com.tamanna.api.interview.repositories.CandidateRepository;
import com.tamanna.api.interview.repositories.CandidateSlotRepository;
import com.tamanna.api.interview.utils.AvailableSlot;
import com.tamanna.api.interview.utils.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private final CandidateRepository candidateRepository;

    @Autowired
    private final CandidateSlotRepository candidateSlotRepository;

    public CandidateService(CandidateRepository candidateRepository, CandidateSlotRepository candidateSlotRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateSlotRepository = candidateSlotRepository;
    }

    public CandidateEntity createCandidate(CandidateDto candidateDto){

        CandidateEntity candidate = CandidateEntity.builder()
                .name(candidateDto.getName())
                .build();
        checkCandidateInformation(candidate);
        return candidateRepository.save(candidate);
    }

    public List<CandidateEntity> getAllCandidates(){
        return candidateRepository.findAll();
    }

    public Optional<CandidateEntity> getCandidateByName(String name){
        return candidateRepository.findById(name);
    }

    public void deleteCandidateByName(String name){
        candidateRepository.deleteById(name);
    }

    public CandidateSlotEntity createCandidateSlot(CandidateSlotDto candidateSlotDto) {

        CandidateEntity candidate = CandidateEntity.builder()
                .name(candidateSlotDto.getCandidateName())
                .build();
        CandidateSlotEntity candidateSlotEntity = CandidateSlotEntity.builder()
                .candidateEntity(candidate)
                .availableSlotList(candidateSlotDto.getAvailableSlotList())
                .build();

        checkCandidateSlotInformation(candidateSlotEntity);

        CandidateSlotEntity candidateExistingSlot = checkIfCandidateHasSlotCreated(
                candidateSlotEntity);

        if (candidateExistingSlot != null) {
            addNewSlot(candidateExistingSlot, candidateSlotEntity);
            return candidateSlotRepository.save(candidateExistingSlot);
        }

        return candidateSlotRepository.save(candidateSlotEntity);
    }

    public List<CandidateSlotEntity> getAllCandidatesSlot() {
        return candidateSlotRepository.findAll();
    }

    public CandidateSlotEntity getCandidateSlotByName(String name) {
        return candidateSlotRepository.getCandidateSlotByCandidateName(name);
    }

    public void deleteCandidateSlotByName(String name) {
        Long candidateAvailabilityIdToBeDeleted =
                candidateSlotRepository.getCandidateSlotByCandidateName(name).getId();

        candidateSlotRepository.deleteById(candidateAvailabilityIdToBeDeleted);
    }

    private void checkCandidateInformation(CandidateEntity candidateEntity) {
        checkNameIsFilled(candidateEntity);
        checkUniqueName(candidateEntity);
    }

    private void checkNameIsFilled(CandidateEntity candidateEntity) {
        String nameOfCandidateToBeCreated = candidateEntity.getName();

        if (nameOfCandidateToBeCreated == null || nameOfCandidateToBeCreated.isBlank()) {
            throw new RuleException("You must provide a name!",
                    candidateEntity.getName() != null ? candidateEntity.getName() : null);
        }
    }

    private void checkUniqueName(CandidateEntity candidateEntity) {
        String nameOfCandidateToBeCreated = candidateEntity.getName();
        List<String> existingNames = candidateRepository.getAllNames();

        if (existingNames.contains(nameOfCandidateToBeCreated)) {
            throw new RuleException("Name already exists!", candidateEntity.getName());
        }
    }

    private void checkCandidateSlotInformation(CandidateSlotEntity candidateSlotEntity) {
        checkIfCandidateExists(candidateSlotEntity);
        checkPeriodOfSlotIsValid(candidateSlotEntity);
    }

    private void checkIfCandidateExists(CandidateSlotEntity candidateSlotEntity) {
        Optional<CandidateEntity> candidateEntity = candidateRepository.findById(
                candidateSlotEntity.getCandidateEntity().getName());

        if (candidateEntity.isEmpty()) {
            throw new RuleException("candidate not found!",
                    candidateSlotEntity.getCandidateEntity().getName());
        }
    }

    private void checkPeriodOfSlotIsValid(CandidateSlotEntity candidateSlotEntity) {
        List<AvailableSlot> availableSlotList = candidateSlotEntity.getAvailableSlotList();

        for (AvailableSlot availableSlot : availableSlotList) {
            List<TimeSlot> timeSlotList = availableSlot.getTimeSlotList();

            for (TimeSlot timeSlot : timeSlotList) {
                LocalTime newTimeSlotStart = timeSlot.getStart();
                LocalTime newTimeSlotEnd = timeSlot.getEnd();

                if (newTimeSlotStart.isAfter(newTimeSlotEnd) || newTimeSlotStart.equals(newTimeSlotEnd)) {
                    throw new RuleException("Start hour of slot must be before end hour of slot!",
                            "From: " + newTimeSlotStart,
                            "To: " + newTimeSlotEnd);
                }

                if (newTimeSlotStart.getMinute() != 0 || newTimeSlotEnd.getMinute() != 0) {
                    throw new RuleException(
                            "Time of period is invalid! The slot must be from the beginning of the hour until the beginning of the next "
                                    + "hour!",
                            "Start: " + newTimeSlotStart, "End: " + newTimeSlotEnd);
                }
            }
        }
    }

    private CandidateSlotEntity checkIfCandidateHasSlotCreated(CandidateSlotEntity candidateSlotEntity) {

        String candidateName = candidateSlotEntity.getCandidateEntity().getName();

        return candidateSlotRepository.getCandidateSlotByCandidateName(candidateName);
    }

    private void addNewSlot(CandidateSlotEntity candidateExistingSlotEntity,
                            CandidateSlotEntity candidateSlotEntity) {
        List<AvailableSlot> existingAvailableSlotList =
                candidateExistingSlotEntity.getAvailableSlotList();

        List<AvailableSlot> newAvailableSlotList = candidateSlotEntity.getAvailableSlotList();

        List<AvailableSlot> remainingNewAvailableSlotList = addNewSlotToExistingDay(
                existingAvailableSlotList, newAvailableSlotList);

        if (!remainingNewAvailableSlotList.isEmpty()) {
            addNewSlotsToNewDay(existingAvailableSlotList, remainingNewAvailableSlotList);
        }
    }

    private List<AvailableSlot> addNewSlotToExistingDay(
            List<AvailableSlot> existingAvailableSlotList, List<AvailableSlot> newAvailableSlotList) {

        List<AvailableSlot> addedSlotsList = new ArrayList<>();

        for (AvailableSlot existingAvailableSlot : existingAvailableSlotList) {
            List<TimeSlot> existingTimeSlotList = existingAvailableSlot.getTimeSlotList();
            LocalDate existingDay = existingAvailableSlot.getDay();

            for (AvailableSlot newAvailableSlot : newAvailableSlotList) {
                List<TimeSlot> newTimeSlotList = newAvailableSlot.getTimeSlotList();
                LocalDate newDay = newAvailableSlot.getDay();

                if (existingDay.isEqual(newDay)) {
                    existingTimeSlotList.addAll(newTimeSlotList);
                    addedSlotsList.add(newAvailableSlot);
                }
            }
        }

        if (!addedSlotsList.isEmpty()) {
            newAvailableSlotList.removeAll(addedSlotsList);
        }

        return newAvailableSlotList;
    }

    private void addNewSlotsToNewDay(List<AvailableSlot> existingAvailableSlotList,
                                            List<AvailableSlot> remainingNewAvailableSlotList) {
        existingAvailableSlotList.addAll(remainingNewAvailableSlotList);
    }
}
