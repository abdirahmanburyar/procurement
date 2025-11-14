package com.procurement.auth.mapper;

import com.procurement.auth.dto.RoleDto;
import com.procurement.auth.entity.Role;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class RoleMapper {
    public RoleDto toDto(Role role) {
        RoleDto dto = new RoleDto();
        dto.setId(role.getId());
        dto.setName(role.getName());
        dto.setDescription(role.getDescription());
        dto.setPermissions(role.getPermissions().stream()
                .map(permission -> permission.getName())
                .collect(Collectors.toSet()));
        return dto;
    }
}

