package com.springbootpractice.demo.data.dict.web;

import com.springbootpractice.demo.data.dict.param.datax.DataXConfigBean;
import com.springbootpractice.demo.data.dict.param.datax.Reader;
import com.springbootpractice.demo.data.dict.param.datax.Writer;
import com.springbootpractice.demo.data.dict.param.rest.ConnectionReqParam;
import com.springbootpractice.demo.data.dict.param.rest.DataXRestReqVo;
import com.springbootpractice.demo.data.dict.param.rest.DatabaseListResParam;
import com.springbootpractice.demo.data.dict.param.rest.GenerateDataDictResParam;
import com.springbootpractice.demo.data.dict.service.DataDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 说明：页面控制
 *
 * @author carter
 * 创建时间： 2020年02月03日 3:57 下午
 **/
@RestController
@Api("数据字典生成")
public class IndexController {

    private final DataDictService dataDictService;

    public IndexController(DataDictService dataDictService) {
        this.dataDictService = dataDictService;
    }

    @GetMapping(path = "/index")
    @ApiIgnore
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @PostMapping(path = "/testConnection")
    @ApiOperation(value = "测试连接信息并获取数据库列表")
    public DatabaseListResParam testConnection(@NonNull ConnectionReqParam param) {
        final List<String> connectionDatabaseList = dataDictService.getConnectionDatabaseList(param.getConnectionUrl(), param.getUsername(), param.getPassword());
        return DatabaseListResParam.builder().databaseList(connectionDatabaseList).build();
    }

    @PostMapping(path = "/generateDataDict")
    @ApiOperation(value = "生成数据字典")
    public GenerateDataDictResParam generateDataDict(@NonNull String databaseName) {
        Assert.isTrue(StringUtils.isNotBlank(databaseName), "请选择数据库");
        StringBuilder stringBuilder = new StringBuilder();
        Arrays.stream(StringUtils.split(databaseName, ","))
                .filter(StringUtils::isNotBlank)
                .forEach(dbName -> {
                    final String markdownContent = dataDictService.generateDataDict(dbName);
                    stringBuilder.append(markdownContent).append("\n");
                });

        return GenerateDataDictResParam.builder().markdownContent(stringBuilder.toString()).build();
    }

    @PostMapping(path = "/generateDataXJson")
    @ApiOperation(value = "生成DataX的json数据")
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


    @GetMapping(path = "/index/{databaseName}/{tableName}")
    @ApiOperation(value = "生成DataX的json数据")
    public Object getIndexInfo(@PathVariable("tableName") String tableName,@PathVariable("databaseName") String databaseName){

      return   dataDictService.getTableIndexList(databaseName,tableName);

    }
}
