package org.jeecg.modules.cable.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cable.vo.Deliver_Receiving_StorageVo;
import org.jeecg.modules.cable.vo.PickUpTheTaskVo;

import java.util.List;

/**
 * WX小程序
 * bai
 * 2020/6/11
 */
public interface WXMapper {
    /**
     * 接任务
     * bai
     * 2020/6/11
     */
    List<PickUpTheTaskVo> pickUpTheTask(@Param("completeState") Integer completeState, @Param("page") Page<PickUpTheTaskVo> page);

    /**
     * 入库处置
     * bai
     * 2020/6/15
     */
    Integer deliverStorage(@Param("vo") Deliver_Receiving_StorageVo vo);

    /**
     * 出库处置
     * bai
     * 2020/6/15
     */
    Integer receivingStorage(@Param("vo") Deliver_Receiving_StorageVo vo);
}
