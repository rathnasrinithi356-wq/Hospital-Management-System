package com.apex.diagnostics.repository;

import com.apex.diagnostics.entity.LabState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabStateRepository extends JpaRepository<LabState, Long> { }
