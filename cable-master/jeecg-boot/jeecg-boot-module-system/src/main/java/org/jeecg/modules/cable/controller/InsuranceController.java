package org.jeecg.modules.cable.controller;

import java.util.Arrays;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.entity.Insurance;
import org.jeecg.modules.cable.entity.Vehicle;
import org.jeecg.modules.cable.service.IInsuranceService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.service.IVehicleService;
import org.jeecg.modules.cable.vo.InsuranceListVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 车保险表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "车保险表")
@RestController
@RequestMapping("/cable/insurance")
@Slf4j
public class InsuranceController extends JeecgController<Insurance, IInsuranceService> {
    @Autowired
    private IInsuranceService insuranceService;

    @Autowired
    private IVehicleService vehicleService;

    /**
     * 分页列表查询
     *
     * @param insurance
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "车保险表-分页列表查询")
    @ApiOperation(value = "车保险表-分页列表查询", notes = "车保险表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(InsuranceListVo insurance,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        Page<InsuranceListVo> page = new Page<InsuranceListVo>(pageNo, pageSize);
        IPage<InsuranceListVo> pageList = insuranceService.getInsurancePage(insurance, page);
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param insurance
     * @return
     */
    @AutoLog(value = "车保险表-添加")
    @ApiOperation(value = "车保险表-添加", notes = "车保险表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Insurance insurance) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        insurance.setUpdateTime(new Date());
        insurance.setUpdateBy(sysUser.getUsername());
        Vehicle vehicle = vehicleService.getById(insurance.getLicense());
        insurance.setLicense(vehicle.getLicense());
        insuranceService.save(insurance);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param insurance
     * @return
     */
    @AutoLog(value = "车保险表-编辑")
    @ApiOperation(value = "车保险表-编辑", notes = "车保险表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Insurance insurance) {
        Vehicle vehicle = vehicleService.getById(insurance.getLicense());
        insurance.setLicense(vehicle.getLicense());
        Insurance insurance1 = insurance;
        insuranceService.updateById(insurance);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "车保险表-通过id删除")
    @ApiOperation(value = "车保险表-通过id删除", notes = "车保险表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        insuranceService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "车保险表-批量删除")
    @ApiOperation(value = "车保险表-批量删除", notes = "车保险表-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.insuranceService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.ok("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "车保险表-通过id查询")
    @ApiOperation(value = "车保险表-通过id查询", notes = "车保险表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Insurance insurance = insuranceService.getById(id);
        if (insurance == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(insurance);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param insurance
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, Insurance insurance) {
        return super.exportXls(request, insurance, Insurance.class, "车保险表");
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
        return super.importExcel(request, response, Insurance.class);
    }

}
