package com.mini_video.service.impl;

import com.mini_video.pojo.Bgm;
import com.mini_video.repository.BgmRepository;
import com.mini_video.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author MM
 * @create 2018-12-07 17:33
 **/
@Service("bgmService")
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmRepository bgmRepository;


    @Override
    public List<Bgm> queryBgmList() {
        return bgmRepository.findAll();
    }

    @Override
    public Bgm queryBgmById(Integer bgmId) {
        return bgmRepository.getOne(bgmId);
    }
}
