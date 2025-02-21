package de.feedpulse.controller;

import de.feedpulse.model.Role;
import de.feedpulse.service.RoleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    private RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    public List<Role> getRoles() {
        System.out.println("getRoles");
        return roleService.getRoles();
    }

}
