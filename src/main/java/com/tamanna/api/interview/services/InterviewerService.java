package com.tamanna.api.interview.services;

import com.tamanna.api.interview.dtos.InterviewerSlotDto;
import com.tamanna.api.interview.dtos.InterviewerDto;
import com.tamanna.api.interview.exception.RuleException;
import com.tamanna.api.interview.entities.InterviewerEntity;
import com.tamanna.api.interview.entities.InterviewerSlotEntity;
import com.tamanna.api.interview.repositories.InterviewerRepository;
import com.tamanna.api.interview.repositories.InterviewerSlotRepository;
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
public class InterviewerService {

    @Autowired
    private final InterviewerRepository interviewerRepository;

    @Autowired
    private final InterviewerSlotRepository interviewerSlotRepository;

    public InterviewerService(InterviewerRepository interviewerRepository, InterviewerSlotRepository interviewerSlotRepository) {
        this.interviewerRepository = interviewerRepository;
        this.interviewerSlotRepository = interviewerSlotRepository;
    }

    public InterviewerEntity createInterviewer(InterviewerDto interviewerDto){

        InterviewerEntity interviewer = InterviewerEntity.builder()
                .name(interviewerDto.getName())
                .build();
        checkInterviewerInformation(interviewer);
        return interviewerRepository.save(interviewer);
    }

    public List<InterviewerEntity> getAllInterviewers(){
        return interviewerRepository.findAll();
    }

    public Optional<InterviewerEntity> getInterviewerByName(String name){
        return interviewerRepository.findById(name);
    }

    public void deleteInterviewerByName(String name){
        interviewerRepository.deleteById(name);
    }

    public InterviewerSlotEntity createInterviewerSlot(InterviewerSlotDto interviewerSlotDto) {

        InterviewerEntity interviewer = InterviewerEntity.builder()
                .name(interviewerSlotDto.getInterviewerName())
                .build();

        InterviewerSlotEntity interviewerSlotEntity = InterviewerSlotEntity.builder()
                .interviewerEntity(interviewer)
                .availableSlotList(interviewerSlotDto.getAvailableSlotList())
                .build();

        checkInterviewerSlotInformation(interviewerSlotEntity);

        InterviewerSlotEntity interviewerExistingSlot = checkIfInterviewerHasSlotCreated(
                interviewerSlotEntity);

        if (interviewerExistingSlot != null) {
            addNewSlot(interviewerExistingSlot, interviewerSlotEntity);
            return interviewerSlotRepository.save(interviewerExistingSlot);
        }

        return interviewerSlotRepository.save(interviewerSlotEntity);
    }

    public List<InterviewerSlotEntity> getAllInterviewersSlot() {
        return interviewerSlotRepository.findAll();
    }

    public InterviewerSlotEntity getInterviewerSlotByName(String name) {
        return interviewerSlotRepository.getInterviewerSlotByInterviewerName(name);
    }

    public void deleteInterviewerSlotByName(String name) {
        Long interviewerAvailabilityIdToBeDeleted =
                interviewerSlotRepository.getInterviewerSlotByInterviewerName(name).getId();

        interviewerSlotRepository.deleteById(interviewerAvailabilityIdToBeDeleted);
    }

    private void checkInterviewerInformation(InterviewerEntity interviewerEntity) {
        checkNameIsFilled(interviewerEntity);
        checkUniqueName(interviewerEntity);
    }

    private void checkNameIsFilled(InterviewerEntity interviewerEntity) {
        String nameOfInterviewerToBeCreated = interviewerEntity.getName();

        if (nameOfInterviewerToBeCreated == null || nameOfInterviewerToBeCreated.isBlank()) {
            throw new RuleException("You must provide a name!",
                    interviewerEntity.getName() != null ? interviewerEntity.getName() : null);
        }
    }

    private void checkUniqueName(InterviewerEntity interviewerEntity) {
        String nameOfInterviewerToBeCreated = interviewerEntity.getName();
        List<String> existingNames = interviewerRepository.getAllNames();

        if (existingNames.contains(nameOfInterviewerToBeCreated)) {
            throw new RuleException("Name already exists!", interviewerEntity.getName());
        }
    }

    private void checkInterviewerSlotInformation(InterviewerSlotEntity interviewerSlotEntity) {
        checkIfInterviewerExists(interviewerSlotEntity);
        checkPeriodOfSlotIsValid(interviewerSlotEntity);
    }

    private void checkIfInterviewerExists(InterviewerSlotEntity interviewerSlotEntity) {
        Optional<InterviewerEntity> interviewerEntity = interviewerRepository.findById(
                interviewerSlotEntity.getInterviewerEntity().getName());

        if (interviewerEntity.isEmpty()) {
            throw new RuleException("Interviewer not found!",
                    interviewerSlotEntity.getInterviewerEntity().getName());
        }
    }

    private void checkPeriodOfSlotIsValid(InterviewerSlotEntity interviewerSlotEntity) {
        List<AvailableSlot> availableSlotList = interviewerSlotEntity.getAvailableSlotList();

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

    private InterviewerSlotEntity checkIfInterviewerHasSlotCreated(InterviewerSlotEntity interviewerSlotEntity) {

        String interviewerName = interviewerSlotEntity.getInterviewerEntity().getName();

        return interviewerSlotRepository.getInterviewerSlotByInterviewerName(interviewerName);
    }

    private void addNewSlot(InterviewerSlotEntity interviewerExistingSlotEntity,
                            InterviewerSlotEntity interviewerSlotEntity) {
        List<AvailableSlot> existingAvailableSlotList =
                interviewerExistingSlotEntity.getAvailableSlotList();

        List<AvailableSlot> newAvailableSlotList = interviewerSlotEntity.getAvailableSlotList();

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
