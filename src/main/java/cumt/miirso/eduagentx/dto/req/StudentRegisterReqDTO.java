package cumt.miirso.eduagentx.dto.req;

import lombok.Data;

/**
 * @Package cumt.miirso.eduagentx.dto.req
 * @Author miirso
 * @Date 2025/5/29 22:35
 */

public class StudentRegisterReqDTO {

    /**
     * Username
     */
    private String username;


    /**
     * Student Number
     */
    private String studentNo;

    /**
     * Real Name
     */
    private String realName;

    /**
     * Gender (0: Female, 1: Male)
     */
    private Integer gender;

    /**
     * Phone Number
     */
    private String phone;

    /**
     * Email Address
     */
    private String email;

    /**
     * Major Code
     */
    private String majorCode;

    /**
     * Class Code
     */
    private String classCode;

    /**
     * College
     */
    private String college;    /**
     * School
     */
    private String school;

    /**
     * Password
     */
    private String password;    public StudentRegisterReqDTO(String username, String studentNo, String realName, Integer gender, String phone, String email, String majorCode, String classCode, String college, String school, String password) {
        this.username = username;
        this.studentNo = studentNo;
        this.realName = realName;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.majorCode = majorCode;
        this.classCode = classCode;
        this.college = college;
        this.school = school;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getStudentNo() {
        return studentNo;
    }

    public String getRealName() {
        return realName;
    }

    public Integer getGender() {
        return gender;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getMajorCode() {
        return majorCode;
    }

    public String getClassCode() {
        return classCode;
    }

    public String getCollege() {
        return college;
    }

    public String getSchool() {
        return school;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setStudentNo(String studentNo) {
        this.studentNo = studentNo;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMajorCode(String majorCode) {
        this.majorCode = majorCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public void setCollege(String college) {
        this.college = college;
    }    public void setSchool(String school) {
        this.school = school;
    }
    
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
