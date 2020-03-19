package com.xxx.demo.web;

import com.xxx.demo.configurer.core.Result;
import com.xxx.demo.dal.model.SysUserAuthority;
import com.xxx.demo.biz.service.SysUserAuthorityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2020/03/19.
*/
@RestController
@RequestMapping("/sys/user/authority")
public class SysUserAuthorityController {
    @Resource
    private SysUserAuthorityService sysUserAuthorityService;

    @PostMapping
    public Result add(@RequestBody SysUserAuthority sysUserAuthority) {
        sysUserAuthorityService.save(sysUserAuthority);
        return Result.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        sysUserAuthorityService.deleteById(id);
        return Result.genSuccessResult();
    }

    @PutMapping
    public Result update(@RequestBody SysUserAuthority sysUserAuthority) {
        sysUserAuthorityService.update(sysUserAuthority);
        return Result.genSuccessResult();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SysUserAuthority sysUserAuthority = sysUserAuthorityService.findById(id);
        return Result.genSuccessResult(sysUserAuthority);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysUserAuthority> list = sysUserAuthorityService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return Result.genSuccessResult(pageInfo);
    }
}
