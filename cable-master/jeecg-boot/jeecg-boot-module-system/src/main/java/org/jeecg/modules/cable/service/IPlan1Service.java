package org.jeecg.modules.cable.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cable.entity.Plan1;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.cable.importpackage.Plan1Im;
import org.jeecg.modules.cable.vo.*;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 计划表1
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface IPlan1Service extends IService<Plan1> {
    /**
     * 计划1合并完单
     * 2020/8/27 bai
     *
     * @param plan1Ids          批量完单的计划 id集合
     * @param operatorSchema    完单类型[0:出库\1:入库]
     * @param receiptNo         交接单号
     * @param receiptPhotos     回单照片
     * @param taskTime          任务日期
     * @param completeOrderList 合并完单填写的数据集
     * @return 受影响的行数
     */
    Result<?> consolidationCompleted(List<Serializable> plan1Ids, String operatorSchema, String receiptNo, String receiptPhotos, String taskTime, List<?> completeOrderList);

    /**
     * 查询计划1批量出库完单的数据
     * 2020/8/26 bai
     *
     * @param ids 批量出库完单 ids
     * @return 计划1批量出库完单的数据
     */
    List<Plan1Vo> getPlan1ReceivingStorageList(List<Serializable> ids);

    /**
     * 查询计划1批量入库完单的数据
     * 2020/8/26 bai
     *
     * @param ids 批量入库完单 ids
     * @return 计划1批量入库完单的数据
     */
    List<Plan1> getPlan1DeliverStorage(List<Serializable> ids);

    /**
     * 根据ids集合条件查询
     * 条件分页查询计划1
     *
     * @param plan1Vo
     * @Author Xm
     * @Date 2020/5/27 15:09
     */
    IPage<Plan1> pageList(Plan1Vo plan1Vo, Page<Plan1> page);

    /**
     * 根据ids集合条件查询
     *
     * @param ids liu
     * @Date 2020/7/21
     */
    List<Plan1> idsqueryRuList(List<String> ids);

    /**
     * 根据ids集合条件查询
     *
     * @param ids liu
     * @Date 2020/7/21
     */
    List<SendOrdersVo> idsqueryChuList(List<String> ids);

    /**
     * 查看库位
     *
     * @param storageLocationListVo 用来保存vo
     * @param page                  分页条件
     * @Author Xm
     * @Date 2020/5/15 11:26
     */
    IPage<StorageLocationListVo> StorageLocationListVoPage(StorageLocationListVo storageLocationListVo, Page<StorageLocationListVo> page);

    /**
     * 导出 plan1
     * bai
     * 2020/5/27
     *
     * @return
     */
    List<Plan1Im> exportPlan1(Plan1Im plan1Im, String explain);

    IPage<SettleAccountsVo> selectSettleAccounts(String backup1, String planType, String projectNo, Page<SettleAccountsVo> page);

    IPage<SettleAccountsDetailsVo> selectSettleAccountsDetails(String projectNo, Page<SettleAccountsDetailsVo> page);

    /**
     * 计划表1配变电/线路统计
     *
     * @return
     */
    IPage<Plan1Vo> selectSubstation(String wasteMaterialText, String beginTime, String endTime, String planType, Page<Plan1Vo> page);

    /**
     * 导出变电/导线统计数据
     *
     * @return
     */
    List<Plan1ExcelVo> exportPlan2(Plan1ExcelVo plan1ExcelVo);

    /**
     * 首页线路统计
     * bai 2020/9/17
     *
     * @param vo 查询条件
     * @return 线路统计数据
     */
    List<IndexXLTJVo> getXLTJList(IndexXLTJVo vo);
}
