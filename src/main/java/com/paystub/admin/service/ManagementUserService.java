package com.paystub.admin.service;

import com.paystub.admin.repository.AdminMapper;
import com.paystub.comon.util.AESUtilUtil;
import com.paystub.comon.util.ExelTransObjectUtil;
import com.paystub.user.dto.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ManagementUserService {

//    private final UserMapper userMapper;
    private final AESUtilUtil aesUtilUtil;
    private final ExelTransObjectUtil exelTransObjectUtil;
    private final AdminMapper adminMapper;

    public UserDao createUserDto(Row row) {
        Integer EmployeeID = Integer.valueOf(exelTransObjectUtil.getStringValueOrNull(row.getCell(0)));
        String name = exelTransObjectUtil.getStringValueOrNull(row.getCell(1));
        String birthday = exelTransObjectUtil.getStringValueOrNull(row.getCell(2));
        String emailAddress = exelTransObjectUtil.getStringValueOrNull(row.getCell(27));
        String socialNumber = aesUtilUtil.encrypt(birthday);

        return UserDao.builder()
                .EmployeeID(EmployeeID)
                .Name(name)
                .State(2) // 활성화 상태
                .Role(1) // 근무자 역할
                .birthday(birthday)
                .SocialNumber(socialNumber)
                .EmailAddress(emailAddress)
                .build();
    }

    @Transactional
    public void saveUsers(List<UserDao> userDaoList, BindingResult bindingResult) {
        for (UserDao userDao : userDaoList) {
            Optional<UserDao> existingUserWithSameIDAndName =
                    adminMapper.findByEmployeeIDAndName(userDao.getEmployeeID(), userDao.getName());
            Optional<UserDao> existingUserWithSameID =
                    adminMapper.findByEmployeeID(userDao.getEmployeeID());

            if (existingUserWithSameIDAndName.isPresent()) {
//                FieldError error = new FieldError("userDto", "Name",
//                        "[" + userDto.getName() + "]님은 이미 존재해서 회원이 추가되지 않았습니다");
//                bindingResult.addError(error);
            } else if (existingUserWithSameID.isPresent()) {
                FieldError error = new FieldError("userDto", "EmployeeID",
                        " 이미 [" + userDao.getEmployeeID() + "] 사번을 가진 직원이 존재합니다..");
                bindingResult.addError(error);
            } else {
                adminMapper.insertUser(userDao);
            }
        }
    }

    public List<UserDao> getAdminUserForm() {
        return adminMapper.findByAdminUser();
    }

    @Transactional
    public void deleteUsersByIds(List<Long> employeeIds) {
        adminMapper.deleteEmployeeSalaryByIds(employeeIds);
        adminMapper.deleteUsersByIds(employeeIds);
    }

}
