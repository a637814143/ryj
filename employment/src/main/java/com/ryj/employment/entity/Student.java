package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 学生实体类
 */
@Data
@TableName("student")
public class Student {
    @TableId(type = IdType.AUTO, value = "student_id")
    private Integer studentId; // 学生ID
    
    private String gender; // 性别
    
    @TableField("personal_profile")
    private String personalProfile; // 个人简介
    
    @TableField("full_name")
    private String fullName; // 姓名
    
    private String certificate; // 证书
    
    private String age; // 年龄
    
    private String major; // 专业
    
    @TableField("registered_residence")
    private String registeredResidence; // 户籍地
    
    private String nation; // 民族
    
    private String education; // 学历
    
    @TableField("graduation_school")
    private String graduationSchool; // 毕业学校
    
    @TableField("id_number")
    private String idNumber; // 身份证号
    
    private String awards; // 获奖情况
    
    @TableField("employment_intention")
    private String employmentIntention; // 就业意向
    
    @TableField("social_practice")
    private String socialPractice; // 社会实践
    
    @TableField("examine_state")
    private String examineState; // 审核状态
    
    private Integer recommend; // 智能推荐
    
    @TableField("user_id")
    private Integer userId; // 用户ID
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间
    
    @TableField("update_time")
    private Timestamp updateTime; // 更新时间



    public Integer getStudentId() {
        return studentId;
    }

    public void setStudentId(Integer studentId) {
        this.studentId = studentId;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPersonalProfile() {
        return personalProfile;
    }

    public void setPersonalProfile(String personalProfile) {
        this.personalProfile = personalProfile;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getRegisteredResidence() {
        return registeredResidence;
    }

    public void setRegisteredResidence(String registeredResidence) {
        this.registeredResidence = registeredResidence;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getGraduationSchool() {
        return graduationSchool;
    }

    public void setGraduationSchool(String graduationSchool) {
        this.graduationSchool = graduationSchool;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getAwards() {
        return awards;
    }

    public void setAwards(String awards) {
        this.awards = awards;
    }

    public String getEmploymentIntention() {
        return employmentIntention;
    }

    public void setEmploymentIntention(String employmentIntention) {
        this.employmentIntention = employmentIntention;
    }

    public String getSocialPractice() {
        return socialPractice;
    }

    public void setSocialPractice(String socialPractice) {
        this.socialPractice = socialPractice;
    }

    public String getExamineState() {
        return examineState;
    }

    public void setExamineState(String examineState) {
        this.examineState = examineState;
    }

    public Integer getRecommend() {
        return recommend;
    }

    public void setRecommend(Integer recommend) {
        this.recommend = recommend;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }
}