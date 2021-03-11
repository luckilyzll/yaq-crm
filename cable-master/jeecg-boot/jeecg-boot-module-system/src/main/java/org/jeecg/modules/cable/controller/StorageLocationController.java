package org.jeecg.modules.cable.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.models.auth.In;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.cable.entity.Inventory;
import org.jeecg.modules.cable.entity.StorageLocation;
import org.jeecg.modules.cable.entity.Warehouse;
import org.jeecg.modules.cable.mapper.MaterialStorageLocationVoMapper;
import org.jeecg.modules.cable.service.IInventoryService;
import org.jeecg.modules.cable.service.IStorageLocationService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.cable.service.IWarehouseService;
import org.jeecg.modules.cable.utils.QRCodeUtil;
import org.jeecg.modules.cable.vo.InventoryListsVo;
import org.jeecg.modules.cable.vo.InventoryVo;
import org.jeecg.modules.cable.vo.StorageLocationVo;
import org.jeecg.modules.cable.vo.YikuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

/**
 * @Description: 库位表
 * @Author: jeecg-boot
 * @Date: 2020-05-22
 * @Version: V1.0
 */
@Api(tags = "库位表")
@RestController
@RequestMapping("/cable/storageLocation")
@Slf4j
public class StorageLocationController extends JeecgController<StorageLocation, IStorageLocationService> {
    @Autowired
    private IStorageLocationService storageLocationService;
    @Autowired
    private IInventoryService inventoryService;
    @Autowired
    private IWarehouseService warehouseService;
    @Value("${jeecg.path.upload}")
    private String QRCodeImg;

    /**
     * 根据库位id生成二维码图片存到数据库中并返回给前端
     * bai
     * 2020/7/3
     *
     * @param locationId
     * @return
     */
    @AutoLog(value = "根据库位id生成二维码图片存到数据库中并返回给前端")
    @ApiOperation(value = "根据库位id生成二维码图片存到数据库中并返回给前端")
    @GetMapping(value = "/showLocationQRCode")
    public Result<?> showLocationQRCode(@RequestParam(value = "locationId", required = true) Integer locationId) {
        // todo 库位信息
        StorageLocation storageLocation = storageLocationService.getById(locationId);
        // todo 仓库信息
        Warehouse warehouse = warehouseService.getById(storageLocation.getWarehouseId());
        // todo 仓库类型 [1自家仓库/2电力公司仓库]
        String warehouseType = warehouse.getType() == 1 ? "自家仓库" : "电力公司仓库";

        // 存放在二维码中的内容
        StringBuilder sb = new StringBuilder();
        sb.append("库位ID >>>" + locationId)
                .append("\n库位名称 >>>" + storageLocation.getStorageLocationName())
                .append("\n库位容积 >>>" + storageLocation.getStorageLocationVolume())
                .append("\n库位当前容积 >>>" + storageLocation.getTheCurrentVolume())
                .append("\n仓库名称 >>>" + warehouse.getName())
                .append("\n仓库地址 >>>" + warehouse.getAddress())
                .append("\n仓库类型 >>>" + warehouseType);

        // 生成的二维码的路径及名称
        String lookPath = "/temp/code" + System.currentTimeMillis() + ".jpg";   // 查看图片时所用路径
        String destPath = QRCodeImg + lookPath; // 生成图片时所用路径
        System.out.println("库位二维码绝对路径 >>>>>>>>>>>>>>>" + destPath);
        System.out.println("库位二维码相对路径 >>>>>>>>>>>>>>>" + lookPath);
        // 二维码图片路径
        storageLocation.setBackup1(destPath);
        storageLocationService.updateById(storageLocation);
        try {
            // 生成二维码
            QRCodeUtil.encode(sb.toString(), null, destPath, true);
            // 解析二维码
            String str = QRCodeUtil.decode(destPath);
            // 打印出解析出的内容
            System.out.println(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(lookPath);
    }

    @GetMapping(value = "/list2")
    public Result<?> queryPageList2(StorageLocation storageLocation) {
        QueryWrapper<StorageLocation> wrapper = new QueryWrapper<StorageLocation>();
        wrapper.eq("warehouse_id", storageLocation.getWarehouseId());
        if (storageLocation.getId() != null) {
            wrapper.ne("id", storageLocation.getId());
        }

        List<StorageLocation> all = storageLocationService.list(wrapper);
        return Result.ok(all);
    }

    /**
     * 分页列表查询
     *
     * @return
     */
    @AutoLog(value = "库位表-分页列表查询")
    @ApiOperation(value = "库位表-分页列表查询", notes = "库位表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(StorageLocationVo storageLocationVo) {
        QueryWrapper<StorageLocationVo> wrapper = new QueryWrapper<StorageLocationVo>();
        if (null != storageLocationVo.getWarehouseId()) {
            wrapper.eq("warehouse_id", storageLocationVo.getWarehouseId());
        }
        if (StrUtil.isNotBlank(storageLocationVo.getStorageLocationName())) {
            wrapper.like("storage_location_name", storageLocationVo.getStorageLocationName());
        }
        List<StorageLocationVo> list = new ArrayList<>();
        if (null != storageLocationVo.getWarehouseId()) {
            list = storageLocationService.getAll(wrapper);
        }
        return Result.ok(list);
    }

    /**
     * 添加库位
     * bai 2020/9/4
     *
     * @param storageLocation 要添加的库位信息
     * @return 受影响的行数
     */
    @AutoLog(value = "库位表-添加")
    @ApiOperation(value = "库位表-添加", notes = "库位表-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody StorageLocation storageLocation) {
        StorageLocation obj = storageLocationService.getOne(new QueryWrapper<StorageLocation>().eq("storage_location_name", storageLocation.getStorageLocationName()));
        if (obj != null) {
            return Result.error("库位已经存在,不能重复添加");
        }
        storageLocationService.save(storageLocation);
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param storageLocation
     * @return
     */
    @AutoLog(value = "库位表-编辑")
    @ApiOperation(value = "库位表-编辑", notes = "库位表-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody StorageLocation storageLocation) {
        storageLocationService.updateById(storageLocation);
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "库位表-通过id删除")
    @ApiOperation(value = "库位表-通过id删除", notes = "库位表-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        Page<InventoryListsVo> page = new Page<InventoryListsVo>(1, 10);
        InventoryListsVo inventoryVo = new InventoryListsVo();
        inventoryVo.setStorageLocationId(Integer.parseInt(id));
        IPage<InventoryListsVo> list = inventoryService.InsurancePageList(inventoryVo, page);
        if (list.getRecords().size() > 0) {
            return Result.error("该库位有库存，请先清空物料再进行删除!");
        } else {
            storageLocationService.removeById(id);
            return Result.ok("删除成功!");
        }
    }

//	/**
//	 *  批量删除
//	 *
//	 * @param ids
//	 * @return
//	 */
//	@AutoLog(value = "库位表-批量删除")
//	@ApiOperation(value="库位表-批量删除", notes="库位表-批量删除")
//	@DeleteMapping(value = "/deleteBatch")
//	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
//		this.storageLocationService.removeByIds(Arrays.asList(ids.split(",")));
//		return Result.ok("批量删除成功!");
//	}

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "库位表-通过id查询")
    @ApiOperation(value = "库位表-通过id查询", notes = "库位表-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        StorageLocation storageLocation = storageLocationService.getById(id);
        if (storageLocation == null) {
            return Result.error("未找到对应数据");
        }
        return Result.ok(storageLocation);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param storageLocation
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, StorageLocation storageLocation) {
        return super.exportXls(request, storageLocation, StorageLocation.class, "库位表");
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
        return super.importExcel(request, response, StorageLocation.class);
    }

    /**
     * 移库
     *
     * @param //materialStorageLocationVo
     * @return
     */
    @Transactional
    @PostMapping(value = "/MaterialStorageLocationEdit")
    public Result<?> MaterialStorageLocationEdit(@RequestBody JSONObject jsonObject) {
        //所有库存信息
        String id = jsonObject.getString("id");
        //数量
        BigDecimal amount = jsonObject.getBigDecimal("amount");
        //要转移的 库位
        Integer storageLocationCId = jsonObject.getInteger("storageLocationCId");
        //要转移到的 目标库位
        Integer storageLocationRId = jsonObject.getInteger("storageLocationRId");
        //单位
        Integer unit = jsonObject.getInteger("unit");
        String projectNo = jsonObject.getString("projectNo");
        String projectName = jsonObject.getString("projectName");
        String materialName = jsonObject.getString("materialName");
        String serial = jsonObject.getString("serial");
        //获取数量
        Inventory locations = inventoryService.getById(id);
        BigDecimal inventoryQuantity = locations.getInventoryQuantity();
        // 移动的数量和库存数量相等
        if (amount.doubleValue() == inventoryQuantity.doubleValue()) {
            QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("storage_location_id", storageLocationRId);
            queryWrapper.eq("backup1", locations.getBackup1());
            queryWrapper.eq("backup2", locations.getBackup2());
            queryWrapper.eq("backup3", locations.getBackup3());
            queryWrapper.eq("material_id", locations.getMaterialId());
            Inventory locations2 = inventoryService.getOne(queryWrapper);
            if (locations2 == null) {
                locations.setStorageLocationId(storageLocationRId);
                inventoryService.updateById(locations);
            } else {
                BigDecimal inventoryQuantity1 = locations2.getInventoryQuantity();
                locations2.setInventoryQuantity(inventoryQuantity1.add(amount));
                inventoryService.updateById(locations2);
                inventoryService.removeById(id);
            }
        } else if (amount.doubleValue() > inventoryQuantity.doubleValue()) {
            return Result.error("移库失败!剩余数量不足!");
        } else {
            //移动的数量小于库存数量
            QueryWrapper<Inventory> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("storage_location_id", storageLocationRId);
            queryWrapper.eq("backup1", locations.getBackup1());
            queryWrapper.eq("backup2", locations.getBackup2());
            queryWrapper.eq("backup3", locations.getBackup3());
            queryWrapper.eq("material_id", locations.getMaterialId());
            Inventory locations2 = inventoryService.getOne(queryWrapper);
            if (locations2 == null) {
                locations2 = new Inventory();
                locations2.setWarehouseId(locations.getWarehouseId());
                locations2.setStorageLocationId(storageLocationRId);
                locations2.setProjectNo(projectNo);
                locations2.setProjectName(locations.getProjectName());
                locations2.setMaterialId(locations.getMaterialId());
                locations2.setInventoryQuantity(new BigDecimal(0));
                locations2.setBackup1(locations.getBackup1());
                locations2.setBackup2(locations.getBackup2());
                locations2.setBackup3(locations.getBackup3());
                locations2.setBackup4(locations.getBackup4());
                inventoryService.save(locations2);
            }
            BigDecimal inventoryQuantity1 = locations2.getInventoryQuantity();
            locations2.setInventoryQuantity(inventoryQuantity1.add(amount));
            inventoryService.updateById(locations2);
            BigDecimal inventoryQuantity2 = locations.getInventoryQuantity();
            locations.setInventoryQuantity(inventoryQuantity2.subtract(amount));
            inventoryService.updateById(locations);
        }
        //容积
        BigDecimal backup4 = locations.getBackup4();
        //.setScale(2,BigDecimal.ROUND_HALF_UP)保留2位小数
        BigDecimal num = backup4.multiply(amount).setScale(2, BigDecimal.ROUND_HALF_UP);

        StorageLocation byId = storageLocationService.getById(storageLocationCId);
        BigDecimal theCurrentVolume = byId.getTheCurrentVolume();
        byId.setTheCurrentVolume(theCurrentVolume.subtract(num));
        storageLocationService.updateById(byId);

        StorageLocation byId2 = storageLocationService.getById(storageLocationRId);
        BigDecimal theCurrentVolume2 = byId2.getTheCurrentVolume();
        byId2.setTheCurrentVolume(theCurrentVolume2.add(num));
        storageLocationService.updateById(byId2);

        YikuVo y = new YikuVo();
        y.setCreateTime(new Date());
        y.setName1(storageLocationCId.toString());
        y.setName2(storageLocationRId.toString());
        y.setWcode(serial);
        y.setUnit(unit);
        y.setWuliaoName(materialName);
        y.setYksum(amount.toString());
        inventoryService.yikuAdd(y);
        return Result.ok("移库成功");
    }
}
