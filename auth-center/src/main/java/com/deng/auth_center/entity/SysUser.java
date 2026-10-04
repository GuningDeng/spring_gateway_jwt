package com.deng.auth_center.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity 
@Table(name = "tbl_sys_user")
@Data 
@NoArgsConstructor 
@AllArgsConstructor 
public class SysUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_user_id")
    private Long id;

    @Column(name = "sys_username")
    private String username;

    @Column(name = "sys_user_password")
    private String password;

    @Column(name = "sys_user_status")
    private Byte status; // 0: active, 1: inactive

    @Column(name = "sys_user_nickname")
    private String nickname; 

    @Column(name = "sys_user_email")
    private String email;

    @Column(name = "sys_user_phone")
    private String phone;

    @Column(name = "sys_user_create_time")
    private LocalDateTime createTime;

    @Column(name = "sys_user_update_time")
    private LocalDateTime updateTime;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "tbl_sys_user_role",
        joinColumns = @JoinColumn(name = "sys_user_id"),
        inverseJoinColumns = @JoinColumn(name = "sys_role_id")
    )
    private List<SysRole> roles;

    
    
}
