@GetMapping("/streamflow")
public List<StreamflowData> getStreamflowData(@RequestParam String siteId) {
    // 查询径流数据
    List<StreamflowData> streamflowData = streamflowDataService.getAllStreamflowDataBySiteId(siteId);
    return streamflowData;
}