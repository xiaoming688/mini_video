package com.mini_video.service.impl;

import com.mini_video.pojo.SearchRecords;
import com.mini_video.repository.SearchRecordsRepository;
import com.mini_video.service.SearchRecordsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("searchRecordsService")
public class SearchRecordsServiceImpl implements SearchRecordsService {

    @Autowired
    private SearchRecordsRepository searchRecordsRepository;

    @Override
    public void insertRecord(SearchRecords s) {
        searchRecordsRepository.save(s);
    }
}
