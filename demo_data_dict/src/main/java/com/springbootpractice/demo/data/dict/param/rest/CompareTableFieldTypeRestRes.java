package com.springbootpractice.demo.data.dict.param.rest;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author     carter
 * 创建日期:  2020/5/8 18:39
 * 描述:     TODO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("表的字段类型比对结果")
public class CompareTableFieldTypeRestRes implements Serializable {
    private static final long serialVersionUID = 3834535175193231928L;

    @ApiModelProperty("字段名称")
    private String fieldName;

    @ApiModelProperty("mysql的字段类型")
    private String mysqlFieldType;

    @ApiModelProperty("oracle的字段类型")
    private String oracleFieldType;

}
