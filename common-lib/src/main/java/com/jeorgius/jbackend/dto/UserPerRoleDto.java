package com.jeorgius.jbackend.dto;

import com.jeorgius.jbackend.entities.BaseUserPerRole;
import com.jeorgius.jbackend.enums.Role;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Relation between user and its roles")
public class UserPerRoleDto implements BaseUserPerRole, Serializable {

    @ApiModelProperty("Id")
    private Long id;

    @NotNull
    @ApiModelProperty("Role")
    private Role role;

    @ApiModelProperty("Is superuser")
    private boolean superuser;

}
