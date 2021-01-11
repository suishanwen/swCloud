package com.sw.vote.service;

import com.sw.vote.mapper.VoteMapper;
import com.sw.vote.middleware.CtrlDeliverSocket;
import com.sw.vote.model.BusinessException;
import com.sw.vote.model.entity.CtrlClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class VoteService {
    private final VoteMapper voteMapper;

    public VoteService(VoteMapper voteMapper) {
        this.voteMapper = voteMapper;
    }


    public List<CtrlClient> queryList(String user) {
        return voteMapper.queryByUser(user);
    }

    public CtrlClient query(String identity) {
        return voteMapper.queryByIdentity(identity);
    }

    public void add(CtrlClient ctrlClient) {
        int count = voteMapper.insert(ctrlClient);
        if (count == 0) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, BusinessException.SERVER_ERROR, "添加失败!");
        }
    }

    public void report(CtrlClient ctrlClient) {
        int count = voteMapper.updateByPrimaryKeySelective(ctrlClient);
        if (count == 0) {
            throw new BusinessException(HttpStatus.INTERNAL_SERVER_ERROR, BusinessException.SERVER_ERROR, "上传失败!");
        } else {
            CtrlDeliverSocket socket = CtrlDeliverSocket.wsClientMap.get(ctrlClient.getIdentity() + "_mobi");
            if (socket != null) {
                try {
                    socket.sendMessage("REFRESH_STATE");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
