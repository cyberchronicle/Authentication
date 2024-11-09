package org.cyberchronicle.auth;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends ListCrudRepository<UserRole, Long> {
    List<UserRole> findByUserId(Long userId);
}
