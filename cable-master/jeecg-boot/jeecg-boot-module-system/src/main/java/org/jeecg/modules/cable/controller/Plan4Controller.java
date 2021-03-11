package org.jeecg.modules.cable.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.entity.Material;
import org.jeecg.modules.cable.entity.Plan1;
import org.jeecg.modules.cable.entity.Plan3;
import org.jeecg.modules.cable.entity.Plan4;
import org.jeecg.modules.cable.importpackage.Plan4Im;
import org.jeecg.modules.cable.service.IMaterialService;
import org.jeecg.modules.cable.service.IPlan4Service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.vo.Plan4ExcelVo;
import org.jeecg.modules.cable.vo.Plan4Vo;
import org.jeecg.modules.cable.vo.SendOrdersVo;
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

/**
 * @Description: 计划表4
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "计划表4")
@RestController
@RequestMapping("/cable/plan4")
@Slf4j
public class Plan4Controller extends JeecgController<Plan4, IPlan4Service> {
    @Autowired
    private IPlan4Service plan4Service;

    @Autowired
    private IMaterialService materialService;

    /**
     * 计划4合并完单
     * 2020/8/28 bai
     *
     * @param plan4Vo 合并完单中的表单数据
     * @return 受影响的行数
     */
    @PostMapping(value = "/consolidationCompleted")
    public Result<?> consolidationCompleted(@RequestBody Plan4Vo plan4Vo) {
        Result<?> result = plan4Service.consolidationCompleted(Arrays.asList(plan4Vo.getPlan4Ids().split(",")), plan4Vo.getOperatorSchema(), plan4Vo.getPlan4ReceiptNo(), plan4Vo.getReceiptPhotos(), plan4Vo.getTaskTime(), plan4Vo.getCompleteOrderList());
        if (result.getCode().equals(CommonConstant.SC_OK_200)) {
            return Result.ok(result.getMessage());
        } else {
            return Result.error(result.getMessage());
        }
    }

    /**
     * 查询计划4批量出库完单的数据
     * bai
     * 2020/8/28
     *
     * @param ids 批量出库完单 ids
     * @return 计划4批量出库完单的数据
     */
    @GetMapping(value = "/getPlan4ReceivingStorageList")
    public Result<?> getPlan4ReceivingStorageList(@RequestParam(name = "ids") String ids) {
        List<Plan4Vo> list = plan4Service.getPlan4ReceivingStorageList(Arrays.asList(ids.split(",")));
        for (Plan4Vo item : list) {
            if (!list.get(0).getProjectNo().equals(item.getProjectNo())) {
                return Result.error("工程账号必须一致");
            }
        }
        return Result.ok(list);
    }

    /**
     * 查询计划4批量入库完单的数据
     * bai
     * 2020/8/28
     *
     * @param ids 批量入库完单 ids
     * @return 计划4批量入库完单的数据
     */
    @GetMapping(value = "/getPlan4DeliverStorage")
    public Result<?> getPlan4DeliverStorage(@RequestParam(name = "ids") String ids) {
        List<Plan4> list = plan4Service.getPlan4DeliverStorage(Arrays.asList(ids.split(",")));
        for (Plan4 item : list) {
            if (!list.get(0).getProjectNo().equals(item.getProjectNo())) {
                return Result.error("工程账号必须一致");
            }
        }
        return Result.ok(list);
    }

    /**
     * 分页列表查询
     * bai
     * 2020/5/29
     *
     * @return
     */
    @AutoLog(value = "计划表4-分页列表查询")
    @ApiOperation(value = "计划表4-分页列表查询", notes = "计划表4-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Plan4 plan4,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<Plan4> page = new Page<Plan4>(pageNo, pageSize);
        IPage<Plan4> pageList = plan4Service.pageList(plan4, page);
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
        List<Plan1> list = new ArrayList<>();
        Plan1 plan;
        List<Plan4> pageList = plan4Service.idsqueryRuList(Arrays.asList(ids.split(",")));
        for (int i = 0; i < pageList.size(); i++) {
            if (!pageList.get(0).getProjectNo().equals(pageList.get(i).getProjectNo()))
                return Result.error("工程账号必须一致");
            plan = new Plan1();
            plan.setId(pageList.get(i).getId());                                //计划id
            plan.setProjectNo(pageList.get(i).getProjectNo());                  //工程账号
            plan.setProjectName(pageList.get(i).getEngName());                  //项目名称
            //TODO 电缆的"物料描述"为 "电缆名称" + "电压等级" + "电缆截面"
            String material = pageList.get(i).getCableName() + " " + pageList.get(i).getVoltageGrade() + " " + pageList.get(i).getCableCross();
            plan.setWasteMaterialText(material);                                    //物料描述
            plan.setWasteMaterialCode(material);                                    //物料代码
            pageList.get(i).setBackup1(null);
            list.add(plan);
        }
        return Result.ok(list);

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
    public Result<?> idsqueryChuList(@RequestParam(name = "ids") String ids) {
        List<SendOrdersVo> pageList = plan4Service.idsqueryChuList(Arrays.asList(ids.split(",")));
        for (int i = 0; i < pageList.size(); i++) {
            if (!pageList.get(0).getProjectNo().equals(pageList.get(i).getProjectNo()))
                return Result.error("工程账号必须一致");
        }
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param plan4
     * @return
     */
    @AutoLog(value = "计划表4-添加")
    @ApiOperation(value = "计划表4-添加", notes = "计划表4-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Plan4 plan4) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        plan4.setUpdateTime(new Date());
        plan4.setUpdateBy(sysUser.getUsername());
        plan4Service.save(plan4);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param plan4
     * @return
     */
    @AutoLog(value = "计划表4-编辑")
    @ApiOperation(value = "计划表4-编辑", notes = "计划表4-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Plan4 plan4) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        plan4.setUpdateTime(new Date());
        plan4.setUpdateBy(sysUser.getUsername());
        plan4Service.updateById(plan4);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "计划表4-通过id删除")
    @ApiOperation(value = "计划表4-通过id删除", notes = "计划表4-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        Plan4 plan4 = plan4Service.getById(id);
        if (plan4.getSendOrdersState() == 0) {
            plan4Service.removeById(id);
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
//	@AutoLog(value = "计划表4-批量删除")
//	@ApiOperation(value="计划表4-批量删除", notes="计划表4-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.plan4Service.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.ok("批量删除成功!");
//	}

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "计划表4-通过id查询")
    @ApiOperation(value = "计划表4-通过id查询", notes = "计划表4-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Plan4 plan4 = plan4Service.getById(id);
        if (plan4 == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(plan4);
    }

    /**
     * 导出excel
     * bai
     * 2020/5/28
     *
     * @param plan4
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(Plan4 plan4,
                                  @RequestParam(name = "explain", required = false) String explain,
                                  @RequestParam(name = "beginTime", required = false) String beginTime,
                                  @RequestParam(name = "endTime", required = false) String endTime) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "新品/临措";
        // 获取导出数据集
        List<Plan4Im> list = plan4Service.exportPlan4(plan4, explain, beginTime, endTime);
        // 导出 excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Plan4Im.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams());
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 导出汇总数据 excel
     * bai
     * 2020/5/28
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls2")
    public ModelAndView exportXls2(HttpServletRequest request, Plan4Vo plan4Vo) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "新品/临措";
        List<Plan4Vo> list = plan4Service.exportFeedbackSummary(plan4Vo);
        for (Plan4Vo vo : list) {
            vo.setBuildTime(plan4Vo.getBeginTime() + " —— " + plan4Vo.getEndTime());    //起始日期
            vo.setDecommissioningDate(plan4Vo.getDecommissioningDate());                  //退役日期
            vo.setItemSlip(plan4Vo.getItemSlip());                                        //交接单号
            vo.setDescription(plan4Vo.getDescription());                                  //情况说明
            vo.setRemark(plan4Vo.getRemark());                                            //备注
            /*list.add(vo);*/
            break;
        }
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Plan4Vo.class);
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
     * @return
     */
    @Transactional
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        Material material = new Material();
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Integer in = 0;
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Plan4Im> list = ExcelImportUtil.importExcel(file.getInputStream(), Plan4Im.class, params);
                //List<Plan4> plan4List = CollectionCopyUtil.copyList(list, Plan4.class);
                Plan4 plan4 = new Plan4();
                for (Plan4Im plan4Im : list) {

                    //判断对象中属性值是否为空
                    Boolean flag = (StrUtil.isNotBlank(plan4Im.getEngName()) || StrUtil.isNotBlank(plan4Im.getProjectNo()) || StrUtil.isNotBlank(plan4Im.getCableName()) || StrUtil.isNotBlank(plan4Im.getCableCross()) || StrUtil.isNotBlank(plan4Im.getVoltageGrade()));
                    if (!flag) return Result.error("请补全电缆信息！");

                    // 属性拷贝 将 plan4Im 接收的excel数据转换为 plan4 实体类数据
                    BeanUtils.copyProperties(plan4Im, plan4);
                    plan4.setPlanType("电缆");
                    plan4.setSendOrdersState(0);
                    plan4.setCompleteState(0);
                    plan4.setUpdateBy(sysUser.getUsername());
                    plan4.setCreateBy(sysUser.getUsername());
                    plan4.setUpdateTime(new Date());
                    plan4.setCreateTime(new Date());
                    plan4Service.save(plan4);
                    in++;

                    /*String serial = plan4.getVoltageGrade().trim() + " " + plan4.getCableCross().trim();
                    QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
                    queryWrapper.like("serial",serial);
                    queryWrapper.like("name", serial);
                    List<Material> lists = materialService.list(queryWrapper);
                    if (lists.size() == 0) {
                        in++;
                        // 添加计划4同时对物料进行添加操作
                        for (int i = 0; i < 2; i++) {
                            if (i == 0) {
                                material.setSerial(serial + " 铜"); // 物料编号
                                material.setName(serial + " 铜");// 物料名称
                            }else if (i == 1) {
                                material.setSerial(serial + " 铝"); // 物料编号
                                material.setName(serial + " 铝");// 物料名称
                            }
                        materialService.save(material);
                        }
                    }*/
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

    /**
     * 计划表4配变电统计
     *
     * @return
     */
    @GetMapping(value = "/selectCable")
    public Result<?> selectCable(@RequestParam(name = "voltageGrade", required = false) String voltageGrade,
                                 @RequestParam(name = "beginTime", required = false) String beginTime,
                                 @RequestParam(name = "endTime", required = false) String endTime,
                                 @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                 @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<Plan4Vo> page = new Page<>(pageNo, pageSize);
        String planType = "电缆";
        IPage<Plan4Vo> pageList = plan4Service.selectCable(voltageGrade, beginTime, endTime, planType, page);
        return Result.ok(pageList);
    }

    /**
     * 导出电缆统计数据 excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls3")
    public ModelAndView exportXls3(HttpServletRequest request, Plan4ExcelVo plan4ExcelVo) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "电缆统计";
        List<Plan4ExcelVo> list = plan4Service.exportPlan3(plan4ExcelVo);
        Plan4ExcelVo plan4ExcelVo1 = new Plan4ExcelVo();
        BigDecimal length = BigDecimal.valueOf(0);
        BigDecimal weight = BigDecimal.valueOf(0);
        BigDecimal retrieval_weight = BigDecimal.valueOf(0);
        String proName = "";
        for (Plan4ExcelVo excelVo : list) {
            proName += excelVo.getEngName() + ",";
            length = length.add(BigDecimal.valueOf(Double.parseDouble(excelVo.getLength())));
            weight = weight.add(BigDecimal.valueOf(Double.parseDouble(excelVo.getWeight())));
            retrieval_weight = retrieval_weight.add(BigDecimal.valueOf(Double.parseDouble(excelVo.getRetrievalWeight())));
            excelVo.setEngName(null);
        }
        plan4ExcelVo1.setVoltageGrade("合计:");
        plan4ExcelVo1.setLength(length.toString());
        plan4ExcelVo1.setWeight(weight.toString());
        plan4ExcelVo1.setRetrievalWeight(retrieval_weight.toString());
        list.add(plan4ExcelVo1);
        list.get(0).setDecommissioningDate(plan4ExcelVo.getDecommissioningDate());
        list.get(0).setRemark(plan4ExcelVo.getRemark());
        list.get(0).setEngName(proName);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Plan4ExcelVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(plan4ExcelVo.getBeginTime() + "--" + plan4ExcelVo.getEndTime() + "/电缆统计", ""));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }
}
