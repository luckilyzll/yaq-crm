package org.jeecg.modules.cable.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.cable.entity.Inventory;
import org.jeecg.modules.cable.mapper.InventoryMapper;
import org.jeecg.modules.cable.service.IInventoryService;
import org.jeecg.modules.cable.vo.InventoryListVo;
import org.jeecg.modules.cable.vo.InventoryListsVo;
import org.jeecg.modules.cable.vo.InventoryVo;
import org.jeecg.modules.cable.vo.YikuVo;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Service
public class InventoryServiceImpl extends ServiceImpl<InventoryMapper, Inventory> implements IInventoryService {

    @Override
    public IPage<InventoryListsVo> InsurancePageList(InventoryListsVo inventoryVo, Page<InventoryListsVo> page) {
        List<InventoryListsVo> list = baseMapper.InsurancePageList(inventoryVo, page);
        return page.setRecords(list);
    }


    @Override
    public List<InventoryListsVo> InsuranceLists(Integer storageLocationId) {
        return baseMapper.InsurancePageList(storageLocationId);
    }

    @Override
    public IPage<YikuVo> yikuVoPage(YikuVo yikuVo, Page<YikuVo> page) {
        List<YikuVo> list = baseMapper.yikuVoPage(yikuVo, page);
        return page.setRecords(list);
    }

    @Override
    public void yikuDel(Integer id) {
        baseMapper.yikuDel(id);
    }

    @Override
    public void yikuAdd(YikuVo yikuVo) {
        baseMapper.yikuAdd(yikuVo);
    }
}
