package com.tamanna.api.interview.services;


import com.tamanna.api.interview.dtos.CandidateSlotDto;
import com.tamanna.api.interview.dtos.InterviewConsultSlotDto;
import com.tamanna.api.interview.dtos.InterviewSlotsResponseDto;
import com.tamanna.api.interview.dtos.InterviewerSlotDto;
import com.tamanna.api.interview.exception.RuleException;
import com.tamanna.api.interview.entities.CandidateEntity;
import com.tamanna.api.interview.entities.CandidateSlotEntity;
import com.tamanna.api.interview.entities.InterviewerEntity;
import com.tamanna.api.interview.entities.InterviewerSlotEntity;
import com.tamanna.api.interview.repositories.CandidateRepository;
import com.tamanna.api.interview.repositories.CandidateSlotRepository;
import com.tamanna.api.interview.repositories.InterviewerRepository;
import com.tamanna.api.interview.repositories.InterviewerSlotRepository;
import com.tamanna.api.interview.utils.AvailableSlot;
import com.tamanna.api.interview.utils.TimeSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class InterviewSlotService {

    @Autowired
    private final CandidateRepository candidateRepository;
    @Autowired
    private final CandidateSlotRepository candidateSlotRepository;
    @Autowired
    private final InterviewerRepository interviewerRepository;
    @Autowired
    private final InterviewerSlotRepository interviewerSlotRepository;


    public InterviewSlotService(CandidateRepository candidateRepository,
                                CandidateSlotRepository candidateSlotRepository,
                                InterviewerRepository interviewerRepository,
                                InterviewerSlotRepository interviewerSlotRepository) {
        this.candidateRepository = candidateRepository;
        this.candidateSlotRepository = candidateSlotRepository;
        this.interviewerRepository = interviewerRepository;
        this.interviewerSlotRepository = interviewerSlotRepository;
    }

    public InterviewSlotsResponseDto getInterviewSlots(InterviewConsultSlotDto interviewConsultSlotDto) {
        
        checkCandidateAndInterviewersExist(interviewConsultSlotDto);

        String candidateName = interviewConsultSlotDto.getCandidateName();
        List<String> interviewersNames = interviewConsultSlotDto.getListInterviewers();

        List<InterviewerSlotDto> interviewAvailableSlots = findInterviewAvailableSlots(interviewConsultSlotDto);

        InterviewSlotsResponseDto interviewSlotsResponseDto = InterviewSlotsResponseDto.builder()
                .candidateName(candidateName)
                .interviewersNames(interviewersNames)
                .interviewerSlotList(interviewAvailableSlots)
                .build();

        return interviewSlotsResponseDto;
    }

    private void checkCandidateAndInterviewersExist(InterviewConsultSlotDto interviewConsultSlotDto) {
        
        String candidateName = interviewConsultSlotDto.getCandidateName();
        List<String> listInterviewers = interviewConsultSlotDto.getListInterviewers();

        checkCandidateExists(candidateName);
        checkInterviewersExist(listInterviewers);
    }

    private void checkCandidateExists(String candidateName) {
        Optional<CandidateEntity> existingCandidate = candidateRepository.findById(candidateName);

        if (existingCandidate.isEmpty()) {
            throw new RuleException("Candidate not found!", candidateName);
        }
    }

    private void checkInterviewersExist(List<String> interviewersNames) {
        for (String interviewerName : interviewersNames) {
            Optional<InterviewerEntity> existingInterviewer = interviewerRepository.findById(interviewerName);

            if (existingInterviewer.isEmpty()) {
                throw new RuleException("Interviewer not found", interviewerName);
            }
        }
    }

    private List<InterviewerSlotDto> findInterviewAvailableSlots(InterviewConsultSlotDto interviewConsultSlotDto) {

        String candidateName = interviewConsultSlotDto.getCandidateName();
        CandidateSlotDto candidateSlot = findCandidateSlot(candidateName);

        List<String> interviewersNames = interviewConsultSlotDto.getListInterviewers();

        List<InterviewerSlotDto> interviewersSlotList = new ArrayList<>();

        for (String interviewerName : interviewersNames) {
            InterviewerSlotDto interviewerSlotEntity = findInterviewerSlot(interviewerName);
            interviewersSlotList.add(interviewerSlotEntity);
        }

        List<InterviewerSlotDto> interviewAvailableSlots =  findCandidateAndInterviewersForInterview(
                        candidateSlot, interviewersSlotList);

        return interviewAvailableSlots;
    }

    private CandidateSlotDto findCandidateSlot(String candidateName) {
        CandidateSlotEntity candidateSlot =
                candidateSlotRepository.getCandidateSlotByCandidateName(candidateName);

        if (candidateSlot == null) {
            throw new RuleException("Candidate has no Slot defined!", candidateName);
        }
        
        CandidateSlotDto candidateSlotDto = CandidateSlotDto.builder()
                .candidateName(candidateName)
                .availableSlotList(candidateSlot.getAvailableSlotList())
                .build();

        return candidateSlotDto;
    }

    private InterviewerSlotDto findInterviewerSlot(String interviewerName) {

        InterviewerSlotEntity interviewerSlot = interviewerSlotRepository
                .getInterviewerSlotByInterviewerName(interviewerName);

        if (interviewerSlot == null) {
            throw new RuleException("Interviewer has no Slot defined!", interviewerName);
        }

        InterviewerSlotDto interviewerSlotDto = InterviewerSlotDto.builder()
                .interviewerName(interviewerName)
                .availableSlotList(interviewerSlot.getAvailableSlotList())
                .build();

        return interviewerSlotDto;
    }

    private List<InterviewerSlotDto> findCandidateAndInterviewersForInterview(CandidateSlotDto candidateSlot,
            List<InterviewerSlotDto> interviewersSlotList) {

        List<AvailableSlot> candidateAvailableSlotList = candidateSlot.getAvailableSlotList();
        List<AvailableSlot> candidateCommonDays = new ArrayList<>();
        List<InterviewerSlotDto> interviewerSlotList = new ArrayList<>();
        String interviewerName = "";

        for (AvailableSlot candidateAvailableSlot : candidateAvailableSlotList) {
            LocalDate candidateSlotDay = candidateAvailableSlot.getDay();

            for (InterviewerSlotDto interviewerSlot : interviewersSlotList) {
                List<AvailableSlot> interviewerCommonDays = new ArrayList<>();
                interviewerName = interviewerSlot.getInterviewerName();
                List<AvailableSlot> interviewerAvailableSlotList = interviewerSlot.getAvailableSlotList();

                for (AvailableSlot interviewerAvailableSlot : interviewerAvailableSlotList) {
                    LocalDate interviewerSlotDay = interviewerAvailableSlot.getDay();

                    if (candidateSlotDay.compareTo(interviewerSlotDay) == 0) {
                        candidateCommonDays.add(candidateAvailableSlot);
                        interviewerCommonDays.add(interviewerAvailableSlot);
                    }
                }
                if (interviewerCommonDays.size() > 0){
                    InterviewerSlotDto interviewerSlotCommonDays = new InterviewerSlotDto();
                    interviewerSlotCommonDays.setInterviewerName(interviewerName);
                    interviewerSlotCommonDays.setAvailableSlotList(interviewerCommonDays);
                    interviewerSlotList.add(interviewerSlotCommonDays);
                }
            }
        }

        List<InterviewerSlotDto> getSlotsCommon = getSlotsCommon(candidateCommonDays, interviewerSlotList);

        return getSlotsCommon;
    }

    private List<InterviewerSlotDto> getSlotsCommon(List<AvailableSlot> candidateCommonDays,
            List<InterviewerSlotDto> interviewerSlotList) {

        List<InterviewerSlotDto> overlappingAvailableSlots = new ArrayList<>();

        for (AvailableSlot candidateAvailableSlot : candidateCommonDays) {
            for (InterviewerSlotDto interviewerSlot: interviewerSlotList) {
                List<AvailableSlot> interviewerCommonDays = interviewerSlot.getAvailableSlotList();
                List<AvailableSlot> availableSlotList = new ArrayList<>();
                for (AvailableSlot interviewerAvailableSlot : interviewerCommonDays) {
                    LocalDate candidateAvailableSlotDay = candidateAvailableSlot.getDay();
                    LocalDate interviewerAvailableSlotDay = interviewerAvailableSlot.getDay();

                    if (candidateAvailableSlotDay.compareTo(interviewerAvailableSlotDay) == 0) {
                        List<TimeSlot> candidateSlotTimeSlots = candidateAvailableSlot.getTimeSlotList();
                        List<TimeSlot> interviewerSlotTimeSlots = interviewerAvailableSlot.getTimeSlotList();

                        List<TimeSlot> overlappingTimeSlots = new ArrayList<>();

                        for (TimeSlot candidateTimeSlot : candidateSlotTimeSlots) {
                            for (TimeSlot interviewerTimeSlot : interviewerSlotTimeSlots) {
                                LocalTime candidateTimeSlotFrom = candidateTimeSlot.getStart();
                                LocalTime candidateTimeSlotTo = candidateTimeSlot.getEnd();
                                LocalTime interviewerTimeSlotFrom = interviewerTimeSlot.getStart();
                                LocalTime interviewerTimeSlotTo = interviewerTimeSlot.getEnd();

                                TimeSlot overlappingTimeSlot = getOverlappingTimeSlot(candidateTimeSlotFrom, candidateTimeSlotTo,
                                        interviewerTimeSlotFrom,
                                        interviewerTimeSlotTo);

                                if (overlappingTimeSlot.getStart() != null && overlappingTimeSlot.getEnd() != null) {
                                    overlappingTimeSlots.add(overlappingTimeSlot);
                                }
                            }
                        }

                        if (!overlappingTimeSlots.isEmpty()) {

                            LocalDate availableSlotDay = interviewerAvailableSlot.getDay();

                            AvailableSlot availableSlot = AvailableSlot.builder()
                                    .day(availableSlotDay)
                                    .timeSlotList(overlappingTimeSlots)
                                    .build();
                            availableSlotList.add(availableSlot);
                            InterviewerSlotDto interviewerSlotCommonDays = new InterviewerSlotDto();
                            interviewerSlotCommonDays.setInterviewerName(interviewerSlot.getInterviewerName());
                            interviewerSlotCommonDays.setAvailableSlotList(availableSlotList);
                            overlappingAvailableSlots.add(interviewerSlotCommonDays);
                        }
                    }
                }

            }
        }

        return overlappingAvailableSlots;
    }

    private TimeSlot getOverlappingTimeSlot(LocalTime candidateFrom, LocalTime candidateTo, LocalTime interviewerFrom,
                                            LocalTime interviewerTo) {
        TimeSlot overlappingTimeSlot = new TimeSlot();
        LocalTime overlapTimeSlotFrom;
        LocalTime overlapTimeSlotTo;

        if (candidateFrom.isBefore(interviewerTo) && interviewerFrom.isBefore(candidateTo)) {
            if (candidateFrom.isBefore(interviewerFrom)) {
                overlapTimeSlotFrom = interviewerFrom;
            } else {
                overlapTimeSlotFrom = candidateFrom;
            }

            if (candidateTo.isBefore(interviewerTo)) {
                overlapTimeSlotTo = candidateTo;
            } else {
                overlapTimeSlotTo = interviewerTo;
            }

            overlappingTimeSlot = TimeSlot.builder()
                    .start(overlapTimeSlotFrom)
                    .end(overlapTimeSlotTo)
                    .build();
        }

        return overlappingTimeSlot;
    }
}
