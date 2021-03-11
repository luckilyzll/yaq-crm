package org.jeecg.modules.cable.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.cable.entity.SendOrdersSubtabulation;
import org.jeecg.modules.cable.vo.DepartureStatisticsDetailsVo;
import org.jeecg.modules.cable.vo.DepartureStatisticsVo;
import org.jeecg.modules.cable.vo.PersonnelTaskStatisticsVo;
import org.jeecg.modules.system.entity.SysUser;

import java.util.List;

/**
 * 统计报表模块
 * bai
 * 2020/5/27
 */
public interface IStatisticalReportService extends IService<SendOrdersSubtabulation> {

    /**
     * 人员任务统计
     * bai
     * 2020/5/27
     *
     * @return
     */
    IPage<PersonnelTaskStatisticsVo> getPersonnelTaskStatistics(String taskTime, String realName, Page<PersonnelTaskStatisticsVo> page);

    /**
     * 出车统计
     * bai
     * 2020/5/27
     *
     * @return
     */
    IPage<DepartureStatisticsVo> getDepartureStatistics(String taskTime, Page<DepartureStatisticsVo> page);

    /**
     * 出车统计详情信息
     * bai
     * 2020/5/27
     *
     * @return
     */
    IPage<DepartureStatisticsDetailsVo> getDepartureStatisticsDetails(String taskTime, String license, String month, Page<DepartureStatisticsDetailsVo> page);

    List<SysUser> getUsers();
}
