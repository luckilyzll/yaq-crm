package org.jeecg.modules.cable.mapper;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.cable.entity.ReceivingStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.cable.vo.PlanVo;

import java.util.List;

/**
 * @Description: 出库/完单表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface ReceivingStorageMapper extends BaseMapper<ReceivingStorage> {

    /**
     * 根据id修改出库信息
     *
     * @param planVo
     * @Author Xm
     * @Date 2020/5/27 11:39
     */
    void updateDS(@Param("PlanVo") PlanVo planVo);
}
