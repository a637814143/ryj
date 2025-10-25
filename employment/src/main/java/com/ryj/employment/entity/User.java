package com.ryj.employment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO, value = "user_id")
    private Integer userId; // 用户ID
    
    private Integer state; // 账户状态
    
    @TableField("user_group")
    private String userGroup; // 所在用户组
    
    @TableField("login_time")
    private Timestamp loginTime; // 上次登录时间
    
    private String phone; // 手机号码
    
    @TableField("phone_state")
    private Integer phoneState; // 手机认证
    
    private String username; // 用户名
    
    private String nickname; // 昵称
    
    private String password; // 密码
    
    private String email; // 邮箱
    
    @TableField("email_state")
    private Integer emailState; // 邮箱认证
    
    private String avatar; // 头像地址
    
    @TableField("create_time")
    private Timestamp createTime; // 创建时间

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public Timestamp getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Timestamp loginTime) {
        this.loginTime = loginTime;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getPhoneState() {
        return phoneState;
    }

    public void setPhoneState(Integer phoneState) {
        this.phoneState = phoneState;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getEmailState() {
        return emailState;
    }

    public void setEmailState(Integer emailState) {
        this.emailState = emailState;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    // 数据库表中没有update_time字段，注释掉
    // private Timestamp updateTime; // 更新时间
}