package org.jeecg.modules.cable.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.cable.entity.Inventory;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.cable.vo.InventoryListVo;
import org.jeecg.modules.cable.vo.InventoryListsVo;
import org.jeecg.modules.cable.vo.InventoryVo;
import org.jeecg.modules.cable.vo.YikuVo;

import java.util.List;

/**
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface IInventoryService extends IService<Inventory> {

    /**
     * 根据仓库id和库位id查当前库位存放的物品（分页）
     *
     * @param inventoryVo
     * @param page
     * @Author Xm
     * @Date 2020/5/22 15:07
     */
    IPage<InventoryListsVo> InsurancePageList(InventoryListsVo inventoryVo, Page<InventoryListsVo> page);

    List<InventoryListsVo> InsuranceLists(Integer storageLocationId);

    IPage<YikuVo> yikuVoPage(YikuVo yikuVo, Page<YikuVo> page);

    void yikuDel(Integer id);

    void yikuAdd(YikuVo yikuVo);
}
