package com.xxx.demo.web;

import com.xxx.demo.configurer.core.Result;
import com.xxx.demo.dal.model.SysUserMenu;
import com.xxx.demo.biz.service.SysUserMenuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2020/03/19.
*/
@RestController
@RequestMapping("/sys/user/menu")
public class SysUserMenuController {
    @Resource
    private SysUserMenuService sysUserMenuService;

    @PostMapping
    public Result add(@RequestBody SysUserMenu sysUserMenu) {
        sysUserMenuService.save(sysUserMenu);
        return Result.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        sysUserMenuService.deleteById(id);
        return Result.genSuccessResult();
    }

    @PutMapping
    public Result update(@RequestBody SysUserMenu sysUserMenu) {
        sysUserMenuService.update(sysUserMenu);
        return Result.genSuccessResult();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SysUserMenu sysUserMenu = sysUserMenuService.findById(id);
        return Result.genSuccessResult(sysUserMenu);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysUserMenu> list = sysUserMenuService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return Result.genSuccessResult(pageInfo);
    }
}
