package org.jeecg.modules.cable.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.jeecg.modules.cable.entity.StorageLocation;
import org.jeecg.modules.cable.mapper.StorageLocationMapper;
import org.jeecg.modules.cable.service.IStorageLocationService;
import org.jeecg.modules.cable.vo.StorageLocationVo;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;

/**
 * @Description: 库位表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Service
public class StorageLocationServiceImpl extends ServiceImpl<StorageLocationMapper, StorageLocation> implements IStorageLocationService {
    @Override
    public List<StorageLocationVo> getAll(Wrapper wrapper) {
        return baseMapper.getAll(wrapper);
    }
}
