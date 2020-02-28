package ${package}.web.param.in;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import javax.validation.constraints.NotBlank;

import java.io.Serializable;

/**
 * 说明：成为会员的通知请求参数
 * @author ${projectAuthor}
 * 创建时间： 2020年02月06日 12:03 下午
 **/
@Data
@ApiModel("成为会员的通知请求参数")
public class BecomeMemberRestReq implements Serializable {
    private static final long serialVersionUID = 6710935703112603436L;

    @NotBlank(message = "商户appId不能为空")
    @ApiModelProperty(value = "分配给商户的appId", required = true)
    private String appId;

    @NotBlank(message = "商户的客户ID不能为空")
    @ApiModelProperty(value = "商户的客户ID或者用户代码", required = true)
    private String userId;

    @NotBlank(message = "成为会员的描述信息不能为空")
    @ApiModelProperty(value = "成为会员的描述信息，比如购买了什么保险产品", required = true)
    private String becomeDesc;

    @NotBlank(message = "时间戳不能为空")
    @ApiModelProperty(value = "时间戳，毫秒", required = true)
    private Long timestamp;

    @NotBlank(message = "签名不能为空")
    @ApiModelProperty(value = "签名，规则：checksum = sha1Hex(appId + appSecret + timestamp + becomeDesc+userId) , appId,appSecret为saas平台颁发给商户的配置信息", required = true)
    private String checksum;
}
