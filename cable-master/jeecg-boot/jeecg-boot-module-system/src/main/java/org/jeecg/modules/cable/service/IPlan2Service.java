package org.jeecg.modules.cable.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cable.entity.Plan1;
import org.jeecg.modules.cable.entity.Plan2;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.cable.importpackage.Plan2Im;
import org.jeecg.modules.cable.vo.IndexBPTJVo;
import org.jeecg.modules.cable.vo.Plan2Vo;
import org.jeecg.modules.cable.vo.SendOrdersVo;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 计划表2
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface IPlan2Service extends IService<Plan2> {
    /**
     * 计划2合并完单
     * 2020/8/28 bai
     *
     * @param ids               批量完单的计划 id集合
     * @param operatorSchema    完单类型[0:出库\1:入库]
     * @param receiptNo         交接单号
     * @param receiptPhotos     回单照片
     * @param taskTime          任务日期
     * @param completeOrderList 合并完单填写的数据集
     * @return 受影响的行数
     */
    Result<?> consolidationCompleted(List<Serializable> ids, String operatorSchema, String receiptNo, String receiptPhotos, String taskTime, List<?> completeOrderList);

    /**
     * 查询计划2批量出库完单的数据
     * 2020/8/28 bai
     *
     * @param ids 批量出库完单 ids
     * @return 计划2批量出库完单的数据
     */
    List<Plan2Vo> getPlan2ReceivingStorageList(List<Serializable> ids);

    /**
     * 查询计划2批量入库完单的数据
     * 2020/8/28 bai
     *
     * @param ids 批量入库完单 ids
     * @return 计划2批量入库完单的数据
     */
    List<Plan2> getPlan2DeliverStorage(List<Serializable> ids);

    /**
     * 根据ids集合条件查询
     *
     * @param ids liu
     * @Date 2020/7/21
     */
    List<Plan2> idsqueryRuList(List<String> ids);

    /**
     * 根据ids集合条件查询
     *
     * @param ids liu
     * @Date 2020/7/21
     */
    List<SendOrdersVo> idsqueryChuList(List<String> ids);

    /**
     * 分页展示计划表2数据
     * bai
     * 2020/5/29
     *
     * @return
     */
    IPage<Plan2> pageList(Plan2 plan2, Page<Plan2> page);

    /**
     * 导出 plan2
     * bai
     * 2020/5/27
     *
     * @return
     */
    List<Plan2Im> exportPlan2(Plan2 plan2, String explain);

    /**
     * 首页备品统计物料出入库数量
     * bai 2020/9/16
     *
     * @param vo 查询条件
     * @return 备品统计物料出入库数量
     */
    List<IndexBPTJVo> getBPTJList(IndexBPTJVo vo);
}
