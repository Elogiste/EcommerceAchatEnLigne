package com.greenpulsespring.greenpulsespring.repos;

import com.greenpulsespring.greenpulsespring.entities.Role;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends CrudRepository<Role, Integer> {

}
