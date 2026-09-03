package com.placelyt.placelyt.repository;

import com.placelyt.placelyt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}