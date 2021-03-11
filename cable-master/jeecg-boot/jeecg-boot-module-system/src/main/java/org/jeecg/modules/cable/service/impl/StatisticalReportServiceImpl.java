package org.jeecg.modules.cable.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.cable.entity.SendOrdersSubtabulation;
import org.jeecg.modules.cable.mapper.StatisticalReportMapper;
import org.jeecg.modules.cable.service.IStatisticalReportService;
import org.jeecg.modules.cable.vo.DepartureStatisticsDetailsVo;
import org.jeecg.modules.cable.vo.DepartureStatisticsVo;
import org.jeecg.modules.cable.vo.PersonnelTaskStatisticsVo;
import org.jeecg.modules.system.entity.SysUser;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 统计报表模块
 * bai
 * 2020/5/27
 */
@Service
public class StatisticalReportServiceImpl extends ServiceImpl<StatisticalReportMapper, SendOrdersSubtabulation> implements IStatisticalReportService {
    String newYear = new SimpleDateFormat("yyyy").format(new Date());//当前年份

    @Override
    public IPage<PersonnelTaskStatisticsVo> getPersonnelTaskStatistics(String taskTime, String realName, Page<PersonnelTaskStatisticsVo> page) {
        if (taskTime == null || taskTime.equals("")) {
            taskTime = newYear;
        }
        List<PersonnelTaskStatisticsVo> list = baseMapper.getPersonnelTaskStatistics(taskTime, realName, page);
        return page.setRecords(list);
    }

    @Override
    public IPage<DepartureStatisticsVo> getDepartureStatistics(String taskTime, Page<DepartureStatisticsVo> page) {
        if (taskTime == null || taskTime.equals("")) {
            taskTime = newYear;
        }
        List<DepartureStatisticsVo> list = baseMapper.getDepartureStatistics(taskTime, page);
        return page.setRecords(list);
    }

    @Override
    public IPage<DepartureStatisticsDetailsVo> getDepartureStatisticsDetails(String taskTime, String license, String month, Page<DepartureStatisticsDetailsVo> page) {
        if (taskTime == null || taskTime.equals("")) {
            taskTime = newYear;
        }
        List<DepartureStatisticsDetailsVo> list = baseMapper.getDepartureStatisticsDetails(taskTime, license, month, page);
        return page.setRecords(list);
    }

    @Override
    public List<SysUser> getUsers() {
        return baseMapper.getUsers();
    }
}
