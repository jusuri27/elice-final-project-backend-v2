package com.hr_handlers.employee.service;

import com.hr_handlers.employee.dto.request.EmployeeUpdateRequestDto;
import com.hr_handlers.employee.dto.request.PasswordUpdateRequestDto;
import com.hr_handlers.employee.dto.response.MailResponseDto;
import com.hr_handlers.employee.dto.response.EmpDetailResponseDto;
import com.hr_handlers.employee.dto.response.TeamDetailResponseDto;
import com.hr_handlers.employee.entity.Employee;
import com.hr_handlers.employee.mapper.EmployeeMapper;
import com.hr_handlers.employee.repository.EmployeeRepository;
import com.hr_handlers.global.dto.SuccessResponse;
import com.hr_handlers.global.exception.ErrorCode;
import com.hr_handlers.global.exception.GlobalException;
import com.hr_handlers.global.service.S3Service;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository empRepository;
    private final PasswordEncoder passwordEncoder;
    private final S3Service s3Service;
    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "jsjhyun98@gmail.com";

    // 사원 상세 조회
    public SuccessResponse<EmpDetailResponseDto> getEmpDetail(String empNo){
        Employee employee =  empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));
        return SuccessResponse.of("사원 상세 조회 성공", EmployeeMapper.toEmpDetailResponseDto(employee));
    }

    // 사원 수정
    @Transactional
    public SuccessResponse<Boolean> updateEmpDetail(String empNo, EmployeeUpdateRequestDto requestDto, MultipartFile profileImageFile) throws IOException {
        String profileImageUrl = null;

        // S3에 새 프로필 이미지 업로드
        if (profileImageFile != null && !profileImageFile.isEmpty()) {
            Employee existingEmployee = empRepository.findByEmpNo(empNo)
                    .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));
            // 기존 프로필 이미지 삭제
            if (existingEmployee.getProfileImage() != null && existingEmployee.getProfileImage().getProfileImageUrl() != null) {
                s3Service.deleteFile(existingEmployee.getProfileImage().getProfileImageUrl());
            }
            profileImageUrl = s3Service.uploadFile("user/profile", profileImageFile);
        }

        empRepository.updateEmp(empNo, requestDto, profileImageUrl);
        return SuccessResponse.of("사원 정보 수정", true);
    }

    // 비밀번호 찾기 : 사원 정보 확인
    public SuccessResponse<Boolean> matchEmailAndEmpNo(String empNo, String email) {
        boolean isMatched = empRepository.existsByEmpNoAndEmail(empNo, email);
        if (!isMatched) {
            throw new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND);
        }
        return SuccessResponse.of("사원번호 이메일 일치", true);
    }

    // 임시 비밀번호
    public MailResponseDto sendResetPassword(String empNo, String email) {
        // 사원번호와 이메일 확인
        Employee employee = empRepository.findByEmpNoAndEmail(empNo, email)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 임시 비밀번호 생성
        String tempPassword = generateTempPassword();
        if (tempPassword.isEmpty()) {
            throw new GlobalException(ErrorCode.TEMP_PASSWORD_GENERATION_FAILED);
        }
        // 비밀번호 업데이트
        String encryptedPassword = passwordEncoder.encode(tempPassword);
        employee.updatePassword(encryptedPassword);
        empRepository.save(employee);

        // 메일 데이터 생성
        return MailResponseDto.builder()
                .address(email)
                .title(employee.getName() + "님의 임시 비밀번호 안내 메일입니다.")
                .message("""
                        안녕하세요.

                        임시 비밀번호 안내 메일입니다.

                        임시 비밀번호는 다음과 같습니다:
                        %s

                        로그인 후 반드시 비밀번호를 변경해주세요.
                        """.formatted(tempPassword))
                .build();
    }

    // 메일 전송
    public SuccessResponse<Boolean> sendMail(@Valid MailResponseDto dto) {
        // 메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(dto.getAddress().trim());
        message.setFrom(FROM_ADDRESS);
        message.setSubject(dto.getTitle());
        message.setText(dto.getMessage());

        try {
            mailSender.send(message);
        } catch (MailException e) {
            throw new GlobalException(ErrorCode.EMAIL_SEND_FAILED);
        }

        return SuccessResponse.of("메일 전송 성공", true);
    }

    // 임시 비밀번호 생성
    private String generateTempPassword() {
        int length = 10;
        String charPool = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = (int) (Math.random() * charPool.length());
            password.append(charPool.charAt(randomIndex));
        }
        return password.toString();
    }

    // 비밀번호 변경
    public SuccessResponse<Boolean> updatePassword(String empNo, PasswordUpdateRequestDto requestDto) {
        Employee employee = empRepository.findByEmpNo(empNo)
                .orElseThrow(() -> new GlobalException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 1. 현재 비밀번호 확인
        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), employee.getPassword())) {
            throw new GlobalException(ErrorCode.INVALID_PASSWORD);
        }

        // 2. 새 비밀번호와 확인 비밀번호 비교
        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new GlobalException(ErrorCode.PASSWORD_MISMATCH);
        }

        Employee updatedEmployee = employee.changePassword(passwordEncoder.encode(requestDto.getNewPassword()));
        empRepository.save(updatedEmployee);

        return SuccessResponse.of("비밀번호가 성공적으로 변경되었습니다.", true);
    }

    // 부서가 같은 사원 정보 조회
    public SuccessResponse<List<TeamDetailResponseDto>> getTeamDetail(String empNo) {
        List<TeamDetailResponseDto> teamDetails = empRepository.findTeamMembers(empNo);

        if (teamDetails.isEmpty()) {
            throw new GlobalException(ErrorCode.TEAM_MEMBER_NOT_FOUND);
        }

        return SuccessResponse.of("같은 부서 사원 조회 성공", teamDetails);
    }
}