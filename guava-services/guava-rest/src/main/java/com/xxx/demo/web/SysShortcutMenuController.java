package com.xxx.demo.web;

import com.xxx.demo.configurer.core.Result;
import com.xxx.demo.dal.model.SysShortcutMenu;
import com.xxx.demo.biz.service.SysShortcutMenuService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2020/03/19.
*/
@RestController
@RequestMapping("/sys/shortcut/menu")
public class SysShortcutMenuController {
    @Resource
    private SysShortcutMenuService sysShortcutMenuService;

    @PostMapping
    public Result add(@RequestBody SysShortcutMenu sysShortcutMenu) {
        sysShortcutMenuService.save(sysShortcutMenu);
        return Result.genSuccessResult();
    }

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        sysShortcutMenuService.deleteById(id);
        return Result.genSuccessResult();
    }

    @PutMapping
    public Result update(@RequestBody SysShortcutMenu sysShortcutMenu) {
        sysShortcutMenuService.update(sysShortcutMenu);
        return Result.genSuccessResult();
    }

    @GetMapping("/{id}")
    public Result detail(@PathVariable Integer id) {
        SysShortcutMenu sysShortcutMenu = sysShortcutMenuService.findById(id);
        return Result.genSuccessResult(sysShortcutMenu);
    }

    @GetMapping
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysShortcutMenu> list = sysShortcutMenuService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return Result.genSuccessResult(pageInfo);
    }
}
