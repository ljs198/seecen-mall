package com.seecen.mall.core.admin.controller;

import com.seecen.mall.common.exception.BusinessException;
import com.seecen.mall.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 后台文件上传 Controller
 *
 * 前缀：/api/admin/upload
 * 权限：必须 ROLE_ADMIN
 *
 * 当前阶段只支持图片上传（封面图）。
 * 视频、压缩包等大文件可以复用本控制器的保存逻辑，
 * 但校验规则和返回字段需要扩展。
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/upload")
public class UploadController {

    /** 允许的图片后缀 */
    private static final List<String> ALLOWED_EXTS = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "svg");

    /** 单张图片最大 5MB */
    private static final long MAX_SIZE_BYTES = 5 * 1024 * 1024L;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * 上传一张图片，返回可访问的 URL。
     *
     * 保存路径：${upload.path}/images/yyyy/MM/dd/{uuid}.{ext}
     * 返回 URL：/api/uploads/images/yyyy/MM/dd/{uuid}.{ext}
     *（WebMvcConfig 已映射该 URL → 磁盘目录，前端直接可用）
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        String original = file.getOriginalFilename();
        if (original == null || !original.contains(".")) {
            throw new BusinessException("文件名不合法");
        }

        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        if (!ALLOWED_EXTS.contains(ext)) {
            throw new BusinessException("仅支持图片格式: " + ALLOWED_EXTS);
        }

        if (file.getSize() > MAX_SIZE_BYTES) {
            throw new BusinessException("图片大小不能超过 5MB");
        }

        String dateDir = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String filename = UUID.randomUUID().toString().replace("-", "") + "." + ext;

        Path target = Paths.get(uploadPath, "images", dateDir, filename);
        Files.createDirectories(target.getParent());
        file.transferTo(target.toFile());

        String url = "/api/uploads/images/" + dateDir + "/" + filename;
        log.info("上传图片成功: {} -> {}", original, target);
        return Result.success(url, "上传成功");
    }
}
