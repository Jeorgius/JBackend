package com.jeorgius.jbackend.dto;

import com.jeorgius.jbackend.enums.UserStatus;
import com.jeorgius.jbackend.utils.BaseUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "Пользователь")
public class UserDto extends IdDto {

    @NotNull
    @NotBlank
    @ApiModelProperty("Почта пользователя")
    @Pattern(regexp = BaseUtils.EMAIL_REGEXP)
    private String email;

    @NotNull
    @NotBlank
    @ApiModelProperty("Фамилия пользователя")
    private String surname;

    @NotNull
    @NotBlank
    @ApiModelProperty("Имя пользователя")
    private String name;

    @ApiModelProperty("Статус пользователя")
    private UserStatus userStatus;

    @ApiModelProperty("Признак удаленного")
    private boolean deleted = false;

    @ApiModelProperty("Признак системного пользователя")
    private boolean system = false;

}
