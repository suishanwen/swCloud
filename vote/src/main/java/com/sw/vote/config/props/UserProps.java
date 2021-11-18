package com.sw.vote.config.props;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
@Setter
@Getter
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
    @Value("${vm.workers:{\"xx\":\"1|2|3\"}}")
    private String workers;
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

}
