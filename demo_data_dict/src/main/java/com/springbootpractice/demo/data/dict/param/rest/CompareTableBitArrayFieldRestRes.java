package com.springbootpractice.demo.data.dict.param.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author carter
 * create_date  2020/5/9 10:45
 * description     对比二进制字段结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("对比二进制字段结果")
public class CompareTableBitArrayFieldRestRes implements Serializable {

    private static final long serialVersionUID = 4736565695816129566L;

    @ApiModelProperty("对比结果")
    private String msg;

}
