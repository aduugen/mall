package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Controller
@Api(tags = "UploadController", description = "文件上传管理")
@RequestMapping("/upload")
public class UploadController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UploadController.class);

    @Value("${upload.path:F:\\mall_data\\mallbackend\\pics}")
    private String uploadPath;

    @Value("${upload.base-url:http://192.168.1.6:8085/pics}")
    private String baseUrl;

    @ApiOperation(value = "上传图片")
    @RequestMapping(value = "/image", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<String> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            LOGGER.error("上传文件为空");
            return CommonResult.failed("上传文件不能为空");
        }

        try {
            // 记录认证信息
            LOGGER.info("收到上传请求，开始检查认证信息");
            String token = request.getHeader("Authorization");
            LOGGER.info("Authorization token: {}", token);

            // 记录所有请求头，帮助调试
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                LOGGER.info("请求头 {}: {}", headerName, request.getHeader(headerName));
            }

            // 确保路径是绝对路径
            File rootUploadDir = new File(uploadPath);
            if (uploadPath.startsWith("./") || uploadPath.startsWith(".\\")) {
                // 获取当前应用程序的绝对路径
                String userDir = System.getProperty("user.dir");
                LOGGER.info("当前工作目录: {}", userDir);
                rootUploadDir = new File(userDir, uploadPath.substring(2));
            }

            LOGGER.info("使用绝对路径上传目录: {}", rootUploadDir.getAbsolutePath());

            // 生成日期目录
            String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
            File dateDirectory = new File(rootUploadDir, dateDir);

            // 创建完整的目录结构
            if (!dateDirectory.exists()) {
                LOGGER.info("尝试创建目录结构: {}", dateDirectory.getAbsolutePath());
                boolean created = dateDirectory.mkdirs();
                LOGGER.info("创建目录结构结果: {}", created);

                if (!created) {
                    // 尝试创建父目录
                    if (!rootUploadDir.exists()) {
                        boolean rootCreated = rootUploadDir.mkdirs();
                        LOGGER.info("创建根目录结果: {}", rootCreated);
                        if (rootCreated) {
                            // 再次尝试创建日期目录
                            created = dateDirectory.mkdirs();
                            LOGGER.info("二次尝试创建日期目录结果: {}", created);
                        }
                    }

                    if (!dateDirectory.exists()) {
                        LOGGER.error("无法创建目录结构，尝试使用系统临时目录");
                        // 使用系统临时目录作为备选
                        String tempDir = System.getProperty("java.io.tmpdir");
                        rootUploadDir = new File(tempDir, "mall-upload");
                        if (!rootUploadDir.exists()) {
                            rootUploadDir.mkdirs();
                        }
                        dateDirectory = new File(rootUploadDir, dateDir);
                        if (!dateDirectory.exists()) {
                            dateDirectory.mkdirs();
                        }
                        LOGGER.info("临时目录路径: {}", dateDirectory.getAbsolutePath());
                    }
                }
            }

            if (!dateDirectory.exists() || !dateDirectory.canWrite()) {
                LOGGER.error("目录不存在或无写入权限: {}", dateDirectory.getAbsolutePath());
                return CommonResult.failed("服务器存储目录不可用或无写入权限");
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                LOGGER.error("原始文件名为空");
                return CommonResult.failed("无效的文件名");
            }

            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String filename = UUID.randomUUID().toString().replaceAll("-", "") + suffix;

            // 保存文件
            File targetFile = new File(dateDirectory, filename);
            LOGGER.info("保存文件到: {}", targetFile.getAbsolutePath());

            try {
                file.transferTo(targetFile);

                // 检查文件是否确实已保存
                if (!targetFile.exists() || targetFile.length() == 0) {
                    LOGGER.error("文件保存失败或文件大小为0: {}", targetFile.getAbsolutePath());
                    return CommonResult.failed("文件保存失败");
                }

                LOGGER.info("文件成功保存，大小: {} 字节", targetFile.length());
            } catch (IOException e) {
                LOGGER.error("文件写入失败: {}", e.getMessage(), e);
                return CommonResult.failed("文件写入失败: " + e.getMessage());
            }

            // 返回文件访问URL
            String fileUrl = baseUrl + "/" + dateDir + "/" + filename;
            LOGGER.info("生成的文件URL: {}", fileUrl);
            return CommonResult.success(fileUrl);
        } catch (Exception e) {
            LOGGER.error("上传过程中发生异常: {}", e.getMessage(), e);
            return CommonResult.failed("上传文件异常: " + e.getMessage());
        }
    }
}