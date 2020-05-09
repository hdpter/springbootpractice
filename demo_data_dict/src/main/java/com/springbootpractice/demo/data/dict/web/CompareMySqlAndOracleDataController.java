package com.springbootpractice.demo.data.dict.web;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.springbootpractice.demo.data.dict.param.rest.*;
import com.springbootpractice.demo.data.dict.service.CompareMySqlAndOracleDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author carter
 * 创建日期:  2020/5/8 18:01
 * 描述:     mysql-oracle数据对比控制器
 */
@RestController
@Api("mysql-oracle数据对比控制器")
public class CompareMySqlAndOracleDataController {

    private final CompareMySqlAndOracleDataService compareMySqlAndOracleDataService;

    public CompareMySqlAndOracleDataController(CompareMySqlAndOracleDataService compareMySqlAndOracleDataService) {
        this.compareMySqlAndOracleDataService = compareMySqlAndOracleDataService;
    }


    @PostMapping("/compare/table_count")
    @ApiOperation("对比mysql和oracle的表数量")
    public CompareTableCountRestRes compareTableCount(@NonNull CompareRestReq param) {

        String mysqlDatabase = param.getMysqlDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mysqlDatabase), "mysql的数据库名为空");

        String oracleDatabase = param.getOracleDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(oracleDatabase), "oracle的数据库名为空");

        Set<String> mysqlTableSet = compareMySqlAndOracleDataService.getMysqlTableSet(mysqlDatabase);
        Set<String> oracleTableSet = compareMySqlAndOracleDataService.getOracleTableSet(oracleDatabase);

        long mysqlTableCount = mysqlTableSet.size();
        long oracleTableCount = oracleTableSet.size();
        String msg = mysqlTableCount == oracleTableCount ? "表数量相等" : (mysqlTableCount > oracleTableCount ? "oracle表数量缺少" : "oracle表数量多了");


        return CompareTableCountRestRes.builder()
                .mysqlTableCount(mysqlTableCount)
                .oracleTableCount(oracleTableCount)
                .msg(msg)
                .build();
    }


    //TODO 对比mysql和oracle的表的字段类型
    @PostMapping("/compare/table_field_type")
    @ApiOperation("对比mysql和oracle的表的字段类型")
    public List<CompareTableFieldTypeRestRes> compareTableFieldType(@NonNull CompareRestReq param) {

        String mysqlDatabase = param.getMysqlDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mysqlDatabase), "mysql的数据库名为空");

        String oracleDatabase = param.getOracleDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(oracleDatabase), "oracle的数据库名为空");

        return null;
    }


    @PostMapping("/compare/table_data_count")
    @ApiOperation("对比mysql和oracle的表的数据数量")
    public List<CompareTableDataCountRestRes> compareTableDataCount(@NonNull CompareRestReq param) {

        String mysqlDatabase = param.getMysqlDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mysqlDatabase), "mysql的数据库名为空");

        String oracleDatabase = param.getOracleDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(oracleDatabase), "oracle的数据库名为空");

        return Collections.emptyList();
    }


    @PostMapping("/compare/table_bitarray_field")
    @ApiOperation("对比mysql和oracle的表中的前10条二进制字段数据")
    public CompareTableBitArrayFieldRestRes compareTableBitArrayField(@NonNull CompareRestReq param) {

        String mysqlDatabase = param.getMysqlDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(mysqlDatabase), "mysql的数据库名为空");

        String oracleDatabase = param.getOracleDatabase();
        Preconditions.checkArgument(!Strings.isNullOrEmpty(oracleDatabase), "oracle的数据库名为空");

        return null;
    }


}
