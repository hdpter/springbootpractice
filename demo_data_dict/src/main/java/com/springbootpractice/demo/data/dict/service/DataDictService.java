package com.springbootpractice.demo.data.dict.service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.springbootpractice.demo.data.dict.dao.MysqlDao;
import com.springbootpractice.demo.data.dict.param.bo.ColumnBo;
import com.springbootpractice.demo.data.dict.param.bo.ColumnTypeBo;
import com.springbootpractice.demo.data.dict.param.bo.IndexBo;
import com.springbootpractice.demo.data.dict.param.bo.TableBo;
import com.springbootpractice.demo.data.dict.param.datax.DataXConfigBean;
import com.springbootpractice.demo.data.dict.param.datax.Reader;
import com.springbootpractice.demo.data.dict.param.datax.Writer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 说明：业务代码实现类
 *
 * @author carter
 * 创建时间： 2020年02月03日 4:46 下午
 **/
@Service
@Slf4j
public class DataDictService {

    public final MysqlDao mysqlDao;


    public DataDictService(MysqlDao mysqlDao) {
        this.mysqlDao = mysqlDao;
    }


    public List<String> getConnectionDatabaseList(String connectionUrl, String username, String password) {

        mysqlDao.rebuildDataSource(connectionUrl, username, password);
        return mysqlDao.getConnectionDatabaseList();
    }


    public String generateDataDict(String databaseName) {

        Map<String, TableBo> tableBoMap = mysqlDao.getTableBoMap(databaseName);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("# ").append(databaseName).append("\n");
        stringBuilder.append("> ").append(databaseName).append("\n");

        Map<String, List<ColumnBo>> map = mysqlDao.getTableNameDataDictBoListMap(databaseName);
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
                                .speed(DataXConfigBean.Speed.builder().channel(1).build())
                                .build())
                        .content(Lists.newLinkedList())
                        .build())
                .build();

        List<DataXConfigBean.Content> contents = dataXConfigBean.getJob().getContent();

        Map<String, TableBo> tableBoMap = mysqlDao.getTableBoMap(databaseName);

        Map<String, List<ColumnBo>> tableNameDictListMap = mysqlDao.getTableNameDataDictBoListMap(databaseName);

        Connection connection = mysqlDao.getConnection();

        Map<String, AtomicInteger> indexNameMap = new HashMap<>(tableNameDictListMap.size() * 3);

        tableBoMap.forEach((tableName, tableBo) -> {
            List<ColumnBo> columnBos = tableNameDictListMap.get(tableName);

            String querySql = getQuerySql(tableName, columnBos);
            //设置querySql
            Reader reader = Reader.builder()
                    .parameter(Reader.ReaderParameter.builder()
                            .connection(Collections.singletonList(Reader.ReaderConnection.builder()
                                    .querySql(Collections.singletonList(querySql))
                                    .jdbcUrl(Lists.newLinkedList())
                                    .build()))
                            .build())
                    .build();

            List<String> columns = getColumns(columnBos);

            List<String> preSqls = new LinkedList<>();
            List<String> preInitSqls = new LinkedList<>();
            String dropTableSql = "delete from " + tableName;
            preSqls.add(dropTableSql);

            //生成建表，设置备注的sql
            preInitSqls.addAll(getPreSqls(databaseName, tableName, columnBos));

            //生成表相关的索引
            List<String> tableIndexList = getTableIndexList(databaseName, tableName, indexNameMap);
            preInitSqls.addAll(tableIndexList);

            Writer writer = Writer.builder()
                    .parameter(Writer.WriterParameter.builder()
                            .column(columns)
                            .preInitTableSql(preInitSqls)
                            .preSql(preSqls)
                            .connection(Collections.singletonList(Writer.WriteConnection.builder()
                                    .table(Collections.singletonList(tableName))
                                    .build()))
                            .build())
                    .build();

            contents.add(DataXConfigBean.Content.builder().reader(reader).writer(writer).build());
        });

        //清空索引统计信息
        indexNameMap.clear();
        return dataXConfigBean;
    }

    /**
     * @param databaseName
     * @param tableName
     * @param indexNameMap
     * @return
     */
    public List<String> getTableIndexList(String databaseName, String tableName, Map<String, AtomicInteger> indexNameMap) {
        List<String> createTableIndexSqls = new LinkedList<>();

        Map<String, List<IndexBo>> indexNameIndexBoListMap = mysqlDao.getIndexNameIndexBoListMap(databaseName, tableName);

        //找出跟主键索引冲突的一般索引
        Set<String> primaryColumnSet = indexNameIndexBoListMap.entrySet().stream()
                .filter(item -> "primary".equalsIgnoreCase(item.getKey()))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElse(Collections.emptyList())
                .stream().map(IndexBo::getColumnName)
                .collect(Collectors.toSet());

        indexNameIndexBoListMap.forEach((indexName, indexBoList) -> {

            Set<String> columnSet = indexBoList.stream().map(IndexBo::getColumnName).collect(Collectors.toSet());

            if (!"PRIMARY".equalsIgnoreCase(indexName) && !Objects.deepEquals(columnSet, primaryColumnSet)) {
                //create index IDX_BWT_INSTANCEID on BIZ_WORKFLOW_TOKEN (INSTANCEID)


                String indexNameFul = indexName + "_" + getTableAliasName(tableName);

                if (indexNameFul.length() >= 30) {
                    //超长的索引，进行简化
                    indexNameFul = getTableAliasName(indexNameFul) + "_" + getTableAliasName(tableName);
                }

                if (indexNameMap.containsKey(indexNameFul)) {
                    indexNameMap.put(indexNameFul, new AtomicInteger(indexNameMap.get(indexNameFul).incrementAndGet()));
                } else {
                    indexNameMap.put(indexNameFul, new AtomicInteger(1));
                }

                //如果存在同名的了，增加数量编排
                int indexNameCount = indexNameMap.get(indexNameFul).get();
                indexNameFul = indexNameCount > 1 ? (indexNameFul + indexNameCount) : indexNameFul;

                String createTableIndexStringBuilder = "create index " +
                        indexNameFul +
                        " on " + tableName +
                        indexBoList.stream()
                                .sorted(Comparator.comparingInt(IndexBo::getSeqInIndex))
                                .map(IndexBo::getColumnName)
                                .collect(Collectors.joining(",", "(", ")"));
                createTableIndexSqls.add(createTableIndexStringBuilder);
            }


        });

        return createTableIndexSqls;
    }


    private static String getTableAliasName(String tableName) {
        return Arrays.asList(StringUtils.split(tableName, "_")).stream().map(item -> item.substring(0, 1))
                .collect(Collectors.joining(""));

    }

    public static void main(String[] args) {
        System.out.println(getTableAliasName("h_user_guide"));
    }


    //分别放入删除表，新建表 , 表字段备注语句 oracle
    private List<String> getPreSqls(String databaseName, String tableName, List<ColumnBo> columnBos) {
        List<String> columnCommentSqls = new LinkedList<>();

        StringBuilder createTableStringBuilder = new StringBuilder("create table ").append(tableName).append("(");

        List<String> primaryFieldList = Lists.newLinkedList();
        columnBos.forEach(columnBo -> {

            String columnType = columnBo.getCOLUMN_TYPE();
            ColumnTypeBo columnTypeBo = ColumnTypeBo.getColumnTypeBo(columnType);
            String oracleTypeString = ColumnTypeBo.getOracleType(columnTypeBo);

            String columnName = columnBo.getCOLUMN_NAME();

            String columnComment = columnBo.getCOLUMN_COMMENT();
            if (!Strings.isNullOrEmpty(columnComment)) {
                columnCommentSqls.add(String.format("comment on column %s.%s  is '%s'", tableName, columnName, columnComment));
            }


            createTableStringBuilder.append("" + columnName + "")
                    .append(" ").append(oracleTypeString);

            String isNullableStr = columnBo.getIS_NULLABLE();

            String columnDefault = columnBo.getCOLUMN_DEFAULT();

            if (!Strings.isNullOrEmpty(columnDefault)) {

                if (columnDefault.startsWith("b") && columnDefault.contains("'")) {
                    columnDefault = columnDefault.substring(columnDefault.indexOf("'") + 1, columnDefault.lastIndexOf("'"));
                }

                if (oracleTypeString.contains("varchar2") || (oracleTypeString.contains("date") && Character.isDigit(columnDefault.charAt(0)))) {
                    createTableStringBuilder.append(" default '").append(columnDefault).append("' ");
                } else {
                    createTableStringBuilder.append(" default ").append(columnDefault);
                }

            }

            if ("no".equalsIgnoreCase(isNullableStr)) {
                createTableStringBuilder.append(" not null");
            }

            String columnKey = columnBo.getCOLUMN_KEY();
            if ("pri".equalsIgnoreCase(columnKey)) {
                primaryFieldList.add(columnName);
            }

            createTableStringBuilder.append(", ");

        });

        if (!primaryFieldList.isEmpty()) {
            createTableStringBuilder.append(primaryFieldList.stream().collect(Collectors.joining(",", " primary key(", ")")));
        } else {
            createTableStringBuilder
                    .delete(createTableStringBuilder.length() - 2, createTableStringBuilder.length() - 1);
        }
        String createTableSql = createTableStringBuilder
                .append(")")
                .toString();

        List<String> preSqlList = new LinkedList<>();

        preSqlList.add(createTableSql);
        preSqlList.addAll(columnCommentSqls);


        return preSqlList;
    }

    //获取表的字段列表
    private List<String> getColumns(List<ColumnBo> columnBos) {
        return columnBos.stream().map(ColumnBo::getCOLUMN_NAME).collect(Collectors.toList());
    }

    //获取查询的SQL
    private String getQuerySql(String tableName, List<ColumnBo> columnBos) {

        StringBuilder stringBuilder = new StringBuilder("select");

        columnBos.forEach(columnBo -> {
            String columnName = columnBo.getCOLUMN_NAME();
            String columnType = columnBo.getCOLUMN_TYPE();

            ColumnTypeBo columnTypeBo = ColumnTypeBo.getColumnTypeBo(columnType);

            //更多不对应的类型，也在这块处理；
            if ("bit".equalsIgnoreCase(columnTypeBo.getColumnTypeName())) {
                columnName = "if(" + columnName + "=true,1,0) " + columnName;
            }

            stringBuilder.append(" ").append(columnName).append(",");
        });

        return stringBuilder.deleteCharAt(stringBuilder.length() - 1).append(" from ").append(tableName).append(";").toString();
    }
}
