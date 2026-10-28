package com.studentportal.repository;
import com.studentportal.model.TeamJoinRequest;
import org.springframework.data.jpa.repository.JpaRepository;
public interface JoinRequestRepository extends JpaRepository<TeamJoinRequest, Long> { }
