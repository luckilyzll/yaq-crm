package org.jeecg.modules.cable.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
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
public interface StatisticalReportMapper extends BaseMapper<SendOrdersSubtabulation> {

    /**
     * 人员任务统计
     * bai
     * 2020/5/27
     *
     * @return
     */
    List<PersonnelTaskStatisticsVo> getPersonnelTaskStatistics(@Param("taskTime") String taskTime,
                                                               @Param("realName") String realName,
                                                               @Param("page") Page<PersonnelTaskStatisticsVo> page);

    /**
     * 出车统计
     * bai
     * 2020/5/27
     *
     * @return
     */
    List<DepartureStatisticsVo> getDepartureStatistics(@Param("taskTime") String taskTime,
                                                       @Param("page") Page<DepartureStatisticsVo> page);

    /**
     * 出车统计详情信息
     * bai
     * 2020/5/27
     *
     * @return
     */
    List<DepartureStatisticsDetailsVo> getDepartureStatisticsDetails(@Param("taskTime") String taskTime,
                                                                     @Param("license") String license,
                                                                     @Param("month") String month,
                                                                     @Param("page") Page<DepartureStatisticsDetailsVo> page);

    /**
     * 查询所有用户用作页面展示
     *
     * @return
     */
    @Select("SELECT * FROM sys_user")
    List<SysUser> getUsers();
}
