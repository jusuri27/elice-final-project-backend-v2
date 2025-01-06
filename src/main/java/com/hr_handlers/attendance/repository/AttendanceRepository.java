package com.hr_handlers.attendance.repository;

import com.hr_handlers.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long>, AttendanceCustomRepository {
}
