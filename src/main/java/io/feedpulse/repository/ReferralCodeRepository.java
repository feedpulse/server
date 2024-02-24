package io.feedpulse.repository;

import io.feedpulse.model.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Integer> {

    Optional<ReferralCode> findByCode(String code);

    Collection<ReferralCode> findByIsUsedIsTrue();
}
