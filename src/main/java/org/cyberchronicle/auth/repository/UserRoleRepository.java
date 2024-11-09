package org.cyberchronicle.auth.repository;

import org.cyberchronicle.auth.model.UserRole;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends ListCrudRepository<UserRole, UUID> {
    List<UserRole> findByUserId(Long userId);
}
