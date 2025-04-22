package com.acleda.company.student.administrator.repository;

import com.acleda.company.student.administrator.model.TAppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository("appUserRepository")
public interface AppUserRepository extends JpaRepository<TAppUser, Long>, JpaSpecificationExecutor<TAppUser> {

    @Query("SELECT o FROM TAppUser o WHERE o.username = :username")
    TAppUser findAppUserByUsername(String username);

    @Query("SELECT appUser FROM TAppUser appUser WHERE appUser.id = :id")
    <T> T findAppUserById(@Param("id") Long id, Class<T> clazz);

    @Query("SELECT appUser FROM TAppUser appUser WHERE appUser.id = :id")
    <T> T findAppUserById(@Param("id") Long id);

    @Query("SELECT appUser FROM TAppUser appUser")
    <T> Page<T> findAllAppUsersProjected(Pageable pageable, Class<T> clazz);

    @Query("SELECT o FROM TAppUser o WHERE (o.username = :usernameOrEmail OR o.email = :usernameOrEmail)")
    TAppUser findAppUserByUsernameOrEmail(String usernameOrEmail);

}
