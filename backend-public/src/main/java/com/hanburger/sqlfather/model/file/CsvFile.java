package com.hanburger.sqlfather.model.file;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.date.DateTime;
import lombok.Data;

import java.io.Serializable;

@Data
public class CsvFile implements Serializable {
    /**
     * 管井名称,管井ID,所属区域,维护单位,井类型,采集单位,采集时间,产权单位,管井用途,维护方式,生命周期状态,产权性质,业务级别,地形特征,所属工程,区域类型,
     * 原有名称,井盖材质,井类别,人井结构,井底长,井底宽,井底高,路边距,井底深度,上覆厚,道路名称,是否有电子锁,井盖形状,监理单位,参建单位,组织ID,
     * 固定资产编号,管井规格,是否危险点,MIS编号,是否局前井,所有权人,审验锁定标记,NFC编号,所属分公司,所属管理区域,备注一字段,质量责任人,
     * 具体位置,一线数据维护人,管孔数
     */
    @Alias("管井名称")
    private String pipeName;
    @Alias("管井ID")
    private Long pipeId;
    @Alias("所属区域")
    private String areaName;
    @Alias("维护单位")
    private String maintenanceUnit;
    @Alias("井类型")
    private String pipeType;
    @Alias("采集单位")
    private String collectionUnit;
    @Alias("采集时间")
    private DateTime collectionTime;
    @Alias("产权单位")
    private String propertyUnit;
    @Alias("管井用途")
    private String pipePurpose;
    @Alias("维护方式")
    private String maintenanceFunc;
    @Alias("生命周期状态")
    private String lifeCycle;
    @Alias("产权性质")
    private String propertyNature;
    @Alias("业务级别")
    private String businessLevel;
    @Alias("地形特征")
    private String locationFeature;
    @Alias("所属工程")
    private String relProject;
    @Alias("区域类型")
    private String areaType;
    @Alias("原有名称")
    private String orgName;
    @Alias("井盖材质")
    private String coverMaterial;
    @Alias("井类别")
    private String coverType;
    @Alias("人井结构")
    private String manholeStructure;
    @Alias("井底长")
    private Double bottomLength;
    @Alias("井底宽")
    private Double bottomWeight;
    @Alias("井底高")
    private Double bottomHeight;
    @Alias("路边距")
    private Double roadDistance;
    @Alias("井底深度")
    private String bottomDepth;
    @Alias("上覆厚")
    private Double coverThickness;
    @Alias("道路名称")
    private String roadName;
    @Alias("是否有电子锁")
    private String hasLock;
    @Alias("井盖形状")
    private String coverShape;
    @Alias("监理单位")
    private String checkUnit;
    @Alias("参建单位")
    private String participateUnit;
    @Alias("组织ID")
    private Long orgId;
    @Alias("固定资产编号")
    private String fixedAssetId;
    @Alias("是否危险点")
    private String pipeRule;
    @Alias("是否危险点")
    private String isDangerous;
    @Alias("MIS编号")
    private String msiNo;
    @Alias("是否局前井")
    private String isJqj;
    @Alias("所有权人")
    private String propertyPerson;
    @Alias("审验锁定标记")
    private String checkMark;
    @Alias("NFC编号")
    private String nfcNo;
    @Alias("所属分公司")
    private String relBranchCom;
    @Alias("所属管理区域")
    private String manageArea;
    @Alias("备注一字段")
    private String description;
    @Alias("质量责任人")
    private String qualityExaminer;
    @Alias("具体位置")
    private String location;
    @Alias("一线数据维护人")
    private String perDataManager;
    @Alias("管孔数")
    private String pipePointNum;
}
