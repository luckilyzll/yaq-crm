package org.jeecg.modules.cable.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.cable.entity.StorageLocation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.cable.vo.StorageLocationVo;

import java.util.List;

/**
 * @Description: 库位表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
public interface StorageLocationMapper extends BaseMapper<StorageLocation> {
    /**
     * 查询库位信息
     * bai
     * 2020/6/8
     *
     * @param wrapper 构造条件
     * @return
     */
    List<StorageLocationVo> getAll(@Param(Constants.WRAPPER) Wrapper wrapper);
}
