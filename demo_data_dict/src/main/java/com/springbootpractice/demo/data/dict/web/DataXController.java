package com.springbootpractice.demo.data.dict.web;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.springbootpractice.demo.data.dict.param.datax.DataXConfigBean;
import com.springbootpractice.demo.data.dict.param.datax.Reader;
import com.springbootpractice.demo.data.dict.param.datax.Writer;
import com.springbootpractice.demo.data.dict.param.rest.ConnectionReqParam;
import com.springbootpractice.demo.data.dict.param.rest.DataXRestReqVo;
import com.springbootpractice.demo.data.dict.param.rest.DatabaseListResParam;
import com.springbootpractice.demo.data.dict.param.rest.OracleSqlRestRes;
import com.springbootpractice.demo.data.dict.service.DataDictService;
import com.springbootpractice.demo.data.dict.util.JsonUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 说明：dataX配置生成接口
 *
 * @author carter
 * 创建时间： 2020年02月03日 3:57 下午
 **/
@RestController
@Api("DataX配置生成")
@RequestMapping("/datax")
public class DataXController {

    private final DataDictService dataDictService;

    public DataXController(DataDictService dataDictService) {
        this.dataDictService = dataDictService;
    }

    @GetMapping(path = "/index")
    @ApiIgnore
    public ModelAndView index() {
        return new ModelAndView("datax");
    }

    @PostMapping(path = "/testOracleConnection")
    @ApiOperation(value = "测试连接Oracle信息并获取数据库列表")
    public DatabaseListResParam testOracleConnection(@NonNull ConnectionReqParam param) {
        final List<String> connectionDatabaseList = dataDictService.getConnectionDatabaseList(param.getConnectionUrl(), param.getUsername(), param.getPassword());
        return DatabaseListResParam.builder().databaseList(connectionDatabaseList).build();
    }


    @PostMapping(path = "/generateOracleSql")
    @ApiOperation(value = "生成Oracle的建表脚本")
    public OracleSqlRestRes generateOracle(@NonNull DataXRestReqVo param) {
        DataXConfigBean dataXConfigBean = generateDataXJson(param);

        StringBuilder stringBuilder = new StringBuilder();

        dataXConfigBean.getJob().getContent()
                .stream()
                .flatMap(content -> Stream.of(content.getWriter().getParameter().getPreInitTableSql()))
                .forEachOrdered((List<String> sqls) -> sqls.forEach(sql -> stringBuilder.append(sql).append(";\n")));

        return OracleSqlRestRes.builder()
                .oracleSql(stringBuilder.toString())
                .build();

    }


    @PostMapping(path = "/generateDataXJson")
    @ApiOperation(value = "生成DataX的json数据,reader:Mysql,writer:Oracle")
    public DataXConfigBean generateDataXJson(@NonNull DataXRestReqVo param) {

        String mysqlDatabaseName = param.getMysqlDatabaseName();
        Assert.isTrue(StringUtils.isNotBlank(mysqlDatabaseName), "请选择数据库");

        return Optional.ofNullable(dataDictService.generateDataXConfigBean(mysqlDatabaseName))
                .map(item -> {
                    Optional.of(item).map(DataXConfigBean::getJob).map(DataXConfigBean.Job::getContent).orElse(Collections.emptyList())
                            .stream()
                            .map(DataXConfigBean.Content::getReader)
                            .peek(reader -> {
                                reader.setName("mysqlreader");
                                Reader.ReaderParameter readerParameter = reader.getParameter();
                                readerParameter.setUsername(param.getMysqlUsername());
                                readerParameter.setPassword(param.getMysqlPassword());
                                readerParameter.getConnection().forEach(connection -> connection.getJdbcUrl().add(param.getMysqlConnectionUrl()));
                            }).collect(Collectors.toList());

                    return item;
                })
                .map(item -> {

                    Optional.of(item).map(DataXConfigBean::getJob).map(DataXConfigBean.Job::getContent).orElse(Collections.emptyList())
                            .stream()
                            .map(DataXConfigBean.Content::getWriter)
                            .peek(writer -> {

                                writer.setName("oraclewriter");
                                Writer.WriterParameter writerParameter = writer.getParameter();

                                writerParameter.setUsername(param.getOracleUsername());
                                writerParameter.setPassword(param.getOraclePassword());
                                writerParameter.setTruncate("true");
                                writerParameter.setBatchSize("128");

                                writerParameter.getConnection().forEach(connection -> connection.setJdbcUrl(param.getOracleConnectionUrl()));

                            }).collect(Collectors.toList());


                    return item;
                })
                .orElse(null);

    }


    @PostMapping(path = "/generateDataXJsonFile")
    @ApiOperation(value = "生成DataX的json数据的多个文件,reader:Mysql,writer:Oracle")
    public OracleSqlRestRes generateDataXJsonFile(@NonNull DataXRestReqVo param) {

        String dateTimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
        DataXConfigBean dataXConfigBean = generateDataXJson(param);

        String pathName = "/data/datax/" + dateTimeStr;
        File file = new File(pathName);
        if (!file.exists()) {
            file.mkdirs();
        }
        AtomicInteger integer = new AtomicInteger(1);
        dataXConfigBean.getJob().getContent().forEach(content -> {

            DataXConfigBean fileContent = DataXConfigBean.builder()
                    .job(DataXConfigBean.Job.builder()
                            .setting(dataXConfigBean.getJob().getSetting())
                            .content(Collections.singletonList(content))
                            .build())
                    .build();

            String tableName = content.getWriter().getParameter().getConnection().get(0).getTable().get(0);
            File fileToWrite = new File(pathName + "/" + integer.getAndIncrement()+"_"+tableName + ".json");

            try {
                if (!fileToWrite.exists()) {
                    fileToWrite.createNewFile();
                }
                Files.write(JsonUtil.toJson(fileContent).getBytes(), fileToWrite);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });


        return OracleSqlRestRes.builder().oracleSql(pathName).build();

    }


    @GetMapping(path = "/preSql/{databaseName}/{tableName}")
    @ApiOperation(value = "生成oracle的建表和索引语句")
    public Object getOraclePreSql(@PathVariable("tableName") String tableName, @PathVariable("databaseName") String databaseName) {
        return dataDictService.getTableIndexList(databaseName, tableName, Maps.newHashMap());

    }
}
