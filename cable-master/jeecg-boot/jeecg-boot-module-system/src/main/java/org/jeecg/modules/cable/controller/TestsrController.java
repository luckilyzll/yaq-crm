package org.jeecg.modules.cable.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.config.DictConfig;
import org.jeecg.modules.cable.service.IMaterialService;
import org.jeecg.modules.cable.service.IWarehouseService;
import org.jeecg.modules.cable.vo.KuweiVo;
import org.jeecg.modules.cable.vo.OutPutWarehouseVo;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.jeecg.modules.system.service.ISysDictService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Description: 仓库表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */

//ruan
@RestController
@RequestMapping("/cable/testdata")
@Slf4j
public class TestsrController {
    @Autowired
    private ISysDictItemService sysDictItemService;
    @Autowired
    private IMaterialService materialService;
    @Autowired
    private ISysUserService sysUserService;

    @Autowired
    private ISysDictService sysDictService;

    @ApiOperation(value = "用户管理-查询所有部门")
    @GetMapping(value = "/bumenList")
    public Result<?> bumenList() {
        QueryWrapper<SysDictItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_id", DictConfig.depart_name_id);
        List<SysDictItem> list = sysDictItemService.list(queryWrapper);
        return Result.ok(list);
    }

    @ApiOperation(value = "用户管理-部门设置-分页列表查询")
    @GetMapping(value = "/bumenPage")
    public Result<?> bumenPage(@RequestParam(required = false) String name,
                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<SysDictItem> page = new Page<>(pageNo, pageSize);
        sysDictItemService.selectBYDict(name, page);
        return Result.ok(page);
    }

    @ApiOperation(value = "用户管理-部门设置-add")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysDictItem sysDictItem) {
        String id = sysDictService.getDepts();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sysDictItem.setUpdateTime(new Date());
        sysDictItem.setUpdateBy(sysUser.getUsername());
        sysDictItem.setCreateTime(new Date());
        sysDictItem.setCreateBy(sysUser.getUsername());
        sysDictItem.setDictId(id);
        QueryWrapper<SysDictItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_id", id);
        List<SysDictItem> list = sysDictItemService.list(queryWrapper);
        for (SysDictItem dictItem : list) {
            if (dictItem.getItemText().equals(sysDictItem.getItemText())) {
                return Result.error("部门名称重复添加");
            }
        }
        if (list.size() > 0) {
            sysDictItem.setItemValue((sysDictItemService.getBumenId(id) + 1) + "");
        } else {
            sysDictItem.setItemValue("1");
        }
        sysDictItemService.save(sysDictItem);
        return Result.ok("添加成功！");
    }

    @ApiOperation(value = "用户管理-部门设置-edit")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SysDictItem sysDictItem) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sysDictItem.setUpdateTime(new Date());
        sysDictItem.setUpdateBy(sysUser.getUsername());
        sysDictItemService.updateById(sysDictItem);
        return Result.ok("编辑成功!");
    }

    @CacheEvict(value = CacheConstant.SYS_DICT_CACHE, allEntries = true)
    @ApiOperation(value = "用户管理-部门设置-delete")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        SysDictItem sysDictItem = sysDictItemService.getById(id);
        QueryWrapper<SysUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("depart_ids", sysDictItem.getDictId());
        List<SysUser> list = sysUserService.list(queryWrapper);
        for (SysUser sysUser : list) {
            sysUser.setDepartIds(null);
            sysUserService.updateById(sysUser);
        }
        sysDictItemService.removeById(id);
        return Result.ok("删除成功!");
//        //预留判断 旗下数据
//        SysDictItem byId = sysDictItemService.getById(id);
//        if (byId == null) {
//            return Result.error("不存在!");
//        }
//        String val = byId.getItemValue();
//        QueryWrapper<SysUser> userQueryWrapper = new QueryWrapper<>();
//        userQueryWrapper.eq("depart_ids", val);
//        int count = sysUserService.count(userQueryWrapper);
//        if (count == 0) {
//            sysDictItemService.removeById(id);
//            return Result.ok("删除成功!");
//        } else {
//            return Result.error("还有关联数据，删除失败!");
//        }
    }

    /**
     * 出入库台账
     * 2020/9/1 bai
     *
     * @return 出入库台账统计信息
     */
    @GetMapping(value = "/getOutPutWarehouseList")
    public Result<?> getOutPutWarehouseList(OutPutWarehouseVo outPutWarehouseVo,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<OutPutWarehouseVo> page = new Page<>(pageNo, pageSize);
        IPage<OutPutWarehouseVo> pageList = materialService.getOutPutWarehouseList(outPutWarehouseVo, page);
        return Result.ok(pageList);
    }

    @Autowired
    private IWarehouseService warehouseService;

    //
//  @GetMapping(value = "/keweiQuery")
//  public Result<?> keweiQuery(@RequestParam(value = "id", required = false) String id,
//                              @RequestParam(value = "type", required = false) String type,
//                              @RequestParam(value = "warehouseId", required = false) String warehouseId) {
//    List<KuweiVo> list = warehouseService.keweiQuery(id, type, warehouseId);
//    return Result.ok(list);
//  }
    @GetMapping(value = "/keweiQuery")
    public Result<?> keweiQuery(@RequestParam(value = "id", required = false) String id) {


        List<KuweiVo> list = warehouseService.kewei(id);
        return Result.ok(list);
    }

    /**
     * 根据项目编号查询对应的库存库位存储数量
     * 2020/8/31 bai
     *
     * @param projectNo 项目编号
     * @return 库存库位信息
     */
    @GetMapping(value = "/queryInventory")
    public Result<?> queryInventory(@RequestParam(value = "projectNo", required = true) Serializable projectNo, @RequestParam(value = "materialId", required = true) Serializable materialId) {
        return Result.ok(warehouseService.queryInventory(projectNo, materialId));
    }
}
