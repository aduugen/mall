import com.macro.mall.model.PmsComment;
import com.macro.mall.portal.domain.PmsCommentParam;

/**
 * 添加商品评价
 */
@ApiOperation("添加商品评价")
@RequestMapping(value = "/comment/create", method = RequestMethod.POST)
@ResponseBody
public CommonResult createProductComment(@RequestBody PmsCommentParam commentParam) {
    int count = orderService.createProductComment(commentParam);
    if (count > 0) {
        return CommonResult.success(count);
    }
    return CommonResult.failed();
}