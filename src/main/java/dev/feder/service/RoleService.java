package dev.feder.service;

import dev.feder.model.Role;
import dev.feder.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {

    private RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }


    public List<Role> getRoles() {
        return roleRepository.findAll();
    }
}
