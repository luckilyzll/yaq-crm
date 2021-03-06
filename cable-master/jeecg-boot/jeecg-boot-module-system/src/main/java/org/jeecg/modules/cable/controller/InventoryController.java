package org.jeecg.modules.cable.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.entity.Inventory;
import org.jeecg.modules.cable.service.IInventoryService;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.vo.InventoryListVo;
import org.jeecg.modules.cable.vo.InventoryListsVo;
import org.jeecg.modules.cable.vo.InventoryVo;
import org.jeecg.modules.cable.vo.YikuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 库存表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "库存表")
@RestController
@RequestMapping("/cable/inventory")
@Slf4j
public class InventoryController extends JeecgController<Inventory, IInventoryService> {
    @Autowired
    private IInventoryService inventoryService;

    /**
     * 添加
     *
     * @param inventory
     * @return
     */
    @AutoLog(value = "库存表-添加")
    @ApiOperation(value = "库存表-添加", notes = "库存表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Inventory inventory) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        inventory.setUpdateTime(new Date());
        inventory.setUpdateBy(sysUser.getUsername());
        inventoryService.save(inventory);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param inventory
     * @return
     */
    @AutoLog(value = "库存表-编辑")
    @ApiOperation(value = "库存表-编辑", notes = "库存表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Inventory inventory) {
        inventoryService.updateById(inventory);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "库存表-通过id删除")
    @ApiOperation(value = "库存表-通过id删除", notes = "库存表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        inventoryService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "库存表-批量删除")
    @ApiOperation(value = "库存表-批量删除", notes = "库存表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.inventoryService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "库存表-通过id查询")
    @ApiOperation(value = "库存表-通过id查询", notes = "库存表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Inventory inventory = inventoryService.getById(id);
        if (inventory == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(inventory);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param inventory
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Inventory inventory) {
        return super.exportXls(request, inventory, Inventory.class, "库存表");
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
        return super.importExcel(request, response, Inventory.class);
    }

    /**
     * 根据库位id查询该库位库存信息
     *
     * @param inventoryVo
     * @param pageNo
     * @param pageSize
     * @param req
     * @Author Xm
     * @Date 2020/5/22 15:43
     */
    @GetMapping(value = "/insurancePageList")
    public Result insurancePageList(InventoryListsVo inventoryVo,
                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Page<InventoryListsVo> page = new Page(pageNo, pageSize);
        IPage<InventoryListsVo> pageList = inventoryService.InsurancePageList(inventoryVo, page);
        return Result.ok(pageList);
    }

    @GetMapping(value = "/insuranceLists")
    public Result<?> insuranceLists(Inventory inventory) {
        List<InventoryListsVo> list = inventoryService.InsuranceLists(inventory.getStorageLocationId());
        return Result.ok(list);
    }
    //移库

    @GetMapping(value = "/yikuList")
    public Result<?> yikuList(YikuVo yikuVo,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<YikuVo> page = new Page<>(pageNo, pageSize);
        IPage<YikuVo> pageList = inventoryService.yikuVoPage(yikuVo, page);
        return Result.ok(pageList);
    }

    @DeleteMapping(value = "/yikuDel")
    public Result<?> yikuDel(@RequestParam(name = "id", required = true) Integer id) {
        inventoryService.yikuDel(id);
        return Result.ok("删除成功!");
    }

}
