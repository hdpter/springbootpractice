package com.springbootpractice.demo.data.dict.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.springbootpractice.demo.data.dict.param.bo.ColumnTypeBo;
import com.springbootpractice.demo.data.dict.param.bo.DataDictBo;
import com.springbootpractice.demo.data.dict.param.bo.IndexBo;
import com.springbootpractice.demo.data.dict.param.bo.TableBo;
import com.springbootpractice.demo.data.dict.param.datax.DataXConfigBean;
import com.springbootpractice.demo.data.dict.param.datax.Reader;
import com.springbootpractice.demo.data.dict.param.datax.Writer;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 说明：业务代码实现类
 *
 * @author carter
 * 创建时间： 2020年02月03日 4:46 下午
 **/
@Service
public class DataDictService {

    private static Connection connection;

    private static final String SQL_SCHEMA = "select SCHEMA_NAME from information_schema.SCHEMATA";
    private static final String SQL_TABLE = "" +
            "" +
            "";
    private static final String SQL_DATA_DICT = "select COLUMN_NAME,COLUMN_TYPE,COLUMN_KEY,COLUMN_COMMENT,EXTRA,IS_NULLABLE,COLUMN_DEFAULT,TABLE_NAME " +
            "from information_schema.COLUMNS WHERE TABLE_SCHEMA=? " +
            "ORDER BY TABLE_NAME DESC , ORDINAL_POSITION ASC ";

    private static final String SQL_INDEX = "show index from ? ";

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
        stringBuilder.append("> ").append(databaseName).append("\n");

        Map<String, List<DataDictBo>> map = getTableNameDataDictBoListMap(databaseName);
        stringBuilder.append("> 表数量：").append(map.keySet().size()).append("\n");
        map.forEach((k, v) -> {
            final TableBo tableBo = tableBoMap.get(k);
            stringBuilder.append("## ").append(k).append("\n\n");
            stringBuilder.append("> 业务说明：").append(tableBo.getTABLE_COMMENT());
            stringBuilder.append("\n存储引擎：").append(tableBo.getENGINE()).append("\n\n");

            stringBuilder.append("|字段名|字段类型|字段备注|默认值|是否允许为空|其它约束|").append("\n");
            stringBuilder.append("|-|-|-|-|-|-|").append("\n");

            v.forEach(column -> stringBuilder.append("|")
                    .append(column.getCOLUMN_NAME()).append("|")
                    .append(column.getCOLUMN_TYPE()).append("|")
                    .append(column.getCOLUMN_COMMENT()).append("|")
                    .append(column.getCOLUMN_DEFAULT()).append("|")
                    .append(column.getIS_NULLABLE()).append("|")
                    .append(column.getEXTRA()).append("|")
                    .append("\n"));
            stringBuilder.append("\n");


        });
        return stringBuilder.toString();
    }

    private Map<String, List<DataDictBo>> getTableNameDataDictBoListMap(String databaseName) {

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
                        .COLUMN_KEY(resultSet.getString("COLUMN_KEY"))
                        .build();
                dataDictBoList.add(dataDictBo);
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

        return new TreeMap<>(dataDictBoList.stream().collect(Collectors.groupingBy(DataDictBo::getTABLE_NAME)));

    }

    private Map<String, List<IndexBo>> getIndexNameIndexBoListMap(String databaseName, String tableName) {

        List<IndexBo> indexBoList = Lists.newLinkedList();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            //查询得到所有的数据的名称
//            preparedStatement = connection.prepareStatement(SQL_INDEX, new int[1]);
//            preparedStatement.setString(1, databaseName+"."+tableName);
            resultSet =  connection.getMetaData().getIndexInfo(databaseName, databaseName,tableName , false, true);
//            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

//                AtomicInteger i = new AtomicInteger(1);
//                for(;i.get()<12;)
//                {
//                    System.out.print(resultSet.getString(i.getAndIncrement()) +" ="+i.get()+", ");
//                }
//
//                System.out.println("=========");

//                IndexBo dataDictBo = IndexBo.builder()
//                        .Column_name(resultSet.getString("Column_name"))
////                        .Index_comment(resultSet.getString("Index_comment"))
//                        .Key_name(resultSet.getString("Key_name"))
//                        .Non_unique(resultSet.getInt("NON_UNIQUE"))
//                        .Seq_in_index(resultSet.getInt("Seq_in_index"))
//                        .Table(resultSet.getString("TABLE_NAME"))
//                        .build();

                IndexBo dataDictBo = IndexBo.builder()
                        .Column_name(resultSet.getString(9))
                        .Key_name(resultSet.getString(6))
                        .Seq_in_index(resultSet.getInt(8))
                        .Table(resultSet.getString(3))
                        .build();
                indexBoList.add(dataDictBo);
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

        return new TreeMap<>(indexBoList.stream().collect(Collectors.groupingBy(IndexBo::getKey_name)));

    }


    /**
     * 生成datax所需的迁移mysql到oracle的脚本,基于mysql的元素数据信息得到
     *
     * @param databaseName 数据库名
     * @return dataX的迁移实体
     */
    public DataXConfigBean generateDataXConfigBean(String databaseName) {

        DataXConfigBean dataXConfigBean = DataXConfigBean.builder()
                .job(DataXConfigBean.Job.builder()
                        .setting(DataXConfigBean.Setting.builder()
                                .speed(DataXConfigBean.Speed.builder().channel(4).build())
                                .build())

                        .build())
                .build();

        List<DataXConfigBean.Content> contents = dataXConfigBean.getJob().getContent();

        Map<String, TableBo> tableBoMap = getTableBoMap(databaseName);

        Map<String, List<DataDictBo>> tableNameDictListMap = getTableNameDataDictBoListMap(databaseName);

        tableBoMap.forEach((tableName, tableBo) -> {


            List<DataDictBo> dataDictBos = tableNameDictListMap.get(tableName);

            String querySql = getQuerySql(tableName, dataDictBos);
            //设置querySql
            Reader reader = Reader.builder()
                    .parameter(Reader.ReaderParameter.builder()
                            .connection(Collections.singletonList(Reader.ReaderConnection.builder()
                                    .querySql(Collections.singletonList(querySql))
                                    .build()))
                            .build())
                    .build();

            List<String> columns = getColumns(dataDictBos);

            List<String> preSqls = getPreSqls(tableName, dataDictBos);

            //TODO 生成表相关的索引
            preSqls.addAll(getTableIndexList(databaseName, tableName));

            Writer writer = Writer.builder()
                    .parameter(Writer.WriterParameter.builder()
                            .column(columns)
                            .preSql(preSqls)
                            .connection(Collections.singletonList(Writer.WriteConnection.builder()
                                    .table(Collections.singletonList(tableName))
                                    .build()))
                            .build())
                    .build();

            contents.add(DataXConfigBean.Content.builder().reader(reader).writer(writer).build());
        });

        return dataXConfigBean;
    }

    public List<String> getTableIndexList(String databaseName, String tableName) {
        List<String> createTableIndexSqls = new LinkedList<>();

        Map<String, List<IndexBo>> indexNameIndexBoListMap = getIndexNameIndexBoListMap(databaseName, tableName);

        indexNameIndexBoListMap.forEach((indexName, indexBoList) -> {
            if (!"PRIMARY".equalsIgnoreCase(indexName)) {
                //create index IDX_BWT_INSTANCEID on BIZ_WORKFLOW_TOKEN (INSTANCEID)
                StringBuilder createTableIndexStringBuilder = new StringBuilder("create index ")
                        .append(indexName)
                        .append(" on ").append(tableName)
                        .append(indexBoList.stream()
                                .sorted(Comparator.comparingInt(IndexBo::getSeq_in_index))
                                .map(IndexBo::getColumn_name)
                                .collect(Collectors.joining(",", "(", ")")));

                createTableIndexSqls.add(createTableIndexStringBuilder.toString());
            }


        });

       return createTableIndexSqls;
    }

    //TODO 分别放入删除表，新建表，新建索引的语句 oracle
    private List<String> getPreSqls(String tableName, List<DataDictBo> dataDictBos) {

        List<String> columnCommentSqls = new LinkedList<>();

        StringBuilder createTableStringBuilder = new StringBuilder("create table ").append(tableName).append("(");

        dataDictBos.forEach(dataDictBo -> {

            String columnType = dataDictBo.getCOLUMN_TYPE();
            ColumnTypeBo columnTypeBo = ColumnTypeBo.getColumnTypeBo(columnType);
            String oracleTypeString = ColumnTypeBo.getOracleType(columnTypeBo);

            String columnName = dataDictBo.getCOLUMN_NAME();

            String columnComment = dataDictBo.getCOLUMN_COMMENT();
            if (!Strings.isNullOrEmpty(columnComment)) {
                columnCommentSqls.add(String.format("comment on column TABLENAME.COLUMNNAME  is '%s'", columnComment));
            }


            createTableStringBuilder.append(columnName)
                    .append(" ").append(oracleTypeString);

            String isNullableStr = dataDictBo.getIS_NULLABLE();

            if ("no".equalsIgnoreCase(isNullableStr)) {
                createTableStringBuilder.append(" not null");
            }

            String columnKey = dataDictBo.getCOLUMN_KEY();
            if ("pri".equalsIgnoreCase(columnKey)) {
                createTableStringBuilder.append(" primary key ");
            }

            String columnDefault = dataDictBo.getCOLUMN_DEFAULT();

            if (!Strings.isNullOrEmpty(columnDefault)) {
                createTableStringBuilder.append(" default ").append(columnDefault);
            }

            createTableStringBuilder.append(", ");

        });


        String createTableSql = createTableStringBuilder
                .deleteCharAt(createTableStringBuilder.length() - 1)
                .append(")")
                .toString();

        List<String> preSqlList = new LinkedList<>();

        String dropTableSql = "drop table " + tableName;
        preSqlList.add(dropTableSql);
        preSqlList.add(createTableSql);
        preSqlList.addAll(columnCommentSqls);


        return preSqlList;
    }

    //获取表的字段列表
    private List<String> getColumns(List<DataDictBo> dataDictBos) {
        return dataDictBos.stream().map(DataDictBo::getCOLUMN_NAME).collect(Collectors.toList());
    }

    //获取查询的SQL
    private String getQuerySql(String tableName, List<DataDictBo> dataDictBos) {

        StringBuilder stringBuilder = new StringBuilder("select");

        dataDictBos.stream().forEachOrdered(dataDictBo -> {
            String columnName = dataDictBo.getCOLUMN_NAME();
            String columnType = dataDictBo.getCOLUMN_TYPE();

            ColumnTypeBo columnTypeBo = ColumnTypeBo.getColumnTypeBo(columnType);

            //更多不对应的类型，也在这块处理；
            if ("bit".equalsIgnoreCase(columnTypeBo.getColumnTypeName())) {
                columnName = "if(" + columnName + "=true,1,0) " + columnName;
            }

            stringBuilder.append(" ").append(columnName).append(",");
        });

        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).append("from ").append(tableName).append(";").toString();
    }
}
