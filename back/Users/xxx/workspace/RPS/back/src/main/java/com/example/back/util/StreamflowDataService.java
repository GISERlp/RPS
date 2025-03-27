public List<StreamflowData> getAllStreamflowDataBySiteId(String siteId) {
    // 实现根据siteId查询所有径流数据的逻辑
    // 这里假设有一个名为streamflowDataRepository的仓库类用于数据库操作
    return streamflowDataRepository.findBySiteId(siteId);
}