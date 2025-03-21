package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Controller
@Api(tags = "UploadController", description = "文件上传管理")
@RequestMapping("/upload")
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @ApiOperation(value = "上传图片")
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return CommonResult.failed("上传文件不能为空");
        }

        try {
            // 获取web应用根目录
            String webRootPath = request.getServletContext().getRealPath("");
            // 生成存储路径
            String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String uploadDir = "upload" + File.separator + dateDir;
            String dirPath = webRootPath + File.separator + uploadDir;

            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 保存文件
            File targetFile = new File(dirPath + File.separator + filename);
            file.transferTo(targetFile);

            // 返回文件访问URL（相对路径）
            String fileUrl = request.getContextPath() + "/" + uploadDir + "/" + filename;
            return CommonResult.success(fileUrl);
        } catch (IOException e) {
            LOGGER.error("上传文件失败：", e);
            return CommonResult.failed("上传文件失败");
        }
    }
}