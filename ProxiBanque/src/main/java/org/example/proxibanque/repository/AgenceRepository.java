package org.example.proxibanque.repository;

import org.example.proxibanque.entity.Agence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgenceRepository extends JpaRepository<Agence, String> {
}