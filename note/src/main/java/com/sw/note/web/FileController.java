package com.sw.note.web;

import com.sw.note.service.FileService;
import com.sw.note.util.DateUtil;
import io.swagger.annotations.*;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.UUID;

import com.baidu.aip.ocr.AipOcr;

import java.util.HashMap;

@Api(value = "文件", description = "文件", tags = "3")
@RestController
@RequestMapping(path = "/file")
public class FileController {
    private static final String APP_ID = "16491720";
    private static final String API_KEY = "NDxSBdoXvWBVGS0u0BfKYmNB";
    private static final String SECRET_KEY = "osdLxGFfNkZ443hzRFMnBxIhUMVh6f2u";

    @Autowired
    FileService fileService;

    @ApiOperation(value = "上传文件", notes = "上传文件")
    @ApiImplicitParam(name = "type", paramType = "query", value = "类型", dataType = "int")
    @PostMapping(value = "upload", headers = "content-type=multipart/form-data")
    public String upload(@RequestParam(value = "type", required = false, defaultValue = "0") int type, @ApiParam(value = "文件", required = true) @RequestBody MultipartFile file) {
        return fileService.upload(file, type);
    }


    @ApiOperation(value = "获取统计数", notes = "获取统计数")
    @GetMapping(value = "statics", produces = MediaType.IMAGE_PNG_VALUE)
    public void statistic(HttpServletResponse response) {
        String now = DateUtil.getDate();
        BufferedImage bufferedImage = fileService.statistic(now);
        try {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Etag", UUID.randomUUID().toString());
            response.setHeader("Date", now);
            ImageIO.write(bufferedImage, "PNG", response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "保存trumpTwitter到Pdf", notes = "保存trumpTwitter到Pdf")
    @PostMapping(value = "trumpTwitter")
    public void trumpTwitter() {
        fileService.trumpTwitter();
    }


    @ApiOperation(value = "识别图片中的文字", notes = "识别图片中的文字")
    @PostMapping(value = "imgocr", headers = "content-type=multipart/form-data")
    public String orc(@ApiParam(value = "文件", required = true) @RequestBody MultipartFile file) throws IOException {
        // 初始化一个AipImageClassify
        AipOcr client = new AipOcr(APP_ID, API_KEY, SECRET_KEY);
        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(2000);
        client.setSocketTimeoutInMillis(60000);
        // 调用接口
        JSONObject res = client.basicGeneral(file.getBytes(), new HashMap<>());
        return res.toString(2);
    }
}
