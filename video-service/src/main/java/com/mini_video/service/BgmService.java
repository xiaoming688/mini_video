package com.mini_video.service;


import com.mini_video.pojo.Bgm;

import java.util.List;

public interface BgmService {
	
	/**
	 * @Description: 查询背景音乐列表
	 */
	public List<Bgm> queryBgmList();
	
	/**
	 * @Description: 根据id查询bgm信息
	 */
	public Bgm queryBgmById(Integer bgmId);
}
