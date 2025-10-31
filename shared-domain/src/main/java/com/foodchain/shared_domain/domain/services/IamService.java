package com.foodchain.shared_domain.domain.services;

import com.foodchain.shared_domain.domain.model.aggregates.UserDetails;

import java.util.Optional;

public interface IamService {
    Optional<UserDetails> validateTokenAndGetUserDetails(String token);
}
