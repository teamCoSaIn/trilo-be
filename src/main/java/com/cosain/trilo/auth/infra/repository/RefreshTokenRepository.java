package com.cosain.trilo.auth.infra.repository;

import com.cosain.trilo.auth.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
