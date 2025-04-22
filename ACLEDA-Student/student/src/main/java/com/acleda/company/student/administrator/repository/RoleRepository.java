package com.acleda.company.student.administrator.repository;

import com.acleda.company.student.administrator.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>, JpaSpecificationExecutor<Role> {


    @Query("SELECT role FROM Role role WHERE role.name = :name")
    Role findRoleByName(String name);

}
