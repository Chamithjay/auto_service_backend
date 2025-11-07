package com.EAD.autoservice_backend.repository;

import com.EAD.autoservice_backend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for Customer entity
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * Find customer by username
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.username) = LOWER(:username)")
    Optional<Customer> findByUsername(@Param("username") String username);

    /**
     * Find customer by email
     */
    @Query("SELECT c FROM Customer c WHERE LOWER(c.email) = LOWER(:email)")
    Optional<Customer> findByEmail(@Param("email") String email);

    /**
     * Check if username exists (excluding specific customer)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c " +
            "WHERE LOWER(c.username) = LOWER(:username) AND c.id != :customerId")
    boolean existsByUsernameAndIdNot(@Param("username") String username, @Param("customerId") Long customerId);

    /**
     * Check if email exists (excluding specific customer)
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Customer c " +
            "WHERE LOWER(c.email) = LOWER(:email) AND c.id != :customerId")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("customerId") Long customerId);
}