package com.tamanna.api.interview.services;

import com.tamanna.api.interview.dtos.CandidateDto;
import com.tamanna.api.interview.entities.CandidateEntity;
import com.tamanna.api.interview.exception.RuleException;
import com.tamanna.api.interview.repositories.CandidateRepository;
import com.tamanna.api.interview.repositories.CandidateSlotRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CandidateServiceTest {
    @Mock
    private CandidateRepository candidateRepository;
    @Mock
    private CandidateSlotRepository candidateSlotRepository;

    @InjectMocks
    private CandidateService candidateService;

    @Test
    public void createCandidateWithNameSuccessfully() {
        // Arrange
        String candidateName = "Lara";
        CandidateDto candidate = new CandidateDto();
        candidate.setName(candidateName);
        CandidateEntity candidateEntity = CandidateEntity.builder()
                .name(candidateName)
                        .build();

        // Act
        when(candidateRepository.save(candidateEntity)).thenReturn(candidateEntity);

        CandidateEntity savedCandidate = candidateService.createCandidate(candidate);

        // Assert
        assertNotNull(savedCandidate);
        assertNull(savedCandidate.getCandidateSlotEntity());
        assertEquals(candidateName, savedCandidate.getName());
    }

    @Test(expected = RuleException.class)
    public void createCandidateWithEmptyNameFails() {
        // Arrange
        String candidateName = "";

        CandidateDto candidate = new CandidateDto();
        candidate.setName(candidateName);

        // Act && Assert
        try {
            candidateService.createCandidate(candidate);
        } catch (RuleException e) {
            String exceptionMessage = "You must provide a name!";
            assertEquals(exceptionMessage, e.getMessage());
            throw e;
        }

        fail("Business exception of candidate with empty name was not thrown!");
    }

    @Test
    public void getAllCandidatesSuccessfully() {
        // Arrange
        String candidateName = "Lara";
        CandidateEntity candidateEntity = CandidateEntity.builder()
                .name(candidateName)
                .build();

        List<CandidateEntity> candidatesToBeReturned = Collections.singletonList(candidateEntity);

        // Act
        when(candidateRepository.findAll()).thenReturn(candidatesToBeReturned);

        List<CandidateEntity> candidatesReturned = candidateService.getAllCandidates();

        // Assert
        assertNotNull(candidatesReturned);
        assertEquals(candidatesToBeReturned.size(), candidatesReturned.size());
        assertEquals(candidatesToBeReturned, candidatesReturned);
    }

    @Test
    public void getCandidateByNameSuccessfully() {
        // Arrange
        String candidateName = "Lara";
        CandidateEntity candidateEntity = CandidateEntity.builder()
                .name(candidateName)
                .build();
        // Act
        when(candidateRepository.findById(candidateName)).thenReturn(Optional.of(candidateEntity));

        Optional<CandidateEntity> candidateReturned = candidateService.getCandidateByName(candidateName);

        // Assert
        assertNotNull(candidateReturned);
        assertEquals(candidateName, candidateReturned.get().getName());
        assertEquals(candidateEntity, candidateReturned.get());
    }

    @Test
    public void deleteCandidateByNameSuccessfully() {
        // Arrange
        String candidateName = "Lara";

        // Act
        candidateService.deleteCandidateByName(candidateName);

        // Assert
        verify(candidateRepository, times(1)).deleteById(candidateName);
    }

}
