package org.jeecg.modules.cable.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.cable.entity.Insurance;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.cable.vo.InsuranceListVo;

/**
 * @Description: 车保险表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface IInsuranceService extends IService<Insurance> {
    /**
     * 根据车牌号码查询车辆保险信息
     *
     * @param insurance 车辆信息
     * @param page      分页条件
     * @Author Xm
     * @Date 2020/5/14 17:50
     */
    IPage<InsuranceListVo> getInsurancePage(InsuranceListVo insurance, Page<InsuranceListVo> page);

}
