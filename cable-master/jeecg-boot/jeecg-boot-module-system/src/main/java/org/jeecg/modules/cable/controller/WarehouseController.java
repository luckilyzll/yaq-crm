package org.jeecg.modules.cable.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.entity.StorageLocation;
import org.jeecg.modules.cable.entity.Vehicle;
import org.jeecg.modules.cable.entity.Warehouse;
import org.jeecg.modules.cable.service.IStorageLocationService;
import org.jeecg.modules.cable.service.IWarehouseService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.vo.InventoryIocationListVo;
import org.jeecg.modules.cable.vo.InventoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 仓库表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "仓库表")
@RestController
@RequestMapping("/cable/warehouse")
@Slf4j
public class WarehouseController extends JeecgController<Warehouse, IWarehouseService> {
    @Autowired
    private IWarehouseService warehouseService;

    @Autowired
    private IStorageLocationService storageLocationService;

    /**
     * 分页列表查询
     *
     * @param warehouse
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "仓库表-分页列表查询")
    @ApiOperation(value = "仓库表-分页列表查询", notes = "仓库表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Warehouse warehouse,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<Warehouse> queryWrapper = QueryGenerator.initQueryWrapper(warehouse, req.getParameterMap());
        queryWrapper.orderByAsc("update_time");
        Page<Warehouse> page = new Page<Warehouse>(pageNo, pageSize);
        IPage<Warehouse> pageList = warehouseService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * todo
     * 分页列表查询inventoryIocationListVo 库存 库位查询
     * liu
     * 2020/7/24 改
     *
     * @return
     */
    @AutoLog(value = "分页列表查询")
    @ApiOperation(value = "分页列表查询")
    @GetMapping(value = "/inventoryIocationList")
    public Result<?> queryPageList(InventoryVo inventoryVo,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<InventoryVo> page = new Page<InventoryVo>(pageNo, pageSize);
        IPage<InventoryVo> pageList = warehouseService.selectPageinventory(inventoryVo, page);
        return Result.ok(pageList);
    }

    /**
     * 分页列表查询 库存 查询详情
     * liu
     * 2020/8/19 改
     *
     * @return
     */
    @AutoLog(value = "分页列表查询")
    @ApiOperation(value = "分页列表查询")
    @GetMapping(value = "/selectInfo")
    public Result<?> selectInfo(InventoryVo inventoryVo,
                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize) {
        Page<InventoryVo> page = new Page<InventoryVo>(pageNo, pageSize);
        IPage<InventoryVo> pageList = warehouseService.selectInfo(inventoryVo, page);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param warehouse
     * @return
     */
    @AutoLog(value = "仓库表-添加")
    @ApiOperation(value = "仓库表-添加", notes = "仓库表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Warehouse warehouse) {
        // 新增仓库对于自家仓库类型不可以重复添加
        if (null != warehouse.getType()) {
            if (warehouse.getType() == 1) {
                // 代表本次添加的仓库是自家仓库
                List<Warehouse> list = warehouseService.list(new QueryWrapper<Warehouse>().eq("name", warehouse.getName()));
                if (list.size() > 0) {
                    return Result.error("仓库已经存在,不能重复添加！");
                }
            }
        }
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        warehouse.setUpdateTime(new Date());
        warehouse.setUpdateBy(sysUser.getUsername());
        warehouseService.save(warehouse);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param warehouse
     * @return
     */
    @AutoLog(value = "仓库表-编辑")
    @ApiOperation(value = "仓库表-编辑", notes = "仓库表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Warehouse warehouse) {
        warehouseService.updateById(warehouse);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "仓库表-通过id删除")
    @ApiOperation(value = "仓库表-通过id删除", notes = "仓库表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        // 删除仓库前首先判断此仓库下是否存在库位
        QueryWrapper<StorageLocation> wrapper = new QueryWrapper<>();
        wrapper.eq("warehouse_id", id);
        List<StorageLocation> list = storageLocationService.list(wrapper);
        if (list.size() > 0) {
            return Result.error("此仓库下存在库位,不能被删除!");
        }
        warehouseService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "仓库表-批量删除")
    @ApiOperation(value = "仓库表-批量删除", notes = "仓库表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.warehouseService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "仓库表-通过id查询")
    @ApiOperation(value = "仓库表-通过id查询", notes = "仓库表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Warehouse warehouse = warehouseService.getById(id);
        if (warehouse == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(warehouse);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param warehouse
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Warehouse warehouse) {
        return super.exportXls(request, warehouse, Warehouse.class, "仓库表");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, Warehouse.class);
    }

    /**
     * 查询所有仓库类型为自家的仓库信息
     *
     * @param
     * @Author Xm
     * @Date 2020/5/20 13:33
     */
    @GetMapping(value = "/warehouseOneselfList")
    public Result<?> warehouseOneselfList() {
        QueryWrapper<Warehouse> queryWrapper = new QueryWrapper<>();
        List<Warehouse> list = warehouseService.list(queryWrapper);
        return Result.ok(list);
    }

}
