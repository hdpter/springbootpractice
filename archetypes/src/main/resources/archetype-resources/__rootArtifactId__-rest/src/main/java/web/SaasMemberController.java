package ${package}.web;

import ${package}.biz.service.SaasMemberService;
import ${package}.web.param.in.BecomeMemberRestReq;
import ${package}.web.param.out.BecomeMemberVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.NonNull;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @author ${projectAuthor}
 */
@RestController
@RequestMapping(path = "/member")
@Api(value = "会员接口")
public class SaasMemberController {

    private final SaasMemberService saasMemberService;

    public SaasMemberController(SaasMemberService saasMemberService) {
        this.saasMemberService = saasMemberService;
    }

    @PostMapping(path = "/becomeMember")
    @ApiOperation(value = "通知用户成为会员接口", notes = "使用场景: 该用户成为了会员 版本：V1")
    public BecomeMemberVO becomeMember(@RequestBody @NonNull @Valid BecomeMemberRestReq param) {
        final String appId = param.getAppId();
        final String merchantUserId = param.getUserId();
        final Long timestamp = param.getTimestamp();
        final String becomeDesc = param.getBecomeDesc();
        final String checksum = param.getChecksum();
        BecomeMemberVO becomeMemberVO = BecomeMemberVO.builder().build();
        BeanUtils.copyProperties(saasMemberService.becomeMember(appId, merchantUserId, timestamp, becomeDesc, checksum), BecomeMemberVO.builder().build());
        return becomeMemberVO;
    }


}
