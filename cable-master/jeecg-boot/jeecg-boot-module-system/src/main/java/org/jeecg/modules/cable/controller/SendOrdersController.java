package org.jeecg.modules.cable.controller;

import java.math.BigDecimal;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.cable.entity.*;
import org.jeecg.modules.cable.service.*;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.vo.PlanVo;
import org.jeecg.modules.cable.vo.SendOrdersTaskVo;
import org.jeecg.modules.cable.vo.SendOrdersVo;
import org.jeecg.modules.cable.vo.TaskVo;
import org.jeecg.modules.demo.test.entity.JeecgOrderMain;
import org.jeecg.modules.demo.test.vo.JeecgOrderMainPage;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 派单表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "派单表")
@RestController
@RequestMapping("/cable/sendOrders")
@Slf4j
public class SendOrdersController extends JeecgController<SendOrders, ISendOrdersService> {
    @Autowired
    private ISendOrdersService sendOrdersService;

    @Autowired
    private ISendOrdersSubtabulationService sendOrdersSubtabulationService;

    @Autowired
    private IReceivingStorageService receivingStorageService;

    @Autowired
    private IDeliverStorageService deliverStorageService;

    @Autowired
    private IMaterialService materialService;

    @Autowired
    private IPlan1Service plan1Service;

    @Autowired
    private IPlan2Service plan2Service;

    @Autowired
    private IPlan3Service plan3Service;

    @Autowired
    private IPlan4Service plan4Service;

    @Autowired
    private IInventoryService inventoryService;

    @Autowired
    private IStorageLocationService storageLocationService;


    /**
     * 分页列表查询
     *
     * @param sendOrders
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "派单表-分页列表查询")
    @ApiOperation(value = "派单表-分页列表查询", notes = "派单表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(SendOrders sendOrders,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<SendOrders> queryWrapper = QueryGenerator.initQueryWrapper(sendOrders, req.getParameterMap());
        Page<SendOrders> page = new Page<SendOrders>(pageNo, pageSize);
        IPage<SendOrders> pageList = sendOrdersService.page(page, queryWrapper);
        return Result.ok(pageList);
    }

    /**
     * 合并派单添加
     * jsonObject 派单信息
     * ids 计划id的集合（字符串类型）
     * liu
     *
     * @return
     */
    @Transactional
    @AutoLog(value = "派单表-合并添加")
    @ApiOperation(value = "派单表-合并添加", notes = "派单表-合并添加")
    @PostMapping(value = "/mergePlanadd")
    public Result<?> MergePlan(@RequestBody SendOrdersVo sendOrdersVo) {
        sendOrdersService.saveMain(sendOrdersVo, sendOrdersVo.getJeecgOrderCustomerList(), sendOrdersVo.getJeecgOrderTicketList());
        return Result.ok("派单成功！");
    }

    /**
     * 派单编辑
     * sendOrdersVo 派单信息
     * ids 计划id的集合（字符串类型）
     * liu
     *
     * @return
     */
    @Transactional
    @AutoLog(value = "派单表-派单编辑")
    @ApiOperation(value = "派单表-派单编辑", notes = "派单表-派单编辑")
    @PostMapping(value = "/mergePlanEdit")
    public Result<?> MergePlanEdit(@RequestBody SendOrdersVo sendOrdersVo) {
        sendOrdersService.MergePlanEdit(sendOrdersVo, sendOrdersVo.getJeecgOrderCustomerList(), sendOrdersVo.getJeecgOrderTicketList());
        return Result.ok("派单编辑成功！");
    }

    /**
     * 合并派单添加
     * jsonObject 派单信息
     * ids 计划id的集合（字符串类型）
     * liu
     * @return
     */
    /*@Transactional
    @AutoLog(value = "派单表-合并添加")
    @ApiOperation(value = "派单表-合并添加", notes = "派单表-合并添加")
    @PostMapping(value = "/mergePlanadd")
    public Result<?> MergePlan(@RequestBody JSONObject jsonObject,
                               @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                               @RequestParam(name = "pageSize", defaultValue = "100") Integer pageSize) {
        List<Map<String,Object>> mapList = new ArrayList<Map<String,Object>>();

//        jsonObject.getString("ids");//获取 JSONObject 中的值

        //TODO 合并派单的共用 派单参数
        SendOrdersVo ordersVo = JSON.parseObject(jsonObject.toJSONString(), SendOrdersVo.class);

        //TODO 截取后的ids数组 转型为集合
        List<String> idlist = Arrays.asList(ordersVo.getIds().split(","));

        if(idlist.size() < 1) return Result.error("请选择要合并派单的工程！");

        //TODO 合并派单后 <<计划派单参数集合>>
        List<SendOrdersVo> sendOrdersVoList = new ArrayList<SendOrdersVo>();

        //TODO 计划派单参数 = 计划详情 + 派单参数
        SendOrdersVo sov;

        if(ordersVo.getPlanType().equals("1")) {
//            Page<Plan1> page1 = new Page<Plan1>(pageNo, pageSize);
            //TODO 根据 ids 查询计划详情
            List<Plan1> planList = plan1Service.idsqueryPageList(idlist);
            for (int i = 0; i < planList.size(); i++) {
                sov = new SendOrdersVo();
                //TODO 将共有部分派单参数赋值给该计划派单参数
                BeanUtils.copyProperties(ordersVo,sov);
                sov.setPlanId(planList.get(i).getId());//计划id，赋值 PlanId
                sov.setProjectNo(planList.get(i).getProjectNo());//项目编码，赋值 ProjectNo

                // 原物料编码，非空赋值给 Serial
                if (null != planList.get(i).getRawMaterialCode()) sov.setSerial(planList.get(i).getRawMaterialCode());
                // 旧物料编码，非空赋值给 Serial
                else if (null != planList.get(i).getWasteMaterialCode()) sov.setSerial(planList.get(i).getWasteMaterialCode());

                sendOrdersVoList.add(sov);
            }
        } else if (ordersVo.getPlanType().equals("2")) {
            Page<Plan2> page2 = new Page<Plan2>(pageNo, pageSize);
            List<Plan2> planList = plan2Service.idsqueryPageList2(idlist,page2).getRecords();
            for (int i = 0; i < planList.size(); i++) {
                sov = new SendOrdersVo();
                //TODO 将共有部分派单参数赋值给该计划派单参数
                BeanUtils.copyProperties(ordersVo,sov);
                sov.setPlanId(planList.get(i).getId()); //计划id

                sov.setSerial(planList.get(i).getReceiptNo());// 入库单号  作为物料编号

//              sov.setSerial(planList.get(i).getEquipmentNo());// 设备号  作为物料编号

                sov.setProjectNo(planList.get(i).getProjectNo());// 工程账号 或 项目编号
                sendOrdersVoList.add(sov);
            }
        } else if (ordersVo.getPlanType().equals("3")) {
            Page<Plan3> page3 = new Page<Plan3>(pageNo, pageSize);
            List<Plan3> planList = plan3Service.idsqueryPageList3(idlist,page3).getRecords();
            for (int i = 0; i < planList.size(); i++) {
                sov = new SendOrdersVo();
                //TODO 将共有部分派单参数赋值给该计划派单参数
                BeanUtils.copyProperties(ordersVo,sov);
                sov.setPlanId(planList.get(i).getId());//计划id
                sov.setSerial(planList.get(i).getMaterialCode());//物料代码
                sov.setProjectNo(planList.get(i).getProjectNo());//工程账号 或 项目编号
                sendOrdersVoList.add(sov);
            }
        } else if (ordersVo.getPlanType().equals("4")) {
            Page<Plan4> page4 = new Page<Plan4>(pageNo, pageSize);
            List<Plan4> planList = plan4Service.idsqueryPageList4(idlist,page4).getRecords();
            for (int i = 0; i < planList.size(); i++) {
                sov = new SendOrdersVo();
                //TODO 将共有部分派单参数赋值给该计划派单参数
                BeanUtils.copyProperties(ordersVo,sov);
                sov.setPlanId(planList.get(i).getId());//计划id
                sov.setSerial("cable2");//电缆2的物料编号定为“cable2”
                sov.setProjectNo(planList.get(i).getProjectNo());//工程账号 或 项目编号
                sendOrdersVoList.add(sov);
            }
        }
        //TODO 遍历所有派单信息，进行派单操作
        for (SendOrdersVo sendOrdersVo : sendOrdersVoList) {
            QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
            //TODO 根据物料编号查询物料信息 （serial）
            queryWrapper.eq("serial", sendOrdersVo.getSerial());
            List<Material> materialList = materialService.list(queryWrapper);
            if (materialList.size() < 1) return Result.error("该计划物料不存在！");
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            //TODO 派单表添加数据
            //添加成功后返回派单表 id
            Integer id = sendOrdersService.saveSendOrdersVo(sendOrdersVo, new Date(), sysUser.getUsername());
            if (sendOrdersVo.getOperatorSchema() == 0) {
                //派单类型（字典：0.出库，1.入库）
                ReceivingStorage receivingStorage = new ReceivingStorage();
                receivingStorage.setWarehouseId(sendOrdersVo.getWarehouseId());
                receivingStorage.setStorageLocationId(sendOrdersVo.getStorageLocationId());
                receivingStorage.setSendOrdersId(id);
                receivingStorage.setState(0);
                receivingStorage.setMaterialId(materialList.get(0).getId());
                //TODO 出库/完单表添加数据
                receivingStorageService.save(receivingStorage);
            } else {
                //派单类型（1.入库）
                DeliverStorage deliverStorage = new DeliverStorage();
                deliverStorage.setWarehouseId(sendOrdersVo.getWarehouseId());
                deliverStorage.setStorageLocationId(sendOrdersVo.getStorageLocationId());
                deliverStorage.setSendOrdersId(id);
                deliverStorage.setState(0);
                deliverStorage.setMaterialId(materialList.get(0).getId());
                //TODO 出库/完单表添加数据
                deliverStorageService.save(deliverStorage);
            }
            if (sendOrdersVo.getLicense() != null) {
                for (String s : sendOrdersVo.getLicense()) {
                    //TODO 派单-车辆-员工关系表添加 “车辆” 数据
                    sendOrdersSubtabulationService.saveSendOrdersSubtabulation(id, 0, s, sendOrdersVo.getTaskTime());
                }
            }
            if (sendOrdersVo.getRealname() != null) {
                for (String s : sendOrdersVo.getRealname()) {
                    //TODO 派单-车辆-员工关系表添加 “员工” 数据
                    sendOrdersSubtabulationService.saveSendOrdersSubtabulation(id, 1, s, sendOrdersVo.getTaskTime());
                }
            }
            //TODO 更新计划表（1、2、3、4） 的派单状态为“已派单”

            sendOrdersService.updatePlanState(sendOrdersVo.getPlanId(), sendOrdersVo.getPlanType());

            //更新计划表（1、2、3、4） 的派单状态为“已派单”    ------冗余逻辑
            if (sendOrdersVo.getPlanType().equals("1")) {
                Plan1 plan1 = plan1Service.getById(sendOrdersVo.getPlanId());
                plan1.setSendOrdersState(1);
                plan1Service.updateById(plan1);
            } else if (sendOrdersVo.getPlanType().equals("2")) {
                Plan2 plan2 = plan2Service.getById(sendOrdersVo.getPlanId());
                plan2.setSendOrdersState(1);
                plan2Service.updateById(plan2);
            } else if (sendOrdersVo.getPlanType().equals("3")) {
                Plan3 plan3 = plan3Service.getById(sendOrdersVo.getPlanId());
                plan3.setSendOrdersState(1);
                plan3Service.updateById(plan3);
            } else {
                Plan4 plan4 = plan4Service.getById(sendOrdersVo.getPlanId());
                plan4.setSendOrdersState(1);
                plan4Service.updateById(plan4);
            }
        }
        return Result.ok("派单成功！");
    }*/
    /*for (Plan2 plan2 : planList) {
            sendOrdersVo.setId(plan2.getId());//计划id
            sendOrdersVo.setSerial(plan2.getWarehouseContact());//物料编码
            sendOrdersVo.setProjectNo(plan2.getProjectNo());//工程账号 或 项目编号
        }
        for (Plan3 plan3 : planList) {
            sendOrdersVo.setId(plan3.getId());//计划id
            sendOrdersVo.setSerial(plan3.getWarehouseContact());//物料编码
            sendOrdersVo.setProjectNo(plan3.getProjectNo());//工程账号 或 项目编号
        }
        for (Plan4 plan4 : planList) {
            sendOrdersVo.setId(plan4.getId());//计划id
            sendOrdersVo.setSerial(plan4.getWarehouseContact());//物料编码
            sendOrdersVo.setProjectNo(plan4.getProjectNo());//工程账号 或 项目编号
        }*/

    /**
     * 添加
     *
     * @return
     */
    @Transactional
    @AutoLog(value = "派单表-添加")
    @ApiOperation(value = "派单表-添加", notes = "派单表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody JSONObject jsonObject) {
        SendOrdersVo sendOrdersVo = JSON.parseObject(jsonObject.toJSONString(), SendOrdersVo.class);
        QueryWrapper<Material> queryWrapper = new QueryWrapper<>();
        //TODO 根据物料编号查询物料信息 （serial）
        queryWrapper.eq("serial", sendOrdersVo.getSerial());
        List<Material> materialList = materialService.list(queryWrapper);
        if (materialList.size() > 0) {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            //TODO 派单表添加数据
            //添加成功后返回派单表 id
            Integer id = sendOrdersService.saveSendOrdersVo(sendOrdersVo, new Date(), sysUser.getUsername());
            /*if (sendOrdersVo.getOperatorSchema() == 0) {
                //派单类型（字典：0.出库，1.入库）
                ReceivingStorage receivingStorage = new ReceivingStorage();
                receivingStorage.setWarehouseId(sendOrdersVo.getWarehouseId());
                receivingStorage.setStorageLocationId(sendOrdersVo.getStorageLocationId());
                receivingStorage.setSendOrdersId(id);
                receivingStorage.setState(0);
                receivingStorage.setMaterialId(materialList.get(0).getId());
                //TODO 出库/完单表添加数据
                receivingStorageService.save(receivingStorage);
            } else {
                //派单类型（1.入库）
                DeliverStorage deliverStorage = new DeliverStorage();
                deliverStorage.setWarehouseId(sendOrdersVo.getWarehouseId());
                deliverStorage.setStorageLocationId(sendOrdersVo.getStorageLocationId());
                deliverStorage.setSendOrdersId(id);
                deliverStorage.setState(0);
                deliverStorage.setMaterialId(materialList.get(0).getId());
                //TODO 出库/完单表添加数据
                deliverStorageService.save(deliverStorage);
            }*/
            if (sendOrdersVo.getLicense() != null) {
                for (String s : sendOrdersVo.getLicense()) {
                    //TODO 派单-车辆-员工关系表添加 “车辆” 数据
                    sendOrdersSubtabulationService.saveSendOrdersSubtabulation(id, 0, s, sendOrdersVo.getTaskTime());
                }
            }
            if (sendOrdersVo.getRealname() != null) {
                for (String s : sendOrdersVo.getRealname()) {
                    //TODO 派单-车辆-员工关系表添加 “员工” 数据
                    sendOrdersSubtabulationService.saveSendOrdersSubtabulation(id, 1, s, sendOrdersVo.getTaskTime());
                }
            }
            //TODO 更新计划表（1、2、3、4） 的派单状态为“已派单”
            sendOrdersService.updatePlanState(sendOrdersVo.getPlanId(), sendOrdersVo.getPlanType());

            //更新计划表（1、2、3、4） 的派单状态为“已派单”
            /*if (sendOrdersVo.getPlanType().equals("1")) {
                Plan1 plan1 = plan1Service.getById(sendOrdersVo.getPlanId());
                plan1.setSendOrdersState(1);
                plan1Service.updateById(plan1);
            } else if (sendOrdersVo.getPlanType().equals("2")) {
                Plan2 plan2 = plan2Service.getById(sendOrdersVo.getPlanId());
                plan2.setSendOrdersState(1);
                plan2Service.updateById(plan2);
            } else if (sendOrdersVo.getPlanType().equals("3")) {
                Plan3 plan3 = plan3Service.getById(sendOrdersVo.getPlanId());
                plan3.setSendOrdersState(1);
                plan3Service.updateById(plan3);
            } else {
                Plan4 plan4 = plan4Service.getById(sendOrdersVo.getPlanId());
                plan4.setSendOrdersState(1);
                plan4Service.updateById(plan4);
            }*/
            return Result.ok("派单成功！");
        }
        return Result.error("该计划物料不存在！");
    }

    /**
     * 编辑
     * zhu
     * 2020-08-28
     *
     * @return
     */
    @AutoLog(value = "派单表-编辑")
    @ApiOperation(value = "派单表-编辑", notes = "派单表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SendOrdersVo sendOrdersVo) {
        SendOrders sendOrders = new SendOrders();
        sendOrders.setId(sendOrdersVo.getId());
        sendOrders.setBackup3(sendOrdersVo.getLinkman());
        sendOrders.setBackup2(sendOrdersVo.getAddress());
        sendOrders.setBackup4(sendOrdersVo.getPhone());
        sendOrdersService.updateById(sendOrders);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @Transactional
    @AutoLog(value = "派单表-通过id删除")
    @ApiOperation(value = "派单表-通过id删除", notes = "派单表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        //删除派单表信息
        sendOrdersService.removeSendOrders(id);
        return Result.ok("删除成功!");
    }

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "派单表-批量删除")
//	@ApiOperation(value="派单表-批量删除", notes="派单表-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.sendOrdersService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.ok("批量删除成功!");
//	}

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "派单表-通过id查询")
    @ApiOperation(value = "派单表-通过id查询", notes = "派单表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        SendOrders sendOrders = sendOrdersService.getById(id);
        if (sendOrders == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(sendOrders);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sendOrders
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SendOrders sendOrders) {
        return super.exportXls(request, sendOrders, SendOrders.class, "派单表");
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
        return super.importExcel(request, response, SendOrders.class);
    }

    /**
     * 根据年份和月份查询当月每日的车辆任务的数量
     *
     * @param date
     * @Author Xm
     * @Date 2020/5/21 12:09
     */
    @GetMapping(value = "/selectTheSameMonthSendOrders")
    public Result<?> selectTheSameMonthVehicleTask(String date) {
        List<TaskVo> list = sendOrdersService.selectTheSameMonthSendOrders(date);
        return Result.ok(list);
    }

    /**
     * 根据日期查询当天车辆任务信息
     *
     * @param date
     * @param pageNo
     * @param pageSize
     * @Author Xm
     * @Date 2020/5/21 12:09
     */
    @GetMapping(value = "/taskList")
    public Result<?> taskList(String date,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<SendOrdersTaskVo> page = new Page<SendOrdersTaskVo>(pageNo, pageSize);
        IPage<SendOrdersTaskVo> list = sendOrdersService.taskList(date, page);
        return Result.ok(list);
    }

    /**
     * 返回近5年年份
     *
     * @Author Xm
     * @Date 2020/5/22 14:07
     */
    @GetMapping(value = "/yearsList")
    public Result<?> yearsList() {
        List<String> list = sendOrdersService.yearsList();
        return Result.ok(list);
    }

    /**
     * 根据项目编号查询入库出库完单信息
     *
     * @param projectNo
     * @Author Xm
     * @Date 2020/5/25 17:03
     */
    @GetMapping(value = "/selectPlanAccomplish")
    public Result<?> selectPlanAccomplish(@RequestParam(name = "projectNo", required = true) String projectNo,
                                          @RequestParam(name = "planId", required = true) String planId,
                                          @RequestParam(name = "planType", required = true) String planType,
                                          @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                          @RequestParam(name = "sendOrdersId", required = true) String sendOrdersId) {
        if (sendOrdersId.equals("www")) {
            sendOrdersId = null;
        }
        Page<PlanVo> page = new Page<>(pageNo, pageSize);
        List<PlanVo> list = sendOrdersService.selectPlan2Accomplish(projectNo, planId, planType, sendOrdersId, page);
        return Result.ok(list);
    }

    /**
     * 完单操作
     *
     * @Author Xm
     * @Date 2020/5/27 10:52
     */
    @PutMapping(value = "/planedit")
    public Result<?> planedit(@RequestBody JSONObject jsonObject) {
        PlanVo planVo = JSON.parseObject(jsonObject.toJSONString(), PlanVo.class);
        Result<?> planedit = sendOrdersService.planedit(planVo);
        if (planedit.getCode() == 200) return Result.ok(planedit.getMessage());
        if (planedit.getCode() == 500) return Result.error(planedit.getMessage());
        return Result.ok(planedit);
    }

    /**
     * 派单记录
     * 根据计划id和计划类型查询历史派单信息
     * zhu
     * 2020-08-28
     *
     * @param ids
     * @param planType
     * @return
     */
    @GetMapping(value = "/selectSendOrdersController")
    public Result<?> selectSendOrdersController(@RequestParam(name = "ids", required = true) String ids,
                                                @RequestParam(name = "planType", required = true) String planType,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<SendOrdersVo> page = new Page<>(pageNo, pageSize);
        IPage<SendOrdersVo> iPage = sendOrdersService.selectSendOrdersController(ids, planType, page);
        return Result.ok(iPage);
    }

    /**
     * 今日派单
     * 查询当天所有的派单
     * zhu
     * 2020-08-28
     *
     * @return
     */
    @GetMapping(value = "/selectPlanSendOrdersTheSameDay")
    public Result<?> selectPlanSendOrdersTheSameDay(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<SendOrdersVo> page = new Page<>(pageNo, pageSize);
        IPage<SendOrdersVo> iPage = sendOrdersService.selectPlanSendOrdersTheSameDay(page);
        return Result.ok(iPage);
    }

    /**
     * 完单记录
     * 查询完单记录
     * zhu
     * 2020-08-28
     *
     * @return
     */
    @GetMapping(value = "/selectSendOrdersWD")
    public Result<?> selectSendOrdersWD(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                        @RequestParam(name = "ids", required = true) String ids,
                                        @RequestParam(name = "planType", required = true) String planType,
                                        @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Page<SendOrdersVo> page = new Page<>(pageNo, pageSize);
        IPage<SendOrdersVo> iPage = sendOrdersService.selectSendOrdersWD(ids, planType, page);
        return Result.ok(iPage);
    }


    /**
     * 通过id删除完单记录
     * 2020/9/7
     *
     * @param sendOrdersVo 要回退的内容数据
     * @param tableId      要删除的计划表1\2\3\4
     */
    @PutMapping(value = "/wddelete/{tableId}")
    public Result<?> wdDelete(@RequestBody SendOrdersVo sendOrdersVo, @PathVariable(name = "tableId") String tableId) {
        return sendOrdersService.deletelStoragesById(sendOrdersVo.getId(), sendOrdersVo.getPlanType(), sendOrdersVo, tableId);
    }

    /**
     * 完单记录编辑
     * zhu
     * 2020-08-28
     *
     * @param sendOrdersVo
     * @return
     */
    @AutoLog(value = "完单表-编辑")
    @ApiOperation(value = "完单表-编辑", notes = "完单表-编辑")
    @PutMapping(value = "/wdedit")
    @Transactional(rollbackFor = Exception.class)
    public Result<?> wdedit(@RequestBody SendOrdersVo sendOrdersVo) {
        Material material = new Material();
        if (sendOrdersVo.getPlanType().equals("出库")) {
            ReceivingStorage receivingStorage = new ReceivingStorage();
            SendOrdersVo sendOrdersVo1 = sendOrdersVo.getCompleteOrderList().get(0);
            //出库表ID
            receivingStorage.setId(sendOrdersVo.getId());
            //出库日期
            receivingStorage.setReceivingTime(sendOrdersVo.getTaskTime());
            //交接单号
            receivingStorage.setReceiptNo(sendOrdersVo.getReceiptNo());
            //回单照片
            receivingStorage.setReceiptPhotos(sendOrdersVo.getReceiptPhotos());

            //完单数量
            receivingStorage.setAccomplishNum(sendOrdersVo1.getAccomplishNum());
            //完单数量单位
            receivingStorage.setAccomplishNumUnit(sendOrdersVo1.getUnit());
            //容积
            receivingStorage.setAccomplishVolume(sendOrdersVo1.getAccomplishVolume());
            //自家仓库
            receivingStorage.setWarehouseId(sendOrdersVo1.getWarehouseId());
            //库位
            receivingStorage.setStorageLocationId(sendOrdersVo1.getStorageLocationId());
            //目标仓库
            receivingStorage.setBackup1(sendOrdersVo1.getEndWarehouseId());
            //情况说明
            receivingStorage.setAnnotation(sendOrdersVo1.getAnnotation());
            //计划四（材质和规格）
            if (sendOrdersVo.getPtype().equals(4)) {
                receivingStorage.setRecyclingSpecifications(sendOrdersVo1.getRawMaterialText());
                receivingStorage.setTexture(sendOrdersVo1.getTexture());
            }
            receivingStorageService.updateById(receivingStorage);

            // 拿到物料描述去查询物料信息,取出物料id来使用
            material = materialService.getOne(new QueryWrapper<Material>().eq("name", sendOrdersVo.getRawMaterialText() + " " + sendOrdersVo.getTexture()));
            if (null == material) return Result.error("没有查到该物料!");


            // 根据仓库、库位、项目编号、物料编号、资产编号查询要添加的库存信息
            QueryWrapper<Inventory> wrapper = new QueryWrapper<Inventory>();
            wrapper.eq("warehouse_id", sendOrdersVo.getZjid()); // 自家仓库id
            wrapper.eq("storage_location_id", sendOrdersVo.getKwid()); // 库位id
            if (sendOrdersVo.getPtype().equals("1")) {
                Plan1 plan1 = plan1Service.getOne(new QueryWrapper<Plan1>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan1.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("asset_no", plan1.getAssetNo()); // 资产编号
                wrapper.eq("backup1", sendOrdersVo.getPlanId()); // 计划 Id
                wrapper.eq("backup2", 1);  // 计划表1
                if (plan1.getAlreadyReceivingStorage() == null) {
                    plan1.setAlreadyReceivingStorage(sendOrdersVo1.getAccomplishNum());
                } else {
                    plan1.setAlreadyReceivingStorage(plan1.getAlreadyReceivingStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan1Service.updateById(plan1);
            }
            if (sendOrdersVo.getPtype().equals("2")) {
                Plan2 plan2 = plan2Service.getOne(new QueryWrapper<Plan2>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan2.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("asset_no", plan2.getRetiredAssetNo()); // 退役资产编号
                if (plan2.getAlreadyReceivingStorage() == null) {
                    plan2.setAlreadyReceivingStorage(sendOrdersVo1.getAccomplishNum());
                } else {
                    plan2.setAlreadyReceivingStorage(plan2.getAlreadyReceivingStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan2Service.updateById(plan2);
            }
            if (sendOrdersVo.getPtype().equals("3")) {
                Plan3 plan3 = plan3Service.getOne(new QueryWrapper<Plan3>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan3.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("asset_no", plan3.getProTheorderNo()); // 采购订单号
                if (plan3.getAlreadyReceivingStorage() == null) {
                    plan3.setAlreadyReceivingStorage(sendOrdersVo1.getAccomplishNum());
                } else {
                    plan3.setAlreadyReceivingStorage(plan3.getAlreadyReceivingStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan3Service.updateById(plan3);
            }
            if (sendOrdersVo.getPtype().equals("4")) {
                Plan4 plan4 = plan4Service.getOne(new QueryWrapper<Plan4>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan4.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("recycling_specifications", sendOrdersVo1.getRawMaterialText() + " " + sendOrdersVo1.getTexture()); // 电缆 =  规格 + ‘ ’ +材质
                wrapper.eq("backup1", sendOrdersVo.getPlanId()); // 计划 Id
                wrapper.eq("backup2", 4);  // 计划表 4
                if (plan4.getAlreadyReceivingStorage() == null) {
                    plan4.setAlreadyReceivingStorage(sendOrdersVo1.getAccomplishWeight());
                } else {
                    plan4.setAlreadyReceivingStorage(plan4.getAlreadyReceivingStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan4Service.updateById(plan4);
            }

            Inventory inventory = inventoryService.getOne(wrapper);
            if (inventory == null) return Result.error("没有查到该库存信息!");
            // 存在库存,在原本的库存数上减少数量，目标仓库增加数量
            inventory.setWarehouseId(sendOrdersVo1.getWarehouseId());
            inventory.setStorageLocationId(sendOrdersVo1.getStorageLocationId());
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            inventory.setUpdateTime(new Date());
            inventory.setUpdateBy(sysUser.getUsername());
            inventory.setBackup3(sendOrdersVo1.getUnit());
            inventory.setMaterialId(material.getId());
            if (sendOrdersVo.getPtype().equals("4")) {
                inventory.setMaterialId(materialService.getOne(new QueryWrapper<Material>().eq("name", sendOrdersVo1.getRawMaterialText() + " " + sendOrdersVo1.getTexture())).getId());
                inventory.setBackup5(sendOrdersVo1.getAccomplishWeight());
                inventory.setRecyclingSpecifications(sendOrdersVo1.getRawMaterialText());
            }
            inventory.setInventoryQuantity(inventory.getInventoryQuantity().add(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
            inventory.setBackup4(inventory.getBackup4().add(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getRjchange()))));
            inventoryService.updateById(inventory);

            if (sendOrdersVo1.getStorageLocationId() == sendOrdersVo.getKwid()) {
                StorageLocation storageLocation = storageLocationService.getById(sendOrdersVo1.getStorageLocationId());
                if (storageLocation == null) return Result.error("没有查到该库位信息!");
                storageLocation.setTheCurrentVolume(storageLocation.getTheCurrentVolume().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getRjchange()))));
                storageLocationService.updateById(storageLocation);
            }

            if (sendOrdersVo1.getStorageLocationId() != sendOrdersVo.getKwid()) {
                StorageLocation storageLocation = storageLocationService.getById(sendOrdersVo1.getStorageLocationId());
                if (storageLocation == null) return Result.error("没有查到该库位信息!");
                storageLocation.setTheCurrentVolume(storageLocation.getTheCurrentVolume().add(sendOrdersVo1.getAccomplishVolume()));
                storageLocationService.updateById(storageLocation);
                storageLocation = storageLocationService.getById(sendOrdersVo.getKwid());
                if (storageLocation == null) return Result.error("没有查到该库位信息!");
                storageLocation.setTheCurrentVolume(storageLocation.getTheCurrentVolume().subtract(sendOrdersVo1.getAccomplishVolume()));
                storageLocationService.updateById(storageLocation);
            }

        }
        if (sendOrdersVo.getPlanType().equals("入库")) {
            SendOrdersVo sendOrdersVo1 = sendOrdersVo.getCompleteOrderList().get(0);
            DeliverStorage deliverStorage = new DeliverStorage();
            //入口库表Id
            deliverStorage.setId(sendOrdersVo.getId());
            //出库日期
            deliverStorage.setDeliverTime(sendOrdersVo.getTaskTime());
            //交接单号
            deliverStorage.setReceiptNo(sendOrdersVo.getReceiptNo());
            //回单照片
            deliverStorage.setReceiptPhotos(sendOrdersVo.getReceiptPhotos());

            //完单数量
            //TODO 做计算 -------------------------------
            deliverStorage.setAccomplishNum(sendOrdersVo1.getAccomplishNum());
            //完单数量单位
            deliverStorage.setAccomplishNumUnit(sendOrdersVo1.getUnit());
            //容积
            //TODO 做计算 -------------------------------
            deliverStorage.setAccomplishVolume(sendOrdersVo1.getAccomplishVolume());
            //目标仓库ID
            deliverStorage.setWarehouseId(sendOrdersVo1.getWarehouseId());
            //库位
            deliverStorage.setStorageLocationId(sendOrdersVo1.getStorageLocationId());
            //是否异常
            deliverStorage.setSceneSituation(Integer.parseInt(sendOrdersVo1.getSceneSituation()));
            //异常原因
            deliverStorage.setAnomalousCause(sendOrdersVo1.getAnomalousCause());
            //异常图片
            deliverStorage.setScenePhotos(sendOrdersVo1.getScenePhotos1());
            //说明
            deliverStorage.setAnnotation(sendOrdersVo1.getAnnotation());
            //计划四（材质和规格）
            if (sendOrdersVo.getPtype().equals("4")) {
                //当编辑计划4 电缆时，更新规格 以及材质
                deliverStorage.setRecyclingSpecifications(sendOrdersVo1.getRawMaterialText());
                deliverStorage.setTexture(sendOrdersVo1.getTexture());
                //当编辑计划4 电缆时，更新重量 以及单位
                deliverStorage.setAccomplishWeight(sendOrdersVo1.getAccomplishWeight());
                deliverStorage.setAccomplishWeightUnit(2);//电缆重量默认吨
            }
            deliverStorageService.updateById(deliverStorage);

            // 查老的库存信息，使用老物料,取物料id来使用
            material = materialService.getOne(new QueryWrapper<Material>().eq("name", sendOrdersVo.getRawMaterialText() + " " + sendOrdersVo.getTexture()));
            if (null == material) return Result.error("没有查到该物料!");
            // 根据仓库、库位、项目编号、物料编号、资产编号查询要添加的库存信息
            QueryWrapper<Inventory> wrapper = new QueryWrapper<Inventory>();
            wrapper.eq("warehouse_id", sendOrdersVo.getZjid()); // 仓库id
            wrapper.eq("storage_location_id", sendOrdersVo.getKwid()); // 库位id
            if (sendOrdersVo.getPtype().equals("1")) {
                Plan1 plan1 = plan1Service.getOne(new QueryWrapper<Plan1>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan1.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("asset_no", plan1.getAssetNo()); // 资产编号
                wrapper.eq("backup1", sendOrdersVo.getPlanId()); // 计划 Id
                wrapper.eq("backup2", 1);  // 计划表1
                if (plan1.getAlreadyDeliverStorage() == null) {
                    plan1.setAlreadyDeliverStorage(sendOrdersVo1.getAccomplishNum());
                } else {
                    plan1.setAlreadyDeliverStorage(plan1.getAlreadyDeliverStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan1Service.updateById(plan1);
            }
            if (sendOrdersVo.getPtype().equals("2")) {
                Plan2 plan2 = plan2Service.getOne(new QueryWrapper<Plan2>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan2.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("asset_no", plan2.getRetiredAssetNo()); // 退役资产编号
                if (plan2.getAlreadyDeliverStorage() == null) {
                    plan2.setAlreadyDeliverStorage(sendOrdersVo1.getAccomplishNum());
                } else {
                    plan2.setAlreadyDeliverStorage(plan2.getAlreadyDeliverStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan2Service.updateById(plan2);
            }
            if (sendOrdersVo.getPtype().equals("3")) {
                Plan3 plan3 = plan3Service.getOne(new QueryWrapper<Plan3>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan3.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("asset_no", plan3.getProTheorderNo()); // 采购订单号
                if (plan3.getAlreadyDeliverStorage() == null) {
                    plan3.setAlreadyDeliverStorage(sendOrdersVo1.getAccomplishNum());
                } else {
                    plan3.setAlreadyDeliverStorage(plan3.getAlreadyDeliverStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan3Service.updateById(plan3);
            }
            if (sendOrdersVo.getPtype().equals("4")) {
                Plan4 plan4 = plan4Service.getOne(new QueryWrapper<Plan4>().eq("id", sendOrdersVo.getPlanId()));
                wrapper.eq("project_no", plan4.getProjectNo()); // 项目编号
                wrapper.eq("material_id", material.getId()); // 物料id
                wrapper.eq("recycling_specifications", sendOrdersVo1.getRawMaterialText() + " " + sendOrdersVo1.getTexture()); // 电缆 =  规格 + ‘ ’ +材质
                wrapper.eq("backup1", sendOrdersVo.getPlanId()); // 计划 Id
                wrapper.eq("backup2", 4);  // 计划表 4
                if (plan4.getAlreadyDeliverStorage() == null) {
                    plan4.setAlreadyDeliverStorage(sendOrdersVo1.getAccomplishWeight());
                } else {
                    plan4.setAlreadyDeliverStorage(plan4.getAlreadyDeliverStorage().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
                }
                plan4Service.updateById(plan4);
            }

            Inventory inventory = inventoryService.getOne(wrapper);
            if (inventory == null) return Result.error("没有查到该库存信息!");
            // 存在库存,在原本的库存数上增加入库数量即可
            inventory.setWarehouseId(sendOrdersVo1.getWarehouseId());
            inventory.setStorageLocationId(sendOrdersVo1.getStorageLocationId());
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            inventory.setUpdateTime(new Date());
            inventory.setUpdateBy(sysUser.getUsername());
            inventory.setBackup3(sendOrdersVo1.getUnit());
            inventory.setMaterialId(material.getId());
            if (sendOrdersVo.getPtype().equals("4")) {
                inventory.setMaterialId(materialService.getOne(new QueryWrapper<Material>().eq("name", sendOrdersVo1.getRawMaterialText() + " " + sendOrdersVo1.getTexture())).getId());
                inventory.setBackup5(sendOrdersVo1.getAccomplishWeight());
                inventory.setRecyclingSpecifications(sendOrdersVo1.getRawMaterialText());
            }
            inventory.setInventoryQuantity(inventory.getInventoryQuantity().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getSlchange()))));
            inventory.setBackup4(inventory.getBackup4().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getRjchange()))));
            inventoryService.updateById(inventory);

            if (sendOrdersVo1.getStorageLocationId() == sendOrdersVo.getKwid()) {
                StorageLocation storageLocation = storageLocationService.getById(sendOrdersVo1.getStorageLocationId());
                if (storageLocation == null) return Result.error("没有查到该库位信息!");
                storageLocation.setTheCurrentVolume(storageLocation.getTheCurrentVolume().subtract(BigDecimal.valueOf(Double.parseDouble(sendOrdersVo.getRjchange()))));
                storageLocationService.updateById(storageLocation);
            }

            if (sendOrdersVo1.getStorageLocationId() != sendOrdersVo.getKwid()) {
                StorageLocation storageLocation = storageLocationService.getById(sendOrdersVo1.getStorageLocationId());
                if (storageLocation == null) return Result.error("没有查到该库位信息!");
                storageLocation.setTheCurrentVolume(storageLocation.getTheCurrentVolume().add(sendOrdersVo1.getAccomplishVolume()));
                storageLocationService.updateById(storageLocation);
                storageLocation = storageLocationService.getById(sendOrdersVo.getKwid());
                if (storageLocation == null) return Result.error("没有查到该库位信息!");
                storageLocation.setTheCurrentVolume(storageLocation.getTheCurrentVolume().subtract(sendOrdersVo1.getAccomplishVolume()));
                storageLocationService.updateById(storageLocation);
            }


        }

        return Result.ok("编辑成功!");
    }


}
