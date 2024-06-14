package com.project.supershop.features.email.repositories;

import com.project.supershop.features.email.domain.entities.Confirmation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation,Long> {
    Confirmation findByToken(String token);
}
