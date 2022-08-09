package com.jeorgius.jbackend.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "DTO that contains ID")
public class IdDto implements Serializable {
    @ApiModelProperty("DTO Id")
    private Long id;

    public IdDto(IdDto slicingDto) {
        this(slicingDto.getId());
    }
}
