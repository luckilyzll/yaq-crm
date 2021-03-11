package org.jeecg.modules.cable.importpackage;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 物料信息导入 excel 工具表
 *
 * @Author: bai <bai211425401@126.com>
 * @Description: CSDN <https://blog.csdn.net/qq_43647359>
 * @Date: 2020/6/6
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class MaterialIm implements Serializable {
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 物料编号
     */
    @Excel(name = "物料编号", width = 15)
    private String serial;

    /**
     * 物料名称
     */
    @Excel(name = "物料名称", width = 15)
    private String name;

    /**
     * 规格型号
     */
    @Excel(name = "规格型号", width = 15)
    private String ations;

    /**
     * 供应商
     */
    @Excel(name = "供应商", width = 15)
    private String supplier;

    /**
     * 单位
     */
    @Excel(name = "单位", width = 15)
    private String unit;

    /**
     * 容积
     */
    @Excel(name = "容积", width = 15)
    private BigDecimal materialVolume;
}
