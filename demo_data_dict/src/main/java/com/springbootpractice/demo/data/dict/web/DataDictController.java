package com.springbootpractice.demo.data.dict.web;

import com.springbootpractice.demo.data.dict.param.rest.ConnectionReqParam;
import com.springbootpractice.demo.data.dict.param.rest.DatabaseListResParam;
import com.springbootpractice.demo.data.dict.param.rest.GenerateDataDictResParam;
import com.springbootpractice.demo.data.dict.service.DataDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;
import java.util.List;

/**
 * 说明：数据字典控制器
 *
 * @author carter
 * 创建时间： 2020年02月03日 3:57 下午
 **/
@RestController
@Api("数据字典生成")
public class DataDictController {

    private final DataDictService dataDictService;

    public DataDictController(DataDictService dataDictService) {
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

}
