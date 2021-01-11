package com.sw.vote.web;

import com.sw.vote.cache.ActiveNode;
import com.sw.vote.cache.BackGroundCache;
import com.sw.vote.cache.VoteProjectCache;
import com.sw.vote.model.BackgroundData;
import com.sw.vote.model.LockInfo;
import com.sw.vote.model.entity.VoteProject;
import com.sw.vote.service.VoteProjectSerivce;
import com.sw.vote.util.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Api(value = "投票数据", description = "投票数据", tags = "2")
@RestController
@RequestMapping(path = "/voteProject")
public class VoteProjectController {

    @Autowired
    private VoteProjectSerivce voteProjectSerivce;
    @Autowired
    private VoteProjectCache voteProjectCache;
    @Autowired
    private BackGroundCache backGroundCache;
    @Autowired
    private ActiveNode activeNode;

    @ApiOperation(value = "查询节点状态", notes = "查询节点状态")
    @GetMapping()
    public Map<String, Object> node() {
        return activeNode.info();
    }

    @ApiOperation(value = "查询", notes = "查询")
    @PostMapping(value = "query")
    public List<VoteProject> query() {
        return voteProjectSerivce.query();
    }

    @ApiOperation(value = "查询", notes = "查询")
    @PostMapping(value = "queryLite")
    @ApiImplicitParam(name = "mgr", paramType = "query", value = "类型", dataType = "int")
    public String queryLite(@RequestParam(value = "mgr", required = false) Integer mgr) {
        return voteProjectSerivce.queryLite(mgr);
    }

    @ApiOperation(value = "查询锁定", notes = "查询锁定")
    @PostMapping(value = "getLock")
    public LockInfo getLock() {
        String name = voteProjectCache.getLocked();
        long ttl = StringUtils.isNotEmpty(name) ? voteProjectCache.getLockedTTl() : -2;
        return new LockInfo(name, ttl);
    }

    @ApiOperation(value = "锁定项目", notes = "锁定项目")
    @ApiImplicitParam(name = "projectName", paramType = "query", value = "项目名", dataType = "String")
    @PostMapping(value = "lock")
    public void lock(@RequestParam("projectName") String projectName) {
        voteProjectCache.setLocked(projectName);
    }

    @ApiOperation(value = "拉黑项目", notes = "拉黑项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectName", paramType = "query", value = "项目名", dataType = "String"),
            @ApiImplicitParam(name = "remove", paramType = "query", value = "类型", dataType = "int")
    })
    @PostMapping(value = "drop")
    public void drop(@RequestParam("projectName") String projectName, @RequestParam("remove") int remove) {
        voteProjectCache.setDropped(projectName, remove);
    }

    @ApiOperation(value = "移除项目", notes = "移除项目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "projectName", paramType = "query", value = "项目名", dataType = "String"),
            @ApiImplicitParam(name = "remove", paramType = "query", value = "类型", dataType = "int")
    })
    @PostMapping(value = "remove")
    public void remove(@RequestParam("projectName") String projectName, @RequestParam("remove") int remove) {
        voteProjectCache.setRemoved(projectName, remove);
    }

    @ApiOperation(value = "添加项目", notes = "添加项目")
    @ApiImplicitParam(name = "remove", paramType = "query", value = "类型", dataType = "int")
    @PostMapping(value = "add")
    public void add(@RequestBody VoteProject voteProject, @RequestParam(value = "remove", required = false) Integer type) {
        voteProjectCache.addProject(voteProject, type);
    }

    @ApiOperation(value = "设置策略", notes = "设置策略")
    @PostMapping(value = "setStrategy")
    public void setStrategy(@RequestBody Map<Object, Object> map) {
        voteProjectCache.setStrategy(map);
    }

    @ApiOperation(value = "获取策略", notes = "获取策略")
    @PostMapping(value = "getStrategy")
    public Map<Object, Object> getStrategy() {
        return voteProjectCache.getStrategy();
    }

    @ApiOperation(value = "所有后台信息", notes = "所有后台信息")
    @PostMapping(value = "background")
    public Map<String, BackgroundData> background() {
        return backGroundCache.all();
    }

    @ApiOperation(value = "交换id", notes = "交换id")
    @PostMapping(value = "borrow")
    @ApiImplicitParam(name = "user", paramType = "query", required = true, dataType = "String")
    public String borrow(@RequestParam("user") String user, @RequestBody String url) {
        return voteProjectSerivce.borrow(user, url);
    }

    @ApiOperation(value = "远程获取", notes = "远程获取")
    @PostMapping(value = "corsGet")
    public String corsGet(@RequestBody String url) {
        return voteProjectSerivce.corsGet(url);
    }
}
