package com.cosain.trilo.auth.domain;

import org.springframework.data.repository.CrudRepository;

public interface TokenRepository extends CrudRepository<Token, String> {

    void deleteTokenBy(String refreshToken);
}
