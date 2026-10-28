package com.studentportal.repository;
import com.studentportal.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MilestoneRepository extends JpaRepository<Milestone, Long> { List<Milestone> findByProjectIdOrderByDueDateAsc(Long projectId); }
