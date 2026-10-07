package io.github.mido8989.leadform.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.mido8989.leadform.entity.Lead;

/** Database access for leads. Spring Data writes the implementation; save, findAll and findById come for free. */
public interface LeadRepository extends JpaRepository<Lead, Long> {
}