package com.jeorgius.jbackend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@ApiModel(description = "DTO that contains ID and Name")
public class IdNameDto extends IdDto implements IdName<Long> {
    @ApiModelProperty("Dto name")
    private String name;

    public IdNameDto(IdNameDto slicingDto) {
        super(slicingDto);
        setName(slicingDto.getName());
    }

    public IdNameDto(Long id, String name) {
        super(id);
        this.name = name;
    }
}
