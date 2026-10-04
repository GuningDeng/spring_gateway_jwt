package com.deng.auth_center.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table(name = "tbl_sys_role")
@Data 
public class SysRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sys_role_id")
    private Long id;

    @Column(name = "sys_role_code")
    private String code;
    @Column(name = "sys_role_name")
    private String name;
    @Column(name = "sys_role_status")
    private Byte status; // 0: active, 1: inactive
    @Column(name = "sys_role_remark")
    private String remark;
}
