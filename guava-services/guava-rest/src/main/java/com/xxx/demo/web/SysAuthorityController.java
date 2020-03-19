package com.xxx.demo.web;
import com.xxx.demo.configurer.core.Result;
import com.xxx.demo.dal.model.SysAuthority;
import com.xxx.demo.biz.service.SysAuthorityService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
* Created by CodeGenerator on 2020/03/19.
*/
@RestController
@RequestMapping("/sys/authority")
public class SysAuthorityController {
    @Resource
    private SysAuthorityService sysAuthorityService;

    @PostMapping("/add")
    public Result add(SysAuthority sysAuthority) {
        sysAuthorityService.save(sysAuthority);
        return Result.genSuccessResult();
    }

    @PostMapping("/delete")
    public Result delete(@RequestParam Integer id) {
        sysAuthorityService.deleteById(id);
        return Result.genSuccessResult();
    }

    @PostMapping("/update")
    public Result update(SysAuthority sysAuthority) {
        sysAuthorityService.update(sysAuthority);
        return Result.genSuccessResult();
    }

    @PostMapping("/detail")
    public Result detail(@RequestParam Integer id) {
        SysAuthority sysAuthority = sysAuthorityService.findById(id);
        return Result.genSuccessResult(sysAuthority);
    }

    @PostMapping("/list")
    public Result list(@RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "0") Integer size) {
        PageHelper.startPage(page, size);
        List<SysAuthority> list = sysAuthorityService.findAll();
        PageInfo pageInfo = new PageInfo(list);
        return Result.genSuccessResult(pageInfo);
    }
}
