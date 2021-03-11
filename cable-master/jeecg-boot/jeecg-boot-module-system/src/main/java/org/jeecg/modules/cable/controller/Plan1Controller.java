package org.jeecg.modules.cable.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.entity.*;
import org.jeecg.modules.cable.importpackage.Plan1Im;
import org.jeecg.modules.cable.service.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.vo.*;
import org.jeecg.modules.system.entity.SysDictItem;
import org.jeecg.modules.system.service.ISysDictItemService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

import static org.jeecgframework.poi.excel.ExcelExportUtil.exportExcel;

/**
 * @Description: 计划表1
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "计划表1")
@RestController
@RequestMapping("/cable/plan1")
@Slf4j
public class Plan1Controller extends JeecgController<Plan1, IPlan1Service> {
    @Autowired
    private IPlan1Service plan1Service;

    @Autowired
    private IPlan2Service plan2Service;

    @Autowired
    private IPlan3Service plan3Service;

    @Autowired
    private IPlan4Service plan4Service;

    @Autowired
    private IMaterialService materialService;

    @Autowired
    private ISysDictItemService sysDictItemService;

    /**
     * 计划1合并完单
     * 2020/8/27 bai
     *
     * @param plan1Vo 合并完单中的表单数据
     * @return 受影响的行数
     */
    @PostMapping(value = "/consolidationCompleted")
    public Result<?> consolidationCompleted(@RequestBody Plan1Vo plan1Vo) {
        Result<?> result = plan1Service.consolidationCompleted(Arrays.asList(plan1Vo.getPlan1Ids().split(",")), plan1Vo.getOperatorSchema(), plan1Vo.getReceiptNo(), plan1Vo.getReceiptPhotos(), plan1Vo.getTaskTime(), plan1Vo.getCompleteOrderList());
        if (result.getCode() == 200) {
            return Result.ok(result.getMessage());
        } else {
            return Result.error(result.getMessage());
        }
    }

    /**
     * 查询计划1批量出库完单的数据
     * bai
     * 2020/8/26
     *
     * @param ids 批量出库完单 ids
     * @return 计划1批量出库完单的数据
     */
    @GetMapping(value = "/getPlan1ReceivingStorageList")
    public Result<?> getPlan1ReceivingStorageList(@RequestParam(name = "ids") String ids) {
        List<Plan1Vo> list = plan1Service.getPlan1ReceivingStorageList(Arrays.asList(ids.split(",")));
        for (Plan1Vo item : list) {
            if (!list.get(0).getProjectNo().equals(item.getProjectNo())) {
                return Result.error("工程账号必须一致");
            }
        }
        return Result.ok(list);
    }

    /**
     * 查询计划1批量入库完单的数据
     * bai
     * 2020/8/26
     *
     * @param ids 批量入库完单 ids
     * @return 计划1批量入库完单的数据
     */
    @GetMapping(value = "/getPlan1DeliverStorage")
    public Result<?> getPlan1DeliverStorage(@RequestParam(name = "ids") String ids) {
        List<Plan1> list = plan1Service.getPlan1DeliverStorage(Arrays.asList(ids.split(",")));
        for (Plan1 item : list) {
            if (!list.get(0).getProjectNo().equals(item.getProjectNo())) {
                return Result.error("工程账号必须一致");
            }
        }
        return Result.ok(list);
    }

    /**
     * 分页列表查询
     *
     * @return
     */
    @AutoLog(value = "计划表1-分页列表查询")
    @ApiOperation(value = "计划表1-分页列表查询", notes = "计划表1-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Plan1Vo plan1,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        // 条件构造
        QueryWrapper<Plan1> wrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(plan1.getPlanType())) {
            //TODO 配变电、其他、电缆1、线路
            wrapper.like("plan_type", plan1.getPlanType());
        }
        if (StrUtil.isNotBlank(plan1.getItemSlip())) {
            //TODO 项目交接单
            wrapper.like("item_slip", plan1.getItemSlip());
        }
        if (StrUtil.isNotBlank(plan1.getAssetNo())) {
            //TODO 资产编码
            wrapper.like("asset_no", plan1.getAssetNo());
        }
        if (StrUtil.isNotBlank(plan1.getProjectNo())) {
            //TODO 项目编码
            wrapper.like("project_no", plan1.getProjectNo());
        }
        if (StrUtil.isNotBlank(plan1.getProjectName())) {
            //TODO 项目名称
            wrapper.like("project_name", plan1.getProjectName());
        }
        if (null != plan1.getCompleteState()) {
            //TODO 计划完成状态(字典 0未完成 1已完成)
            wrapper.eq("complete_state", plan1.getCompleteState());
        }
        Page<Plan1> page = new Page<>(pageNo, pageSize);
        IPage<Plan1> pageList = plan1Service.page(page, wrapper);
        return Result.ok(pageList);
    }

    /**
     * 根据ids集合
     * 实现分页列表查询
     *
     * @return
     */
    @AutoLog(value = "计划表1-分页列表查询")
    @ApiOperation(value = "计划表1-分页列表查询", notes = "计划表1-分页列表查询")
    @GetMapping(value = "/idslistRu")
    public Result<?> idsqueryRuList(@RequestParam(name = "ids") String ids,
                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        List<Plan1> pageList = plan1Service.idsqueryRuList(Arrays.asList(ids.split(",")));
        for (int i = 0; i < pageList.size(); i++) {
            if (!pageList.get(0).getProjectNo().equals(pageList.get(i).getProjectNo()))
                return Result.error("工程账号必须一致");
            pageList.get(i).setBackup1(null);
        }
        return Result.ok(pageList);
    }

    /**
     * 根据ids集合
     * 派单出库列表查询
     *
     * @return
     */
    @AutoLog(value = "计划表1-分页列表查询")
    @ApiOperation(value = "计划表1-分页列表查询", notes = "计划表1-分页列表查询")
    @GetMapping(value = "/idslistChu")
    public Result<?> idsqueryChuList(@RequestParam(name = "ids") String ids,
                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        List<SendOrdersVo> pageList = plan1Service.idsqueryChuList(Arrays.asList(ids.split(",")));
        for (int i = 0; i < pageList.size(); i++) {
            if (!pageList.get(0).getProjectNo().equals(pageList.get(i).getProjectNo()))
                return Result.error("工程账号必须一致");
        }
        return Result.ok(pageList);
    }
    /*
        List<SendOrdersVo> list = new ArrayList<>();
        SendOrdersVo sendOrdersVo ;
        List<SendOrdersVo> pageList = plan1Service.idsqueryChuList(Arrays.asList(ids.split(",")));
        for (int i = 0; i < pageList.size(); i++) {
            if (!pageList.get(0).getProjectNo().equals(pageList.get(i).getProjectNo()))
                return Result.error("工程账号必须一致");
            sendOrdersVo = new SendOrdersVo();
            sendOrdersVo.setId(pageList.get(i).getId());                                //计划id
            sendOrdersVo.setProjectNo(pageList.get(i).getProjectNo());                  //工程账号
            sendOrdersVo.setProjectName(pageList.get(i).getProjectName());              //项目名称
            sendOrdersVo.setWasteMaterialText(pageList.get(i).getWasteMaterialText());  //物料描述
            sendOrdersVo.setWarehouseId(pageList.get(i).getWarehouseId());              //仓库 id
            sendOrdersVo.setStorageLocationId(pageList.get(i).getStorageLocationId());  //库位 id
            sendOrdersVo.setWname(pageList.get(i).getWname());                          //仓库名称
            sendOrdersVo.setStoragename(pageList.get(i).getStoragename());              //库位名称
            sendOrdersVo.setInventoryQuantity(pageList.get(i).getInventoryQuantity());  //可出库数量
            list.add(sendOrdersVo);
        }
        return Result.ok(list);
    */

    /**
     * 添加
     *
     * @param plan1
     * @return
     */
    @AutoLog(value = "计划表1-添加")
    @ApiOperation(value = "计划表1-添加", notes = "计划表1-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Plan1 plan1) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        plan1.setUpdateTime(new Date());
        plan1.setUpdateBy(sysUser.getUsername());
        plan1Service.save(plan1);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param plan1
     * @return
     */
    @AutoLog(value = "计划表1-编辑")
    @ApiOperation(value = "计划表1-编辑", notes = "计划表1-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Plan1 plan1) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        plan1.setUpdateTime(new Date());
        plan1.setUpdateBy(sysUser.getUsername());
        plan1Service.updateById(plan1);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "计划表1-通过id删除")
    @ApiOperation(value = "计划表1-通过id删除", notes = "计划表1-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        Plan1 plan1 = plan1Service.getById(id);
        if (plan1.getSendOrdersState() == 0) {
            plan1Service.removeById(id);
            return Result.ok("删除成功!");
        }
        return Result.error("该计划已派过单，暂时不能删除");
    }

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "计划表1-批量删除")
//	@ApiOperation(value="计划表1-批量删除", notes="计划表1-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.plan1Service.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.ok("批量删除成功!");
//	}

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "计划表1-通过id查询")
    @ApiOperation(value = "计划表1-通过id查询", notes = "计划表1-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Plan1 plan1 = plan1Service.getById(id);
        if (plan1 == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(plan1);
    }

    /**
     * 导出 plan1
     * bai 2020/9/7
     *
     * @return 要导出计划1数据
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(Plan1Im plan1Im, @RequestParam(name = "explain", required = false) String explain) {
        // 获取导出数据集
        List<Plan1Im> list = plan1Service.exportPlan1(plan1Im, explain);
        /*list.forEach(obj -> {
            String inventoryPoint = obj.getInventoryPoint();
            if (null != inventoryPoint && inventoryPoint.length() > 0) {
                String[] s = inventoryPoint.split("_");
                obj.setInventoryPoint(s[1]);
            }
            String terminalBin = obj.getTerminalBin();
            if (null != terminalBin && terminalBin.length() > 0) {
                String[] s = terminalBin.split("_");
                obj.setTerminalBin(s[1]);
            }
        });*/
        // AutoPoi 导出 excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        /*mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下/*/
        mv.addObject(NormalExcelConstants.CLASS, Plan1Im.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams());
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 通过excel导入数据
     * bai
     * 2020/5/28
     *
     * @param request
     * @param response
     * @param planType 导入excel类型
     * @return
     */
    @Transactional
    @RequestMapping(value = "/importExcel/{planType}", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response, @PathVariable String planType) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Material material = new Material();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (null != sysUser) {
            material.setCreateBy(sysUser.getUsername());
            material.setUpdateBy(sysUser.getUsername());
        } else return Result.error("请重新登录！");
        material.setCreateTime(new Date());
        material.setUpdateTime(new Date());
        Integer in = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Plan1Im> list = ExcelImportUtil.importExcel(file.getInputStream(), Plan1Im.class, params);
                Plan1 plan1 = new Plan1();
                for (Plan1Im plan1Im : list) {
                    //判断对象中属性值是否为空
                    Boolean flag = (StrUtil.isNotBlank(plan1Im.getProjectName()) || StrUtil.isNotBlank(plan1Im.getProjectNo()) || StrUtil.isNotBlank(plan1Im.getWasteMaterialCode()) || StrUtil.isNotBlank(plan1Im.getWasteMaterialText()) || StrUtil.isNotBlank(plan1Im.getAssetNo()));
                    if (!flag) return Result.error("请补全物料信息！");

                    // 单位之间的转换
                    List<SysDictItem> dictItems = sysDictItemService.selectType("unit");
                    for (SysDictItem dictItem : dictItems) {
                        // 导入时判断单位是否存在，存在则进行单位转换
                        if (StrUtil.isNotBlank(plan1Im.getRawMaterialUnit())) {
                            if (plan1Im.getRawMaterialUnit().trim().equals(dictItem.getItemText())) {
                                plan1.setRawMaterialUnit(Integer.parseInt(dictItem.getItemValue()));
                            }
                        }
                        if (StrUtil.isNotBlank(plan1Im.getWasteMaterialUnit())) {
                            if (plan1Im.getWasteMaterialUnit().trim().equals(dictItem.getItemText())) {
                                plan1.setWasteMaterialUnit(Integer.parseInt(dictItem.getItemValue()));
                            }
                        }
                    }
                    // 属性拷贝 将 plan1Im 接收的excel数据转换为 plan1 实体类数据
                    BeanUtils.copyProperties(plan1Im, plan1);
                    plan1.setPlanType(planType);
                    plan1.setSendOrdersState(0);
                    plan1.setCompleteState(0);
                    plan1.setUpdateBy(sysUser.getUsername());
                    plan1.setCreateBy(sysUser.getUsername());
                    plan1.setUpdateTime(new Date());
                    plan1.setCreateTime(new Date());
                    plan1Service.save(plan1);
                    in++;
                    QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("serial", plan1.getWasteMaterialCode());
                    queryWrapper.eq("name", plan1.getWasteMaterialText());
                    List<Material> list1 = materialService.list(queryWrapper);
                    if (list1.size() == 0) {
                        if (StrUtil.isNotBlank(plan1.getWasteMaterialCode()) && StrUtil.isNotBlank(plan1.getWasteMaterialText())) {
                            // 废旧物料编码作为物料编码
                            material.setSerial(plan1.getWasteMaterialCode());
                            // 废旧物料描述作为物料名称
                            material.setName(plan1.getWasteMaterialText());
                            // 废旧物料单位作为物料单位
                            material.setUnit(plan1.getWasteMaterialUnit());
                            materialService.save(material);
                        }
                    }
                }
                if (in != 0) {
                    return Result.ok("文件导入成功！数据行数：" + in);
                }
                return Result.ok("文件导入成功！数据行数：" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    @PutMapping(value = "/planTheSameDayEdit")
    public Result<?> planTheSameDayEdit(@RequestBody SendOrdersVo sendOrdersVo) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (sendOrdersVo.getPlanType().equals("1")) {
            Plan1 plan1 = plan1Service.getById(sendOrdersVo.getPlanId());
            if (null != sysUser)
                plan1.setUpdateBy(sysUser.getUsername());
            plan1.setUpdateTime(new Date());
            plan1.setTheContact(sendOrdersVo.getLinkman());
            plan1.setThePhone(sendOrdersVo.getPhone());

            plan1.setEngineeringAddress(sendOrdersVo.getAddress()); //修改任务地址

            plan1Service.updateById(plan1);
        } else if (sendOrdersVo.getPlanType().equals("2")) {
            Plan2 plan2 = plan2Service.getById(sendOrdersVo.getPlanId());
            if (null != sysUser)
                plan2.setUpdateBy(sysUser.getUsername());
            plan2.setUpdateTime(new Date());
            plan2.setEquipmentOwners(sendOrdersVo.getLinkman());

            plan2.setAddress(sendOrdersVo.getAddress()); //修改任务地址

            plan2Service.updateById(plan2);
        } else if (sendOrdersVo.getPlanType().equals("3")) {
            Plan3 plan3 = plan3Service.getById(sendOrdersVo.getPlanId());
            if (null != sysUser)
                plan3.setUpdateBy(sysUser.getUsername());
            plan3.setUpdateTime(new Date());
            plan3.setFieldConsignee(sendOrdersVo.getLinkman());
            plan3.setCPhone(sendOrdersVo.getLinkman());

            plan3.setAddress(sendOrdersVo.getAddress()); //修改任务地址

            plan3Service.updateById(plan3);
        } else if (sendOrdersVo.getPlanType().equals("4")) {
            Plan4 plan4 = plan4Service.getById(sendOrdersVo.getPlanId());
            if (null != sysUser)
                plan4.setUpdateBy(sysUser.getUsername());
            plan4.setUpdateTime(new Date());
            plan4.setTeamContact(sendOrdersVo.getLinkman());

            plan4.setSamplingAddres(sendOrdersVo.getAddress()); //修改任务地址

            plan4Service.updateById(plan4);
        }
        return Result.ok("修改成功!");
    }

    /**
     * 查询已完成的计划（结算）
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping(value = "/selectSettleAccounts")
    public Result<?> selectSettleAccounts(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "backup1", required = false) String backup1,
                                          @RequestParam(name = "planType", required = false) String planType,
                                          @RequestParam(name = "projectNo", required = false) String projectNo) {
        Page<SettleAccountsVo> page = new Page<>(pageNo, pageSize);
        IPage<SettleAccountsVo> pageList = plan1Service.selectSettleAccounts(backup1, planType, projectNo, page);
        return Result.ok(pageList);
    }

    /**
     * 查询已完成的计划(结算)详情信息
     * bai
     * 2020/6/10 14:07
     *
     * @return
     */
    @GetMapping(value = "/selectSettleAccountsDetails")
    public Result<?> selectSettleAccountsDetails(@RequestParam(name = "projectNo", required = false) String projectNo,
                                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<SettleAccountsDetailsVo> page = new Page<>(pageNo, pageSize);
        IPage<SettleAccountsDetailsVo> pageList = plan1Service.selectSettleAccountsDetails(projectNo, page);
        return Result.ok(pageList);
    }

    /**
     * 项目结算，计划结算功能
     *
     * @param projectNo
     * @return
     */
    @GetMapping(value = "/updateSettleAccounts")
    public Result<?> updateSettleAccounts(@RequestParam(name = "projectNo", required = false) String projectNo) {

        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (null == sysUser)
            return Result.error("用户登录超时，请重新登录!");

        Plan1 plan1 = new Plan1();
        plan1.setBackup1("1");
        plan1.setUpdateTime(new Date());
        plan1.setUpdateBy(sysUser.getUsername());
        plan1Service.update(plan1, new QueryWrapper<Plan1>().eq("project_no", projectNo));
        Plan2 plan2 = new Plan2();
        plan2.setBackup1("1");
        plan2.setUpdateTime(new Date());
        plan2.setUpdateBy(sysUser.getUsername());
        plan2Service.update(plan2, new QueryWrapper<Plan2>().eq("project_no", projectNo));
        Plan3 plan3 = new Plan3();
        plan3.setBackup1("1");
        plan3.setUpdateTime(new Date());
        plan3.setUpdateBy(sysUser.getUsername());
        plan3Service.update(plan3, new QueryWrapper<Plan3>().eq("project_no", projectNo));
        Plan4 plan4 = new Plan4();
        plan4.setBackup1("1");
        plan4.setUpdateTime(new Date());
        plan4.setUpdateBy(sysUser.getUsername());
        plan4Service.update(plan4, new QueryWrapper<Plan4>().eq("project_no", projectNo));

        return Result.ok("结算成功!");
    }

    /**
     * 计划表1配变电统计
     *
     * @return
     */
    @GetMapping(value = "/selectSubstation")
    public Result<?> selectSubstation(@RequestParam(name = "wasteMaterialText", required = false) String wasteMaterialText,
                                      @RequestParam(name = "beginTime", required = false) String beginTime,
                                      @RequestParam(name = "endTime", required = false) String endTime,
                                      @RequestParam(name = "planType", required = false) String planType,
                                      @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<Plan1Vo> page = new Page<>(pageNo, pageSize);
        // String planType="配变电";
        IPage<Plan1Vo> pageList = plan1Service.selectSubstation(wasteMaterialText, beginTime, endTime, planType, page);
        return Result.ok(pageList);
    }

    /**
     * 导出变电/导线统计数据 excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls2")
    public ModelAndView exportXls2(HttpServletRequest request, Plan1ExcelVo plan1ExcelVo) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "变电统计";
        List<Plan1ExcelVo> list = plan1Service.exportPlan2(plan1ExcelVo);
        System.out.println(plan1ExcelVo.getDecommissioningDate());
        Plan1ExcelVo plan1ExcelVo1 = new Plan1ExcelVo();
        BigDecimal stockNum = BigDecimal.valueOf(0);
        BigDecimal receivingNum = BigDecimal.valueOf(0);
        String proName = "";
        for (Plan1ExcelVo excelVo : list) {
            proName += excelVo.getProjectName() + ",";
            stockNum = stockNum.add(BigDecimal.valueOf(Double.parseDouble(excelVo.getStockNum())));
            receivingNum = receivingNum.add(BigDecimal.valueOf(Double.parseDouble(excelVo.getReceivingNum())));
            excelVo.setProjectName(null);
        }
        plan1ExcelVo1.setWasteMaterialText("合计:");
        plan1ExcelVo1.setStockNum(stockNum.toString());
        plan1ExcelVo1.setReceivingNum(receivingNum.toString());
        list.add(plan1ExcelVo1);
        list.get(0).setDecommissioningDate(plan1ExcelVo.getDecommissioningDate());
        list.get(0).setRemark(plan1ExcelVo.getRemark());
        list.get(0).setProjectName(proName);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Plan1ExcelVo.class);
        if (plan1ExcelVo.getPlanType().equals("线路")) {
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(plan1ExcelVo.getBeginTime() + "--" + plan1ExcelVo.getEndTime() + "/线路统计", ""));
        } else {
            mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(plan1ExcelVo.getBeginTime() + "--" + plan1ExcelVo.getEndTime() + "/变电统计", ""));
        }
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

}
