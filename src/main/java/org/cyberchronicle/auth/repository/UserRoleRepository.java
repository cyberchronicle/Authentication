package org.cyberchronicle.auth.repository;

import org.cyberchronicle.auth.model.UserRole;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface UserRoleRepository extends ListCrudRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
}
