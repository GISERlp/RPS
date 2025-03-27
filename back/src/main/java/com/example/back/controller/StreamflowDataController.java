package com.example.back.controller;

import com.example.back.service.StreamflowDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@CrossOrigin(origins = "*")
public class StreamflowDataController {

    @Autowired
    private StreamflowDataService streamflowDataService;

    @GetMapping("/streamflow")
    public String getStreamflowData(@RequestParam String siteId) {
        // 查询径流数据并返回字符串
        return streamflowDataService.getStreamflowDataBySiteId(siteId);
    }
}