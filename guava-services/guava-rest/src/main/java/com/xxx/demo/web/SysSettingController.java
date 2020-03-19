package com.xxx.demo.web;

import com.xxx.demo.configurer.core.Result;
import com.xxx.demo.dal.model.SysSetting;
import com.xxx.demo.biz.service.SysSettingService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2020/03/19.
*/
@RestController
@RequestMapping("/sys/setting")
public class SysSettingController {
    @Resource
    private SysSettingService sysSettingService;

    @PostMapping
    public Result add(@RequestBody SysSetting sysSetting) {
        sysSettingService.save(sysSetting);
        return Result.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        sysSettingService.deleteById(id);
        return Result.genSuccessResult();
    }

    @PutMapping
    public Result update(@RequestBody SysSetting sysSetting) {
        sysSettingService.update(sysSetting);
        return Result.genSuccessResult();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SysSetting sysSetting = sysSettingService.findById(id);
        return Result.genSuccessResult(sysSetting);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysSetting> list = sysSettingService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return Result.genSuccessResult(pageInfo);
    }
}
