package com.sw.vote.web;

import com.sw.vote.cache.ClientDirectCache;
import com.sw.vote.model.Result;
import com.sw.vote.model.SpeedupInfo;
import com.sw.vote.model.entity.ClientData;
import com.sw.vote.model.entity.ClientDirect;
import com.sw.vote.model.entity.ClientDirectExt;
import com.sw.vote.service.ClientDirectExtService;
import com.sw.vote.service.ClientDirectService;
import com.sw.vote.util.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api(value = "指令", description = "指令", tags = "1")
@RestController
@RequestMapping(path = "/direct")
public class ClientDirectController {

    private final ClientDirectService clientDirectService;
    private final ClientDirectExtService clientDirectExtService;

    public ClientDirectController(ClientDirectService clientDirectService, ClientDirectExtService clientDirectExtService) {
        this.clientDirectService = clientDirectService;
        this.clientDirectExtService = clientDirectExtService;
    }

    @ApiOperation(value = "测试", notes = "测试")
    @GetMapping
    public String test() {
        return ClientDirectCache.hostName;
    }


    @ApiOperation(value = "加载客户端信息", notes = "加载客户端信息")
    @PostMapping(value = "load")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "sortNo", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "version", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "restart", paramType = "query", dataType = "int"),
    })
    public String load(@RequestParam("userId") String userId, @RequestParam("sortNo") Integer sortNo, @RequestParam("version") String version,
                       @RequestParam(value = "restart", required = false, defaultValue = "0") String restart) {
        return clientDirectService.load(userId, sortNo, version, restart);
    }

    @ApiOperation(value = "上报并获取指令", notes = "上报并获取指令")
    @PostMapping(value = "report")
    public String direct(@RequestBody ClientDirect clientDirect) {
        return clientDirectService.direct(clientDirect);
    }

    @ApiOperation(value = "确认指令", notes = "确认指令")
    @PostMapping(value = "confirm")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "sortNo", paramType = "query", required = true, dataType = "int")
    })
    public int confirm(@RequestParam("userId") String userId, @RequestParam("sortNo") Integer sortNo, @RequestBody String direct) {
        return clientDirectService.confirm(userId, sortNo, direct);
    }

    @ApiOperation(value = "通过用户Id检查列表", notes = "通过用户Id检查列表")
    @GetMapping(value = "checkByUserId")
    @ApiImplicitParam(name = "userId", paramType = "query", required = true, dataType = "String")
    public Map<String, ClientDirect> checkByUserId(@RequestParam("userId") String userId) {
        return clientDirectService.checkByUserId(userId);
    }


    @ApiOperation(value = "通过用户Id查看列表", notes = "通过用户Id查看列表")
    @GetMapping(value = "selectByUserIdLite")
    @ApiImplicitParam(name = "userId", paramType = "query", required = true, dataType = "String")
    public String selectByUserIdLite(@RequestParam("userId") String userId) {
        return clientDirectService.selectByUserIdLite(userId);
    }

    @ApiOperation(value = "抓取机器信息", notes = "抓取机器信息")
    @PostMapping(value = "track")
    public String track() {
        return clientDirectService.track();
    }

    @ApiOperation(value = "更新机器信息", notes = "更新机器信息")
    @PostMapping(value = "updateExt")
    public void updateExt(@RequestBody List<ClientDirectExt> clientDirectExtList) {
        clientDirectExtService.updateExt(clientDirectExtList);
    }

    @ApiOperation(value = "通过Id获取位置", notes = "通过Id获取位置")
    @GetMapping(value = "selectLocationById")
    @ApiImplicitParam(name = "id", paramType = "query", required = true, dataType = "String")
    public ClientDirectExt selectLocationById(@RequestParam("id") String id) {
        return clientDirectExtService.selectLocationById(id);
    }

    @ApiOperation(value = "删除", notes = "删除")
    @PostMapping(value = "delete")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "sortNo", paramType = "query", required = true, dataType = "int")
    })
    public void deleteClient(@RequestParam("userId") String userId, @RequestParam("sortNo") Integer sortNo) {
        clientDirectService.deleteClient(userId, sortNo);
    }

    @ApiOperation(value = "指令下发", notes = "指令下发")
    @PostMapping(value = "send")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keys", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "all", paramType = "query", dataType = "int")
    })
    public void updateDirect(@RequestParam("keys") String keys, @RequestParam(value = "all", required = false, defaultValue = "0") Integer all, @RequestBody String direct) {
        clientDirectService.updateDirect(keys, all, direct);
    }

    @ApiOperation(value = "检查版本", notes = "检查版本")
    @GetMapping(value = "checkVersion")
    public long checkVersion() {
        return clientDirectService.checkVersion();
    }

    @ApiOperation(value = "更新最新", notes = "更新最新")
    @PostMapping(value = "upgrade")
    public void upgradeLatest() {
        clientDirectService.upgradeLatest();
    }

    @ApiOperation(value = "上传数据", notes = "上传数据")
    @PostMapping(value = "upload")
    public int dataUpload(@RequestBody ClientData clientData) {
        return clientDirectService.dataUpload(clientData);
    }

    @ApiOperation(value = "获取用户数据日报", notes = "获取用户数据日报")
    @PostMapping(value = "selectDataByUserId")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "date", paramType = "query", required = true, dataType = "String"),
    })
    public String selectDataByUserId(@RequestParam("userId") String userId, @RequestParam("date") String date) {
        return clientDirectService.selectDataByUserId(userId, date);
    }

    @ApiOperation(value = "获取用户数据日报明细", notes = "获取用户数据日报明细")
    @PostMapping(value = "selectDetail")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "date", paramType = "query", required = true, dataType = "String"),
    })
    public String selectDetail(@RequestParam("id") String id, @RequestParam("date") String date) {
        return clientDirectService.selectDetail(id, date);
    }

    @ApiOperation(value = "获取用户月统计数据", notes = "获取用户月统计数据")
    @PostMapping(value = "selectDetails")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "date", paramType = "query", required = true, dataType = "String"),
    })
    public String selectDetails(@RequestParam(value = "id", required = false) String id, @RequestParam("date") String date) {
        if (StringUtils.isNotEmpty(id)) {
            return clientDirectService.selectDetails(id, date);
        } else {
            return clientDirectService.selectMonthDetails(date);

        }
    }

    @ApiOperation(value = "获取用户统计数据排行", notes = "获取用户统计数据排行")
    @PostMapping(value = "selectEliminatedDetails")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "day", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "limit", paramType = "query", required = true, dataType = "int"),
            @ApiImplicitParam(name = "order", paramType = "query", required = true, dataType = "String")
    })
    public String selectEliminatedDetails(@RequestParam(value = "day", defaultValue = "5") int day, @RequestParam(value = "limit", defaultValue = "30") int limit, @RequestParam(value = "order", defaultValue = "ASC") String order) {
        return clientDirectService.selectEliminatedDetails(day, limit, order);
    }

    @ApiOperation(value = "获取活跃客户端数", notes = "获取活跃客户端数")
    @GetMapping(value = "active", produces = MediaType.IMAGE_PNG_VALUE)
    public void activeClient(HttpServletResponse response) {
        String now = DateUtil.getDate();
        BufferedImage bufferedImage = clientDirectService.activeClient(now);
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

    @ApiOperation(value = "续费", notes = "续费")
    @PostMapping(value = "renew")
    public String renew() {
        return clientDirectService.renew();
    }

    @ApiOperation(value = "bugReport", notes = "bugReport")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "msg", paramType = "query", required = true, dataType = "String"),
    })
    @GetMapping(value = "bug")
    public void bugReport(@RequestParam("id") String id, @RequestParam("msg") String msg) {
        clientDirectService.bugReport(id, msg);
    }

    @ApiOperation(value = "提速", notes = "提速")
    @PostMapping(value = "speedup")
    public Result<Object> speedup() {
        return clientDirectService.speedup();
    }

    @ApiOperation(value = "获取提速信息", notes = "获取提速信息")
    @PostMapping(value = "getSpeedupInfo")
    public SpeedupInfo getSpeedupInfo() {
        return clientDirectService.getSpeedupInfo();
    }

    @ApiOperation(value = "分布式锁", notes = "分布式锁")
    @PostMapping(value = "keyLock")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "key", paramType = "query", required = true, dataType = "String"),
            @ApiImplicitParam(name = "ttl", paramType = "query", dataType = "int", defaultValue = "30"),
    })
    public void keyLock(@RequestParam("key") String key, @RequestParam(value = "ttl", required = false, defaultValue = "30") Integer ttl) {
        clientDirectService.keyLock(key, ttl);
    }
}
