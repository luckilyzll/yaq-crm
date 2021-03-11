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
import org.jeecg.modules.cable.entity.Material;
import org.jeecg.modules.cable.entity.Plan1;
import org.jeecg.modules.cable.entity.Plan2;
import org.jeecg.modules.cable.entity.Plan3;
import org.jeecg.modules.cable.importpackage.Plan3Im;
import org.jeecg.modules.cable.service.IMaterialService;
import org.jeecg.modules.cable.service.IPlan3Service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.vo.Plan3ExcelVo;
import org.jeecg.modules.cable.vo.Plan3Vo;
import org.jeecg.modules.cable.vo.SendOrdersVo;
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

/**
 * @Description: 计划表3
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "计划表3")
@RestController
@RequestMapping("/cable/plan3")
@Slf4j
public class Plan3Controller extends JeecgController<Plan3, IPlan3Service> {
    @Autowired
    private IPlan3Service plan3Service;

    @Autowired
    private IMaterialService materialService;

    @Autowired
    private ISysDictItemService sysDictItemService;

    /**
     * 计划3合并完单
     * 2020/8/28 bai
     *
     * @param plan3Vo 合并完单中的表单数据
     * @return 受影响的行数
     */
    @PostMapping(value = "/consolidationCompleted")
    public Result<?> consolidationCompleted(@RequestBody Plan3Vo plan3Vo) {
        Result<?> result = plan3Service.consolidationCompleted(Arrays.asList(plan3Vo.getPlan3Ids().split(",")), plan3Vo.getOperatorSchema(), plan3Vo.getPlan3ReceiptNo(), plan3Vo.getReceiptPhotos(), plan3Vo.getTaskTime(), plan3Vo.getCompleteOrderList());
        if (result.getCode() == 200) {
            return Result.ok(result.getMessage());
        } else {
            return Result.error(result.getMessage());
        }
    }

    /**
     * 查询计划3批量出库完单的数据
     * bai
     * 2020/8/28
     *
     * @param ids 批量出库完单 ids
     * @return 计划3批量出库完单的数据
     */
    @GetMapping(value = "/getPlan3ReceivingStorageList")
    public Result<?> getPlan3ReceivingStorageList(@RequestParam(name = "ids") String ids) {
        List<Plan3Vo> list = plan3Service.getPlan3ReceivingStorageList(Arrays.asList(ids.split(",")));
        for (Plan3Vo item : list) {
            if (!list.get(0).getProjectNo().equals(item.getProjectNo())) {
                return Result.error("工程账号必须一致");
            }
        }
        return Result.ok(list);
    }

    /**
     * 查询计划3批量入库完单的数据
     * bai
     * 2020/8/28
     *
     * @param ids 批量入库完单 ids
     * @return 计划3批量入库完单的数据
     */
    @GetMapping(value = "/getPlan3DeliverStorage")
    public Result<?> getPlan3DeliverStorage(@RequestParam(name = "ids") String ids) {
        List<Plan3> list = plan3Service.getPlan3DeliverStorage(Arrays.asList(ids.split(",")));
        for (Plan3 item : list) {
            if (!list.get(0).getProjectNo().equals(item.getProjectNo())) {
                return Result.error("工程账号必须一致");
            }
        }
        return Result.ok(list);
    }

    /**
     * 分页列表查询
     * bai
     * 2020/5/25
     *
     * @return
     */
    @AutoLog(value = "计划表3-分页列表查询")
    @ApiOperation(value = "计划表3-分页列表查询", notes = "计划表3-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(Plan3 plan3,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<Plan3> page = new Page<>(pageNo, pageSize);
        IPage<Plan3> pageList = plan3Service.pageList(plan3, page);
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
        List<Plan3> pageList = plan3Service.idsqueryRuList(Arrays.asList(ids.split(",")));
        for (int i = 0; i < pageList.size(); i++) {
            if (!pageList.get(0).getProjectNo().equals(pageList.get(i).getProjectNo()))
                return Result.error("工程账号必须一致");
            plan = new Plan1();
            plan.setId(pageList.get(i).getId());                                //计划id
            plan.setProjectNo(pageList.get(i).getProjectNo());                  //工程账号
            plan.setProjectName(pageList.get(i).getEngName());                  //项目名称
            plan.setWasteMaterialText(pageList.get(i).getMaterialDescribe());   //物料描述
            plan.setWasteMaterialCode(pageList.get(i).getMaterialCode());       //物料代码
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
        List<SendOrdersVo> pageList = plan3Service.idsqueryChuList(Arrays.asList(ids.split(",")));
        for (int i = 0; i < pageList.size(); i++) {
            if (!pageList.get(0).getProjectNo().equals(pageList.get(i).getProjectNo()))
                return Result.error("工程账号必须一致");
        }
        return Result.ok(pageList);
    }

    /**
     * 添加
     *
     * @param plan3
     * @return
     */
    @AutoLog(value = "计划表3-添加")
    @ApiOperation(value = "计划表3-添加", notes = "计划表3-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody Plan3 plan3) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        plan3.setUpdateTime(new Date());
        plan3.setUpdateBy(sysUser.getUsername());
        plan3Service.save(plan3);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param plan3
     * @return
     */
    @AutoLog(value = "计划表3-编辑")
    @ApiOperation(value = "计划表3-编辑", notes = "计划表3-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody Plan3 plan3) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        plan3.setUpdateTime(new Date());
        plan3.setUpdateBy(sysUser.getUsername());
        plan3Service.updateById(plan3);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "计划表3-通过id删除")
    @ApiOperation(value = "计划表3-通过id删除", notes = "计划表3-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        Plan3 plan3 = plan3Service.getById(id);
        if (plan3.getSendOrdersState() == 0) {
            plan3Service.removeById(id);
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
//	@AutoLog(value = "计划表3-批量删除")
//	@ApiOperation(value="计划表3-批量删除", notes="计划表3-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.plan3Service.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.ok("批量删除成功!");
//	}

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "计划表3-通过id查询")
    @ApiOperation(value = "计划表3-通过id查询", notes = "计划表3-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Plan3 plan3 = plan3Service.getById(id);
        if (plan3 == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(plan3);
    }

    /**
     * 导出excel
     * bai 2020/9/8
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(Plan3 plan3, @RequestParam(name = "explain", required = false) String explain) {
        // LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        // String title = "新品/临措";
        // 获取导出数据集
        List<Plan3Im> list = plan3Service.exportPlan3(plan3, explain);
        // 导出 excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        // mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Plan3Im.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams());
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

    /**
     * 通过excel导入数据
     * bai
     * 2020/5/28
     *
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
        // 单位之间的转换
        List<SysDictItem> dictItems = sysDictItemService.selectType("unit");
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<Plan3Im> list = ExcelImportUtil.importExcel(file.getInputStream(), Plan3Im.class, params);
                List<Plan3> plan3List = new ArrayList<>();
                List<Material> materialList = new ArrayList<>();
                QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
                for (Plan3Im plan3Im : list) {
                    //判断对象中属性值是否为空
                    Boolean flag = (StrUtil.isNotBlank(plan3Im.getEngName()) || StrUtil.isNotBlank(plan3Im.getProjectNo()) || StrUtil.isNotBlank(plan3Im.getMaterialCode()) || StrUtil.isNotBlank(plan3Im.getMaterialDescribe()) || StrUtil.isNotBlank(plan3Im.getProTheorderNo()));
                    if (!flag) return Result.error("请补全物料信息！");//只要有一个参数不为空的就新增一条数据

                    Plan3 plan3 = new Plan3();
                    for (SysDictItem dictItem : dictItems) {
                        // 导入时若单位不为空则进行单位转换
                        if (StrUtil.isNotBlank(plan3Im.getMeasuringUnit())) {
                            if (plan3Im.getMeasuringUnit().equals(dictItem.getItemText())) {
                                plan3.setMeasuringUnit(Integer.parseInt(dictItem.getItemValue()));
                            }
                        }
                    }
                    // 属性拷贝 将 plan3Im 接收的excel数据转换为 plan3 实体类数据
                    BeanUtils.copyProperties(plan3Im, plan3);
                    plan3.setPlanType(planType);
                    plan3.setSendOrdersState(0);
                    plan3.setCompleteState(0);
                    plan3.setUpdateBy(sysUser.getUsername());
                    plan3.setCreateBy(sysUser.getUsername());
                    plan3.setUpdateTime(new Date());
                    plan3.setCreateTime(new Date());
                    //todo 每次都向计划3集合中添加单个计划对象信息
                    plan3List.add(plan3);
                    in++;

                    // 根据计划3的物料代码构造条件
                    queryWrapper.eq("serial", plan3.getMaterialCode());
                    queryWrapper.eq("name", plan3.getMaterialDescribe());
                    // 根据条件查询数据库中是否存在此物料
                    List<Material> list1 = materialService.list(queryWrapper);
                    if (list1.size() == 0) {
                        if (StrUtil.isNotBlank(plan3.getMaterialCode()) && StrUtil.isNotBlank(plan3.getMaterialDescribe())) {
                            // 不存在此物料那么就向数据库中新增一条物料信息
                            material.setSerial(plan3.getMaterialCode());    // 物料编码
                            material.setName(plan3.getMaterialDescribe());  // 物料描述
                            material.setUnit(plan3.getMeasuringUnit());     // 物料单位
                            //todo 每次都向物料集合中添加单个物料对象信息
                            materialList.add(material);
                        }
                    }
                }
                // todo 最后通过 saveBatch 方法全部添加所有的物料和计划3中的数据到库中
                long start = System.currentTimeMillis();
                plan3Service.saveBatch(plan3List);
                materialService.saveBatch(materialList);
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
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
     * 计划表3新品统计
     *
     * @return
     */
    @GetMapping(value = "/selectNewproducts")
    public Result<?> selectNewproducts(@RequestParam(name = "materialDescribe", required = false) String materialDescribe,
                                       @RequestParam(name = "beginTime", required = false) String beginTime,
                                       @RequestParam(name = "endTime", required = false) String endTime,
                                       @RequestParam(name = "planType", required = false) String planType,
                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<Plan3Vo> page = new Page<>(pageNo, pageSize);
        IPage<Plan3Vo> pageList = plan3Service.selectNewproducts(materialDescribe, beginTime, endTime, planType, page);
        return Result.ok(pageList);
    }

    /**
     * 导出新品统计数据 excel
     *
     * @param request
     */
    @RequestMapping(value = "/exportXls2")
    public ModelAndView exportXls2(HttpServletRequest request, Plan3ExcelVo plan3ExcelVo) {
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        String title = "新品统计";
        List<Plan3ExcelVo> list = plan3Service.exportPlan2(plan3ExcelVo);
        Plan3ExcelVo plan3ExcelVo1 = new Plan3ExcelVo();
        BigDecimal warehousingNum = BigDecimal.valueOf(0);
        BigDecimal warehouseOutNum = BigDecimal.valueOf(0);
        String proName = "";
        for (Plan3ExcelVo excelVo : list) {
            proName += excelVo.getEngName() + ",";
            warehousingNum = warehousingNum.add(BigDecimal.valueOf(Double.parseDouble(excelVo.getWarehousingNum())));
            warehouseOutNum = warehouseOutNum.add(BigDecimal.valueOf(Double.parseDouble(excelVo.getWarehouseOutNum())));
            excelVo.setEngName(null);
        }
        plan3ExcelVo1.setMaterialDescribe("合计:");
        plan3ExcelVo1.setWarehousingNum(warehousingNum.toString());
        plan3ExcelVo1.setWarehouseOutNum(warehouseOutNum.toString());
        list.add(plan3ExcelVo1);
        list.get(0).setDecommissioningDate(plan3ExcelVo.getDecommissioningDate());
        list.get(0).setRemark(plan3ExcelVo.getRemark());
        list.get(0).setEngName(proName);
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, Plan3ExcelVo.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(plan3ExcelVo.getBeginTime() + "--" + plan3ExcelVo.getEndTime() + "/新品统计", ""));
        mv.addObject(NormalExcelConstants.DATA_LIST, list);
        return mv;
    }

}
