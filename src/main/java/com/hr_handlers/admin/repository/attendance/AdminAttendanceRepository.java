package com.hr_handlers.admin.repository.attendance;

import com.hr_handlers.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminAttendanceRepository extends JpaRepository<Attendance, Long>, AdminAttendanceCustomRepository {
}
