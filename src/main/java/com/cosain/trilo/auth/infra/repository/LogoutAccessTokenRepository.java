package com.cosain.trilo.auth.infra.repository;

import com.cosain.trilo.auth.domain.LogoutAccessToken;
import org.springframework.data.repository.CrudRepository;

public interface LogoutAccessTokenRepository extends CrudRepository<LogoutAccessToken, String> {
}
