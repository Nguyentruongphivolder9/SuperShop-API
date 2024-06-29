package com.project.supershop.features.email.repositories;

import com.project.supershop.features.email.domain.entities.Confirmation;
import com.project.supershop.features.email.domain.entities.Email;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    Email findEmailByEmailAddress(String emailAddress);
    Email findEmailByConfirmations(Confirmation confirmation);
}
