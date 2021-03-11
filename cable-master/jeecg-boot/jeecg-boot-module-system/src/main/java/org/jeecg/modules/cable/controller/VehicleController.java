package org.jeecg.modules.cable.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.entity.SendOrdersSubtabulation;
import org.jeecg.modules.cable.entity.Vehicle;
import org.jeecg.modules.cable.service.ISendOrdersSubtabulationService;
import org.jeecg.modules.cable.service.IVehicleService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 车辆表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "车辆表")
@RestController
@RequestMapping("/cable/vehicle")
@Slf4j
public class VehicleController extends JeecgController<Vehicle, IVehicleService> {
    @Autowired
    private IVehicleService vehicleService;

    @Autowired
    private ISendOrdersSubtabulationService sendOrdersSubtabulationService;

    /**
     * 分页列表查询
     *
     * @param vehicle
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "车辆表-分页列表查询")
    @ApiOperation(value = "车辆表-分页列表查询", notes = "车辆表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Vehicle vehicle,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        //TODO QueryWrapper<Vehicle> queryWrapper = QueryGenerator.initQueryWrapper(vehicle, req.getParameterMap());
        //TODO queryWrapper.orderByDesc("update_time");
        QueryWrapper<Vehicle> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(vehicle.getLicense())) {
            wrapper.like("license", vehicle.getLicense()); //TODO 根据车牌号码进行模糊查询
        }
        if (null != vehicle.getType()) {
            wrapper.eq("type", vehicle.getType()); //TODO 根据车辆类型进行等值查询
        }
        if (null != vehicle.getState()) {
            wrapper.eq("state", vehicle.getState());  //TODO 根据车辆状态进行等值查询
        }
        wrapper.orderByDesc("update_time"); //TODO 根据修改时间进行排序操作
        Page<Vehicle> page = new Page<Vehicle>(pageNo, pageSize);
        IPage<Vehicle> pageList = vehicleService.page(page, wrapper);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param vehicle
     * @return
     */
    @AutoLog(value = "车辆表-添加")
    @ApiOperation(value = "车辆表-添加", notes = "车辆表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Vehicle vehicle) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        vehicle.setUpdateTime(new Date());
        vehicle.setUpdateBy(sysUser.getUsername());
        vehicleService.save(vehicle);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param vehicle
     * @return
     */
    @AutoLog(value = "车辆表-编辑")
    @ApiOperation(value = "车辆表-编辑", notes = "车辆表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Vehicle vehicle) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        vehicle.setUpdateTime(new Date());
        vehicle.setUpdateBy(sysUser.getUsername());
        vehicleService.updateById(vehicle);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "车辆表-通过id删除")
    @ApiOperation(value = "车辆表-通过id删除", notes = "车辆表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        Vehicle vehicle = vehicleService.getById(id);
        QueryWrapper<SendOrdersSubtabulation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("distribution_type", "0");
        queryWrapper.eq("type_id", vehicle.getLicense());
        int count = sendOrdersSubtabulationService.count(queryWrapper);
        if (count > 0) {
            return Result.error("该车 " + vehicle.getLicense() + " 已派单，不能被删除");
        } else {
            vehicleService.removeById(vehicle.getId());
            return Result.ok("删除成功!");
        }
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "车辆表-批量删除")
    @ApiOperation(value = "车辆表-批量删除", notes = "车辆表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        String[] idlist = ids.split(",");
        if (idlist.length > 0) {
            for (String id : idlist) {
                Vehicle vehicle = vehicleService.getById(id);
                QueryWrapper<SendOrdersSubtabulation> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("distribution_type", "0");
                queryWrapper.eq("type_id", vehicle.getLicense());
                List<SendOrdersSubtabulation> list = sendOrdersSubtabulationService.list(queryWrapper);
                if (list.size() > 0) {
                    return Result.error("该车 " + vehicle.getLicense() + " 已派单，不能被删除");
                } else {
                    vehicleService.removeById(vehicle.getId());
                }
            }
            return Result.ok("删除成功!");
        }
        return Result.error("删除失败");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "车辆表-通过id查询")
    @ApiOperation(value = "车辆表-通过id查询", notes = "车辆表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Vehicle vehicle = vehicleService.getById(id);
        if (vehicle == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(vehicle);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param vehicle
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Vehicle vehicle) {
        return super.exportXls(request, vehicle, Vehicle.class, "车辆表");
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
        return super.importExcel(request, response, Vehicle.class);
    }

    /**
     * 查询所有正常车辆
     *
     * @param
     * @Author Xm
     * @Date 2020/5/26 14:57
     */
    @GetMapping(value = "/selectVehicleList")
    public Result<?> selectVehicleList() {
        QueryWrapper<Vehicle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("state", 0);
        List<Vehicle> list = vehicleService.list(queryWrapper);
        return Result.ok(list);
    }

}
