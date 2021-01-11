package com.sw.vote.config.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class UserProps {
    @Value("${vm.superuser:xxx}")
    private String superuser;
    @Value("${vm.manager:xxx}")
    private String manager;
    @Value("${vm.manager1:xxx}")
    private String manager1;
    @Value("${vm.limit:90}")
    private int limit;
    @Value("${vm.host:xxx}")
    private String host;
    @Value("${vm.username:xxx}")
    private String username;
    @Value("${vm.password:xxx}")
    private String password;
    @Value("${vm.delay:15}")
    private int delay;
    @Value("${vm.username1:xxx}")
    private String username1;
    @Value("${vm.password1:xxx}")
    private String password1;
    @Value("${vm.delay1:24}")
    private int delay1;
    @Value("${ttl.lock:600}")
    private int lockTTl;
    @Value("${track.report:true}")
    private boolean trackReport;
    @Value("${track.report-detail:true}")
    private boolean trackReportDetail;
    @Value("${track.fetch:true}")
    private boolean trackFetch;
    @Value("${track.fetch-detail:true}")
    private boolean trackFetchDetail;
    @Value("${error.ignore:false}")
    private boolean errorIgnore;
    @Value("${forbid.concurrent:true}")
    private boolean forbidConcurrent;
    @Value("${ip.dat:/home/vote/qqwry.dat}")
    private String ipDatFile;
    @Value("${speedup.percent:50}")
    private int speedupPercent;
    @Value("${speedup.times:10}")
    private int speedupTimes;
    @Value("${speedup.interval:0.75}")
    private double speedupInterval;
    @Value("${speedup.exclude:北京,美国}")
    private String speedupExclude;

    public String getSuperuser() {
        return superuser;
    }

    public void setSuperuser(String superuser) {
        this.superuser = superuser;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getManager1() {
        return manager1;
    }

    public void setManager1(String manager1) {
        this.manager1 = manager1;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername1() {
        return username1;
    }

    public void setUsername1(String username1) {
        this.username1 = username1;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getIpDatFile() {
        return ipDatFile;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public int getDelay1() {
        return delay1;
    }

    public void setDelay1(int delay1) {
        this.delay1 = delay1;
    }

    public boolean isTrackReport() {
        return trackReport;
    }

    public void setTrackReport(boolean trackReport) {
        this.trackReport = trackReport;
    }

    public boolean isTrackReportDetail() {
        return trackReportDetail;
    }

    public void setTrackReportDetail(boolean trackReportDetail) {
        this.trackReportDetail = trackReportDetail;
    }

    public boolean isTrackFetch() {
        return trackFetch;
    }

    public void setTrackFetch(boolean trackFetch) {
        this.trackFetch = trackFetch;
    }

    public boolean isTrackFetchDetail() {
        return trackFetchDetail;
    }

    public void setTrackFetchDetail(boolean trackFetchDetail) {
        this.trackFetchDetail = trackFetchDetail;
    }

    public void setIpDatFile(String ipDatFile) {
        this.ipDatFile = ipDatFile;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean isErrorIgnore() {
        return errorIgnore;
    }

    public void setErrorIgnore(boolean errorIgnore) {
        this.errorIgnore = errorIgnore;
    }

    public boolean isForbidConcurrent() {
        return forbidConcurrent;
    }

    public void setForbidConcurrent(boolean forbidConcurrent) {
        this.forbidConcurrent = forbidConcurrent;
    }

    public int getLockTTl() {
        return lockTTl;
    }

    public void setLockTTl(int lockTTl) {
        this.lockTTl = lockTTl;
    }

    public int getSpeedupPercent() {
        return speedupPercent;
    }

    public void setSpeedupPercent(int speedupPercent) {
        this.speedupPercent = speedupPercent;
    }

    public int getSpeedupTimes() {
        return speedupTimes;
    }

    public void setSpeedupTimes(int speedupTimes) {
        this.speedupTimes = speedupTimes;
    }

    public double getSpeedupInterval() {
        return speedupInterval;
    }

    public void setSpeedupInterval(double speedupInterval) {
        this.speedupInterval = speedupInterval;
    }


    public String getSpeedupExclude() {
        return speedupExclude;
    }

    public void setSpeedupExclude(String speedupExclude) {
        this.speedupExclude = speedupExclude;
    }
}
