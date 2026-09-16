package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.CareerPath;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CareerPathRepository
        extends JpaRepository<CareerPath, Long> {

    Optional<CareerPath> findByNameIgnoreCase(String name);
}