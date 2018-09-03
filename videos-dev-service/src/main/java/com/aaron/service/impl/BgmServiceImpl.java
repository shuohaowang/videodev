package com.aaron.service.impl;

import com.aaron.mapper.BgmMapper;
import com.aaron.mapper.UsersMapper;
import com.aaron.pojo.Bgm;
import com.aaron.pojo.Users;
import com.aaron.service.BgmService;
import com.aaron.service.UserService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Autowired
    private Sid sid;


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }


    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public Bgm queryBgmById(String bgmId) {
        Bgm bgm = bgmMapper.selectByPrimaryKey(bgmId);
        return bgm;
    }
}
