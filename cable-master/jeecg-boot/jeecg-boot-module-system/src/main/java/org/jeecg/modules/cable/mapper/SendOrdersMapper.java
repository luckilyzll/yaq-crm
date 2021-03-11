package org.jeecg.modules.cable.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cable.entity.SendOrders;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.cable.vo.SendOrdersTaskVo;
import org.jeecg.modules.cable.vo.SendOrdersVo;
import org.jeecg.modules.cable.vo.TaskVo;

import java.util.Date;
import java.util.List;

/**
 * @Description: 派单表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface SendOrdersMapper extends BaseMapper<SendOrders> {

    /**
     * 根据年份和月份查询当月每日的车辆任务的数量
     *
     * @Author Xm
     * @Date 2020/5/19 14:50
     */
    List<TaskVo> selectTheSameMonthSendOrders(@Param("date") Date date);

    /**
     * 根据日期查询当天车辆任务信息
     *
     * @param date
     * @Author Xm
     * @Date 2020/5/19 17:53
     */
    List<SendOrdersTaskVo> taskList(@Param("date") Date date, @Param("page") Page<SendOrdersTaskVo> page);

    /**
     * 根据计划表名与计划id查询对应计划信息
     *
     * @Author Xm
     * @Date 2020/5/22 13:40
     */
    SendOrdersTaskVo getPlan(@Param("name") String name, @Param("id") String id);

    /**
     * 新增派单数据
     *
     * @param sendOrdersVo
     * @Author Xm
     * @Date 2020/5/26 15:51
     */
    void saveSendOrders(@Param("sendOrdersVo") SendOrdersVo sendOrdersVo, @Param("date") Date date, @Param("name") String name);

    List<SendOrdersVo> selectSendOrdersController(@Param("planId") List<String> planId, @Param("planType") String planType, @Param("page") Page<SendOrdersVo> page);

    void updatePlanState(@Param("planId") Integer planId, @Param("planType") String planType);

    List<SendOrdersVo> selectPlanSendOrdersTheSameDay(@Param("page") Page<SendOrdersVo> page);


    List<SendOrdersVo> selectSendOrdersWD(@Param("planId") List<String> planId, @Param("planType") String planType, @Param("page") Page<SendOrdersVo> page);
}
