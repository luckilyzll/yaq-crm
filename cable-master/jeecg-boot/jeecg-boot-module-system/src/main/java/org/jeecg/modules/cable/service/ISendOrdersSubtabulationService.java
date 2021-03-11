package org.jeecg.modules.cable.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cable.entity.SendOrdersSubtabulation;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Date;

/**
 * @Description: 派单-车辆-员工关系表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface ISendOrdersSubtabulationService extends IService<SendOrdersSubtabulation> {

    /**
     * 新增派单-员工/车辆
     *
     * @param sendOrdersId
     * @param distributionType
     * @param typeId
     * @param taskTime
     * @Author Xm
     * @Date 2020/5/26 17:46
     */
    void saveSendOrdersSubtabulation(Integer sendOrdersId, Integer distributionType, String typeId, Date taskTime);
}
