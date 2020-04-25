package com.alto.repository;

import com.alto.model.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Candidate findCandidateByFilekey(String filekey);

}

