package ${package}.biz.service.impl;

import ${package}.biz.param.out.BecomeMemberBO;
import ${package}.dal.entity.SaasMemberEntity;
import ${package}.dal.mapper.SaasMemberEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 说明：saas会员业务实现
 * @author ${projectAuthor}
 * 创建时间： 2020年02月06日 12:02 下午
 **/
@Service
@Slf4j
public class SaasMemberServiceImpl implements ${package}.biz.service.SaasMemberService {

    private final SaasMemberEntityMapper saasMemberEntityMapper;

    public SaasMemberServiceImpl(SaasMemberEntityMapper saasMemberEntityMapper) {
        this.saasMemberEntityMapper = saasMemberEntityMapper;
    }


    /**
     * 成为saas会员
     * @param appId
     * @param merchantUserId
     * @param timestamp
     * @param becomeDesc
     * @param checksum
     * @return 会员结果信息
     */
    @Override
    public BecomeMemberBO becomeMember(String appId, String merchantUserId, Long timestamp, String becomeDesc, String checksum) {
        //代码仅做示例，不作为逻辑正确性依据
        final SaasMemberEntity saasMemberEntity = saasMemberEntityMapper.selectByPrimaryKey(1);
        return BecomeMemberBO.builder().build();
    }

}
