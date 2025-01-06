package com.hr_handlers.admin.repository.vacation;

import com.hr_handlers.vacation.entity.Vacation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminVacationRepository extends JpaRepository<Vacation, Long>, AdminVacationCustomRepository {
}
