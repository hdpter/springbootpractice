package com.springbootpractice.demo.data.dict.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springbootpractice.demo.data.dict.param.bo.DataDictBo;
import com.springbootpractice.demo.data.dict.param.bo.TableBo;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 说明：业务代码实现类
 * @author carter
 * 创建时间： 2020年02月03日 4:46 下午
 **/
@Service
public class DataDictService {

    private static Connection connection;

    private static final String SQL_SCHEMA = "select SCHEMA_NAME from information_schema.SCHEMATA";
    private static final String SQL_TABLE = "select TABLE_NAME,TABLE_COMMENT,ENGINE from information_schema.TABLES where TABLE_SCHEMA=? ";
    private static final String SQL_DATA_DICT = "select COLUMN_NAME,COLUMN_TYPE,COLUMN_COMMENT,EXTRA,IS_NULLABLE,COLUMN_DEFAULT,TABLE_NAME " +
            "from information_schema.COLUMNS WHERE TABLE_SCHEMA=? " +
            "ORDER BY TABLE_NAME DESC , ORDINAL_POSITION ASC ";

    public List<String> getConnectionDatabaseList(String connectionUrl, String username, String password) {

        List<String> databaseNameList = Lists.newLinkedList();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(connectionUrl, username, password);
            //查询得到所有的数据的名称
            preparedStatement = connection.prepareStatement(SQL_SCHEMA);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                databaseNameList.add(resultSet.getString(1));
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(resultSet)) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (Objects.nonNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return databaseNameList;
    }

    private Map<String, TableBo> getTableBoMap(String databaseName) {
        Map<String, TableBo> tableBoMap = Maps.newHashMap();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //查询得到所有的数据的名称
            preparedStatement = connection.prepareStatement(SQL_TABLE, new int[1]);
            preparedStatement.setString(1, databaseName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                TableBo tableBo = TableBo.builder()
                        .TABLE_NAME(resultSet.getString("TABLE_NAME"))
                        .TABLE_COMMENT(resultSet.getString("TABLE_COMMENT"))
                        .ENGINE(resultSet.getString("ENGINE"))
                        .build();
                tableBoMap.put(tableBo.getTABLE_NAME(), tableBo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(resultSet)) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (Objects.nonNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return tableBoMap;
    }

    public String generateDataDict(String databaseName) {

        Map<String, TableBo> tableBoMap = getTableBoMap(databaseName);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("# ").append(databaseName).append("\n");
        List<DataDictBo> dataDictBoList = Lists.newLinkedList();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //查询得到所有的数据的名称
            preparedStatement = connection.prepareStatement(SQL_DATA_DICT, new int[1]);
            preparedStatement.setString(1, databaseName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                DataDictBo dataDictBo = DataDictBo.builder()
                        .COLUMN_NAME(resultSet.getString("COLUMN_NAME"))
                        .COLUMN_TYPE(resultSet.getString("COLUMN_TYPE"))
                        .COLUMN_COMMENT(resultSet.getString("COLUMN_COMMENT"))
                        .COLUMN_DEFAULT(resultSet.getString("COLUMN_DEFAULT"))
                        .IS_NULLABLE(resultSet.getString("IS_NULLABLE"))
                        .EXTRA(resultSet.getString("EXTRA"))
                        .TABLE_NAME(resultSet.getString("TABLE_NAME"))
                        .build();
                dataDictBoList.add(dataDictBo);
            }

            dataDictBoList.stream().collect(Collectors.groupingBy(DataDictBo::getTABLE_NAME))
                    .forEach((k, v) -> {
                        final TableBo tableBo = tableBoMap.get(k);
                        stringBuilder.append("## ").append(k).append("\n\n");
                        stringBuilder.append("> 业务说明：").append(tableBo.getTABLE_COMMENT());
                        stringBuilder.append("\n存储引擎：").append(tableBo.getENGINE()).append("\n\n");

                        stringBuilder.append("|字段名|字段类型|字段备注|默认值|是否允许为空|其它约束|").append("\n");
                        stringBuilder.append("|-|-|-|-|-|-|").append("\n");

                        v.forEach(column -> stringBuilder.append("|").append(column.getCOLUMN_NAME()).append("|").append(column.getCOLUMN_TYPE()).append("|").append(column.getCOLUMN_COMMENT()).append("|").append(column.getCOLUMN_DEFAULT()).append("|").append(column.getIS_NULLABLE()).append("|").append(column.getEXTRA()).append("|").append("\n"));
                        stringBuilder.append("\n");


                    });

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (Objects.nonNull(resultSet)) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (Objects.nonNull(preparedStatement)) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return stringBuilder.toString();
    }
}
