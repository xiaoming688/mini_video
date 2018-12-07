package com.mini_video.controller;

import com.mini_video.service.BgmService;
import com.mini_video.utils.MData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/miniVideo/bgm")
public class BgmController {

    @Autowired
    private BgmService bgmService;

    @PostMapping("/list")
    public MData list() {
        MData result = new MData();
        result.put("data", bgmService.queryBgmList());
        return result;
    }

}
