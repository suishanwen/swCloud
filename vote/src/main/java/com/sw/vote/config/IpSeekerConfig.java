package com.sw.vote.config;

import com.sw.vote.config.props.UserProps;
import com.sw.vote.ip.IPSeeker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IpSeekerConfig {
    @Autowired
    UserProps userProps;

    @Bean
    public IPSeeker ipSeeker() {
        return new IPSeeker(userProps.getIpDatFile());
    }
}
