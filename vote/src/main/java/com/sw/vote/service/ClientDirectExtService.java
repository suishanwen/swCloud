package com.sw.vote.service;

import com.sw.vote.cache.ClientDirectCache;
import com.sw.vote.config.props.UserProps;
import com.sw.vote.mapper.ClientDataMapper;
import com.sw.vote.mapper.ClientDirectExtMapper;
import com.sw.vote.model.entity.ClientDirect;
import com.sw.vote.model.entity.ClientDirectExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientDirectExtService {
    @Autowired
    ClientDirectCache clientDirectCache;
    @Autowired
    ClientDirectExtMapper clientDirectExtMapper;
    @Autowired
    ClientDataMapper clientDataMapper;
    @Autowired
    UserProps userProps;

    public void updateExt(List<ClientDirectExt> clientDirectExtList) {
        clientDirectCache.batchUpdateExt(clientDirectExtList);
        clientDirectExtMapper.batchUpdate(clientDirectExtList);
    }

    public ClientDirectExt selectLocationById(String id) {
        String location = clientDataMapper.selectLocationById(id);
        ClientDirectExt clientDirectExt = clientDirectExtMapper.selectByPrimaryKey(id);
        clientDirectExt.setLocation(location);
        return clientDirectExt;
    }

}
