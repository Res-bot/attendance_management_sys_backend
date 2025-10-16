package com.ams.attendance.repository;

import com.ams.attendance.entity.User;
import com.ams.attendance.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);

    List<User> findByRole(UserRole role); 
    
    boolean existsByEmail(String email);

    Optional<User> findByDepartmentIgnoreCase(String department);

    Optional<User> findByDesignationIgnoreCase(String designation);

    Optional<User> findByDepartmentAndRole(String department, UserRole role);

    Optional<User> findByEmailContainingIgnoreCase(String keyword);

    Optional<User> findByRoleOrderByNameAsc(UserRole role);
}
