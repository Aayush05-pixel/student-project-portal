package com.studentportal.repository;
import com.studentportal.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
public interface TeamRepository extends JpaRepository<Team, Long> { }
