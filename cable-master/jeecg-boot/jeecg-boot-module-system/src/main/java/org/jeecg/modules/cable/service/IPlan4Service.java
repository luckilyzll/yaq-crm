package org.jeecg.modules.cable.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cable.entity.Plan1;
import org.jeecg.modules.cable.entity.Plan4;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.cable.importpackage.Plan4Im;
import org.jeecg.modules.cable.vo.Plan4ExcelVo;
import org.jeecg.modules.cable.vo.Plan4Vo;
import org.jeecg.modules.cable.vo.SendOrdersVo;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 计划表4
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface IPlan4Service extends IService<Plan4> {
    /**
     * 计划4合并完单
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
     * 查询计划4批量出库完单的数据
     * 2020/8/28 bai
     *
     * @param ids 批量出库完单 ids
     * @return 计划4批量出库完单的数据
     */
    List<Plan4Vo> getPlan4ReceivingStorageList(List<Serializable> ids);

    /**
     * 查询计划4批量入库完单的数据
     * 2020/8/28 bai
     *
     * @param ids 批量入库完单 ids
     * @return 计划4批量入库完单的数据
     */
    List<Plan4> getPlan4DeliverStorage(List<Serializable> ids);

    /**
     * 根据ids集合条件查询
     *
     * @param ids liu
     * @Date 2020/7/21
     */
    List<Plan4> idsqueryRuList(List<String> ids);

    /**
     * 根据ids集合条件查询
     *
     * @param ids liu
     * @Date 2020/7/21
     */
    List<SendOrdersVo> idsqueryChuList(List<String> ids);

    /**
     * 分页展示计划表4数据
     * bai
     * 2020/5/29
     *
     * @param plan4
     * @return
     */
    IPage<Plan4> pageList(Plan4 plan4, Page<Plan4> page);

    /**
     * 导出计划表4数据
     * bai
     * 2020/5/27
     *
     * @return
     */
    List<Plan4Im> exportPlan4(Plan4 plan4, String explain, String beginTime, String endTime);

    /**
     * 导出计划表4汇总数据
     * bai
     * 2020/5/28
     *
     * @return
     */
    List<Plan4Vo> exportFeedbackSummary(Plan4Vo plan4Vo);

    /**
     * 计划表4电缆统计
     *
     * @return
     */
    IPage<Plan4Vo> selectCable(String voltageGrade, String beginTime, String endTime, String planType, Page<Plan4Vo> page);

    /**
     * 导出电缆统计数据
     *
     * @return
     */
    List<Plan4ExcelVo> exportPlan3(Plan4ExcelVo plan4ExcelVo);
}
