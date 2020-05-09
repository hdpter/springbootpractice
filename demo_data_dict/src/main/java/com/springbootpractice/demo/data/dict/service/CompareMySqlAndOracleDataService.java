package com.springbootpractice.demo.data.dict.service;

import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * carter
 * 创建日期:  2020/5/8 18:13
 * 描述:     对比oracle和mysql的数据业务服务代码
 *
 * @author lifuchun
 */

@Service
public class CompareMySqlAndOracleDataService {

    //TODO 获取mysql的表名称集合
    public Set<String> getMysqlTableSet(String mysqlDatabase) {
        return null;
    }

    //TODO 获取oracle的表名称集合
    public Set<String> getOracleTableSet(String oracleDatabase) {
        return null;
    }
}
