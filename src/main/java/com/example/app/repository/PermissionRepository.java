package com.example.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.app.model.Permission;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {
}
