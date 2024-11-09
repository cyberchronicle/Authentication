package org.cyberchronicle.auth.repository;

import org.cyberchronicle.auth.model.RefreshToken;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
}
