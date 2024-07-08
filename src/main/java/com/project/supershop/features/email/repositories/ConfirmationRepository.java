package com.project.supershop.features.email.repositories;

import com.project.supershop.features.email.domain.entities.Confirmation;
import com.project.supershop.features.email.domain.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmationRepository extends JpaRepository<Confirmation, Long> {
    Confirmation findConfirmationByToken(String token);
    Confirmation findConfirmationByEmail(Email email);
    List<Confirmation> findByEmail(Email email);
}
