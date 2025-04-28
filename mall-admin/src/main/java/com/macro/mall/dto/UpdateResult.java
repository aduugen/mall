package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 更新操作结果类
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResult {
    @ApiModelProperty("操作是否成功")
    private boolean success;

    @ApiModelProperty("错误消息")
    private String message;

    @ApiModelProperty("当前版本号")
    private Integer currentVersion;

    public UpdateResult(boolean success) {
        this.success = success;
    }

    public UpdateResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}