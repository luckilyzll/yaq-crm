package org.jeecg.modules.cable.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cable.entity.Insurance;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.cable.vo.InsuranceListVo;

import java.util.List;

/**
 * @Description: 车保险表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface InsuranceMapper extends BaseMapper<Insurance> {

    /**
     * 根据车牌号码查询车辆保险信息
     *
     * @param license 车牌号码
     * @param page    分页条件
     * @Author Xm
     * @Date 2020/5/14 17:51
     */
    List<InsuranceListVo> getInsurancePage(@Param("license") String license, @Param("page") Page<InsuranceListVo> page);


}
