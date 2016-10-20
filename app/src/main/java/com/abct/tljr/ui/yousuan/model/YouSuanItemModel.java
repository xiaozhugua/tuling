package com.abct.tljr.ui.yousuan.model;

import com.abct.tljr.ui.yousuan.util.YouSuanShiShiChart;

import java.io.Serializable;

public class YouSuanItemModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 递增id
     */
    private int id;

    /**
     * 跟投的名字
     */
    private String name;

    /**
     * 年回报率
     */
    private double annualRate;

    /**
     * 最大回撤率
     */
    private double maxDrawDown;
    /**
     * 最大回撤天数
     */
    private int maxDrawDownPeriod;

    /**
     * 夏普比率
     */
    private double sharpRate;

    /**
     * 最少跟投的金额 以分为单位
     */
    private long leastMoney;

    /**
     * 曲线图的地址
     */
    private String curvePicUrl;


    /**
     * 饱和度
     */
    private String saturation;

    /**
     * 当月收益率
     */
    private double currentMoonRate;

    /**
     * 实盘收益率
     */
    private double actualRate;

    /**
     * 交易平率(月)
     */
    private int tradingCount;

    /**
     * 更新时间
     */
    private long updateDate;

    /**
     * 杠杆倍数
     */
    private int leverMultiple;

    /**
     * 收益风险比
     */
    private double EarningsRisk;

    /**
     * 交易总次数
     */
    private int dealCount;

    /**
     * 盈利的次数
     */
    private int profitCount;

    /**
     * 总盈利金额
     */
    private long profitMoney;

    /**
     * 总亏损金额
     */
    private long lossMoney;

    /**
     * 实盘天数
     */
    private int days;
    /**
     * 品种
     */
    private String variety;
    /**
     * 周期
     */
    private String period;
    /**
     * 持仓类型
     */
    private String PositionType;

    /**
     * 类别
     */
    private String category;

    /**
     * 详细说明
     */
    private String desc;

    private String key;

    /**
     * 胜率
     */
    private double odds;

    /**
     * 盈亏比
     */
    private double ProfitAndLossThan;

    /**
     * 每手建议配置资金
     */
    private int money;

    private long adviceMoney;

    /**
     * 最大回撤值
     */
    private double maxDrawDownMoney;

    private YouSuanShiShiChart youSuanShiShiChart;

    /**
     * 初始净值
     */
    private double chuShiJingZhi;

    /**
     * 最新净值
     */
    private double zuiXinJingZhi;

    /**
     * 总收益率
     */
    private double totalYield;

    /**
     * 最大回撤的发生时间
     */
    private long maxDrawDownMoneyStartTime;

    /**
     * 最大回撤的结束时间
     */
    private long maxDrawDownMoneyEndTime;

    /**
     * 最大回撤率开始时间
     */
    private long maxDrawDownStartTime;

    /**
     * 最大回撤率结束时间
     */
    private long maxDrawDownEndTime;


    public YouSuanItemModel(int id, String name, double annualRate, double maxDrawDown, int maxDrawDownPeriod, double sharpRate,
                            long leastMoney, String curvePicUrl, String saturation, double currentMoonRate, double actualRate,
                            int tradingCount, long updateDate, int leverMultiple, double EarningsRisk, int dealCount, int profitCount,
                            long profitMoney, long lossMoney, int days, String variety, String period, String PositionType, String category
            , String desc, String key, double odds, double ProfitAndLossThan, int money, long adviceMoney, double maxDrawDownMoney
            ,double chuShiJingZhi,double zuiXinJingZhi,double totalYield,long maxDrawDownMoneyStartTime,long maxDrawDownMoneyEndTime,
                            long maxDrawDownStartTime,long maxDrawDownEndTime) {
        this.id = id;
        this.name = name;
        this.annualRate = annualRate;
        this.maxDrawDown = maxDrawDown;
        this.maxDrawDownPeriod = maxDrawDownPeriod;
        this.sharpRate = sharpRate;
        this.leastMoney = leastMoney;
        this.curvePicUrl = curvePicUrl;

        this.saturation = saturation;
        this.currentMoonRate = currentMoonRate;
        this.actualRate = actualRate;
        this.tradingCount = tradingCount;
        this.updateDate = updateDate;
        this.leverMultiple = leverMultiple;
        this.EarningsRisk = EarningsRisk;
        this.dealCount = dealCount;
        this.profitCount = profitCount;
        this.profitMoney = profitMoney;
        this.lossMoney = lossMoney;
        this.days = days;
        this.variety = variety;
        this.period = period;
        this.PositionType = PositionType;
        this.category = category;
        this.desc = desc;
        this.key = key;
        this.odds = odds;
        this.ProfitAndLossThan = ProfitAndLossThan;
        this.money = money;
        this.adviceMoney = adviceMoney;
        this.maxDrawDownMoney = maxDrawDownMoney;
        this.chuShiJingZhi=chuShiJingZhi;
        this.zuiXinJingZhi=zuiXinJingZhi;
        this.totalYield=totalYield;
        this.maxDrawDownMoneyStartTime=maxDrawDownMoneyStartTime;
        this.maxDrawDownMoneyEndTime=maxDrawDownMoneyEndTime;
        this.maxDrawDownStartTime=maxDrawDownStartTime;
        this.maxDrawDownEndTime=maxDrawDownEndTime;
    }

    public void setChuShiJingZhi(double chuShiJingZhi) {
        this.chuShiJingZhi = chuShiJingZhi;
    }

    public void setZuiXinJingZhi(double zuiXinJingZhi) {
        this.zuiXinJingZhi = zuiXinJingZhi;
    }

    public void setTotalYield(double totalYield) {
        this.totalYield = totalYield;
    }

    public void setMaxDrawDownMoneyStartTime(long maxDrawDownMoneyStartTime) {
        this.maxDrawDownMoneyStartTime = maxDrawDownMoneyStartTime;
    }

    public void setMaxDrawDownMoneyEndTime(long maxDrawDownMoneyEndTime) {
        this.maxDrawDownMoneyEndTime = maxDrawDownMoneyEndTime;
    }

    public void setMaxDrawDownStartTime(long maxDrawDownStartTime) {
        this.maxDrawDownStartTime = maxDrawDownStartTime;
    }

    public void setMaxDrawDownEndTime(long maxDrawDownEndTime) {
        this.maxDrawDownEndTime = maxDrawDownEndTime;
    }

    public double getChuShiJingZhi() {
        return chuShiJingZhi;
    }

    public double getZuiXinJingZhi() {
        return zuiXinJingZhi;
    }

    public double getTotalYield() {
        return totalYield;
    }

    public long getMaxDrawDownMoneyStartTime() {
        return maxDrawDownMoneyStartTime;
    }

    public long getMaxDrawDownMoneyEndTime() {
        return maxDrawDownMoneyEndTime;
    }

    public long getMaxDrawDownStartTime() {
        return maxDrawDownStartTime;
    }

    public long getMaxDrawDownEndTime() {
        return maxDrawDownEndTime;
    }

    public YouSuanShiShiChart getYouSuanShiShiChart() {
        return youSuanShiShiChart;
    }

    public void setYouSuanShiShiChart(YouSuanShiShiChart youSuanShiShiChart) {
        this.youSuanShiShiChart = youSuanShiShiChart;
    }

    public double getMaxDrawDownMoney() {
        return maxDrawDownMoney;
    }

    public void setMaxDrawDownMoney(double maxDrawDownMoney) {
        this.maxDrawDownMoney = maxDrawDownMoney;
    }

    public long getAdviceMoney() {
        return adviceMoney;
    }

    public void setAdviceMoney(long adviceMoney) {
        this.adviceMoney = adviceMoney;
    }

    public double getOdds() {
        return odds;
    }

    public void setOdds(double odds) {
        this.odds = odds;
    }

    public double getProfitAndLossThan() {
        return ProfitAndLossThan;
    }

    public void setProfitAndLossThan(double profitAndLossThan) {
        ProfitAndLossThan = profitAndLossThan;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSaturation() {
        return saturation;
    }

    public void setSaturation(String saturation) {
        this.saturation = saturation;
    }

    public double getCurrentMoonRate() {
        return currentMoonRate;
    }

    public void setCurrentMoonRate(double currentMoonRate) {
        this.currentMoonRate = currentMoonRate;
    }

    public double getActualRate() {
        return actualRate;
    }

    public void setActualRate(double actualRate) {
        this.actualRate = actualRate;
    }

    public int getTradingCount() {
        return tradingCount;
    }

    public void setTradingCount(int tradingCount) {
        this.tradingCount = tradingCount;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public int getLeverMultiple() {
        return leverMultiple;
    }

    public void setLeverMultiple(int leverMultiple) {
        this.leverMultiple = leverMultiple;
    }

    public double getEarningsRisk() {
        return EarningsRisk;
    }

    public void setEarningsRisk(double earningsRisk) {
        EarningsRisk = earningsRisk;
    }

    public int getDealCount() {
        return dealCount;
    }

    public void setDealCount(int dealCount) {
        this.dealCount = dealCount;
    }

    public int getProfitCount() {
        return profitCount;
    }

    public void setProfitCount(int profitCount) {
        this.profitCount = profitCount;
    }

    public long getProfitMoney() {
        return profitMoney;
    }

    public void setProfitMoney(long profitMoney) {
        this.profitMoney = profitMoney;
    }

    public long getLossMoney() {
        return lossMoney;
    }

    public void setLossMoney(long lossMoney) {
        this.lossMoney = lossMoney;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getVariety() {
        return variety;
    }

    public void setVariety(String variety) {
        this.variety = variety;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getPositionType() {
        return PositionType;
    }

    public void setPositionType(String positionType) {
        PositionType = positionType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getAnnualRate() {
        return annualRate;
    }

    public void setAnnualRate(double annualRate) {
        this.annualRate = annualRate;
    }

    public double getMaxDrawDown() {
        return maxDrawDown;
    }

    public void setMaxDrawDown(double maxDrawDown) {
        this.maxDrawDown = maxDrawDown;
    }

    public int getMaxDrawDownPeriod() {
        return maxDrawDownPeriod;
    }

    public void setMaxDrawDownPeriod(int maxDrawDownPeriod) {
        this.maxDrawDownPeriod = maxDrawDownPeriod;
    }

    public double getSharpRate() {
        return sharpRate;
    }

    public void setSharpRate(double sharpRate) {
        this.sharpRate = sharpRate;
    }

    public long getLeastMoney() {
        return leastMoney;
    }

    public void setLeastMoney(long leastMoney) {
        this.leastMoney = leastMoney;
    }

    public String getCurvePicUrl() {
        return curvePicUrl;
    }

    public void setCurvePicUrl(String curvePicUrl) {
        this.curvePicUrl = curvePicUrl;
    }


}
