package com.aaron.service;

import com.aaron.pojo.Bgm;
import com.aaron.pojo.Users;

import java.util.List;

public interface BgmService {


    /**
     * 查询背景音乐列表
     * @return
     */
    public List<Bgm> queryBgmList();

    /**
     * 根据Id查询
     * @param bgmId
     * @return
     */
    public Bgm queryBgmById(String bgmId);



}
