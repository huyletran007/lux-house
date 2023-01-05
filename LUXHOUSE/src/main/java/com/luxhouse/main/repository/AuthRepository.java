package com.luxhouse.main.repository;

import com.luxhouse.main.domain.Authorities;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository extends JpaRepository<Authorities, Long> {
	@Query("SELECT o FROM Authorities o WHERE o.users.id = ?1")
    List<Authorities> selectsByUserId(Long id);
}
