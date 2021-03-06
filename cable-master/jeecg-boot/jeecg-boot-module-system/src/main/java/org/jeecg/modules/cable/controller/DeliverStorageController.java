package org.jeecg.modules.cable.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.cable.entity.DeliverStorage;
import org.jeecg.modules.cable.service.IDeliverStorageService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 入库/完单表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "入库/完单表")
@RestController
@RequestMapping("/cable/deliverStorage")
@Slf4j
public class DeliverStorageController extends JeecgController<DeliverStorage, IDeliverStorageService> {
    @Autowired
    private IDeliverStorageService deliverStorageService;

    /**
     * 分页列表查询
     *
     * @param deliverStorage
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "入库/完单表-分页列表查询")
    @ApiOperation(value = "入库/完单表-分页列表查询", notes = "入库/完单表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(DeliverStorage deliverStorage,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<DeliverStorage> queryWrapper = QueryGenerator.initQueryWrapper(deliverStorage, req.getParameterMap());
        Page<DeliverStorage> page = new Page<DeliverStorage>(pageNo, pageSize);
        IPage<DeliverStorage> pageList = deliverStorageService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param deliverStorage
     * @return
     */
    @AutoLog(value = "入库/完单表-添加")
    @ApiOperation(value = "入库/完单表-添加", notes = "入库/完单表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody DeliverStorage deliverStorage) {
        deliverStorageService.save(deliverStorage);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param deliverStorage
     * @return
     */
    @AutoLog(value = "入库/完单表-编辑")
    @ApiOperation(value = "入库/完单表-编辑", notes = "入库/完单表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody DeliverStorage deliverStorage) {
        deliverStorageService.updateById(deliverStorage);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "入库/完单表-通过id删除")
    @ApiOperation(value = "入库/完单表-通过id删除", notes = "入库/完单表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        deliverStorageService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "入库/完单表-批量删除")
    @ApiOperation(value = "入库/完单表-批量删除", notes = "入库/完单表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.deliverStorageService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "入库/完单表-通过id查询")
    @ApiOperation(value = "入库/完单表-通过id查询", notes = "入库/完单表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        DeliverStorage deliverStorage = deliverStorageService.getById(id);
        if (deliverStorage == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(deliverStorage);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param deliverStorage
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, DeliverStorage deliverStorage) {
        return super.exportXls(request, deliverStorage, DeliverStorage.class, "入库/完单表");
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
        return super.importExcel(request, response, DeliverStorage.class);
    }

}
