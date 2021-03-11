package org.jeecg.modules.cable.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cable.entity.DeliverStorage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.cable.vo.PlanVo;

import java.util.List;

/**
 * @Description: 入库/完单表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface DeliverStorageMapper extends BaseMapper<DeliverStorage> {

    /**
     * 根据项目编号查询入库完单信息
     *
     * @param projectNo
     * @Author Xm
     * @Date 2020/5/25 16:12
     */
    List<PlanVo> selectPlan2DS(@Param("projectNo") String projectNo, @Param("id") String id, @Param("planType") String planType, @Param("sendOrdersId") String sendOrdersId, @Param("page") Page<PlanVo> page);

    /**
     * 根据id修改入库信息
     *
     * @param planVo
     * @Author Xm
     * @Date 2020/5/27 11:10
     */
    void updateDS(@Param("PlanVo") PlanVo planVo);
}
