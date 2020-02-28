package ${package}.biz.service;

import ${package}.biz.param.out.BecomeMemberBO;

/**
 * 说明：接口
 * @author ${projectAuthor}
 * 创建时间： 2020年02月26日 2:12 下午
 **/

public interface SaasMemberService {
    /**
     * 成为saas会员
     * @param appId
     * @param merchantUserId
     * @param timestamp
     * @param becomeDesc
     * @param checksum
     * @return 会员结果信息
     */
    BecomeMemberBO becomeMember(String appId, String merchantUserId, Long timestamp, String becomeDesc, String checksum);

}
