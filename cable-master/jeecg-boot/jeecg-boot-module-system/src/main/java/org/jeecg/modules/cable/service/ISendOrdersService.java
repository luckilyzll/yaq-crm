package org.jeecg.modules.cable.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cable.entity.SendOrders;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.cable.entity.SendOrdersSubtabulation;
import org.jeecg.modules.cable.entity.Vehicle;
import org.jeecg.modules.cable.vo.*;
import org.jeecg.modules.demo.test.entity.JeecgOrderCustomer;
import org.jeecg.modules.demo.test.entity.JeecgOrderMain;
import org.jeecg.modules.demo.test.entity.JeecgOrderTicket;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Description: 派单表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface ISendOrdersService extends IService<SendOrders> {

    /**
     * 派单编辑操作
     */
    public void MergePlanEdit(SendOrdersVo sendOrdersVo, List<SendOrdersVo> sendOrdersList, List<SendOrdersTaskVo> vehicleList);


    /**
     * 合并派单操作
     */
    public void saveMain(SendOrdersVo sendOrdersVo, List<SendOrdersVo> sendOrdersList, List<SendOrdersTaskVo> vehicleList);

    /**
     * 根据年份和月份查询当月每日的车辆任务的数量
     *
     * @Author Xm
     * @Date 2020/5/19 14:21
     */
    List<TaskVo> selectTheSameMonthSendOrders(String date);

    /**
     * 根据日期查询当天车辆任务信息
     *
     * @param date
     * @Author Xm
     * @Date 2020/5/19 17:52
     */
    IPage<SendOrdersTaskVo> taskList(String date, Page<SendOrdersTaskVo> page);

    /**
     * 返回近5年年份
     *
     * @Author Xm
     * @Date 2020/5/22 14:08
     */
    List<String> yearsList();

    /**
     * 派单
     *
     * @param sendOrdersVo
     * @Author Xm
     * @Date 2020/5/26 15:09
     */
    Integer saveSendOrdersVo(SendOrdersVo sendOrdersVo, Date date, String name);

    /**
     * 根据项目编号查询入库出库完单信息
     *
     * @param projectNo
     * @Author Xm
     * @Date 2020/5/25 16:14
     */
    List<PlanVo> selectPlan2Accomplish(String projectNo, String id, String planType, String sendOrdersId, Page<PlanVo> page);

    /**
     * 完单操作
     *
     * @param planVo
     * @Author Xm
     * @Date 2020/5/27 10:53
     */
    Result<?> planedit(PlanVo planVo);

    /**
     * 根据计划id和计划类型查询历史派单记录
     *
     * @param ids
     * @param planType
     * @return
     */
    IPage<SendOrdersVo> selectSendOrdersController(String ids, String planType, Page<SendOrdersVo> page);

    void updatePlanState(Integer planId, String planType);

    void removeSendOrders(String id);

    void updateSendOrders(SendOrdersVo sendOrdersVo);

    IPage<SendOrdersVo> selectPlanSendOrdersTheSameDay(Page<SendOrdersVo> page);


    IPage<SendOrdersVo> selectSendOrdersWD(String ids, String planType, Page<SendOrdersVo> page);

    /**
     * 通过id删除完单记录信息
     * 2020/9/7
     *
     * @param sendOrdersVo 要回退的内容数据
     * @param id           完单信息 id
     * @param type         要删除的完单记录类型[出库、入库]
     * @param tableId      要删除的计划表1\2\3\4
     * @return success/error
     */
    Result<?> deletelStoragesById(Integer id, String type, SendOrdersVo sendOrdersVo, String tableId);
}
