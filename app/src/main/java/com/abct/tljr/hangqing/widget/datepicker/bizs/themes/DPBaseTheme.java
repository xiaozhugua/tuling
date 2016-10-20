package com.abct.tljr.hangqing.widget.datepicker.bizs.themes;



/**
 * 主题的默认实现类
 * 
 * The default implement of theme
 *
 * @author AigeStudio 2015-06-17
 */
public class DPBaseTheme extends DPTheme {
	
	 /**
     * 月视图背景色
     * Color of MonthView's background
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorBG() {
        return 0xFFFFFFFF;
    }

    /**
     * 背景圆颜色
     * Color of MonthView's selected circle
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorBGCircle() {
        return 0x88EB5041;
    }

    /**
     * 标题栏背景色
     * Color of TitleBar's background
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorTitleBG() {
        return 0xFFFFFFFF;
    }

    /**
     * 标题栏文本颜色
     * Color of TitleBar text
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorTitle() {
        return 0xEE323232;
    }

    /**
     * 今天的背景色
     * Color of Today's background
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorToday() {
        return 0x88F37B7A;
    }

    /**
     * 公历文本颜色
     * Color of Gregorian text
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorG() {
        return 0xEE333333;
    }

    /**
     * 节日文本颜色
     * Color of Festival text
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorF() {
        return 0xEEC08AA4;
    }

    /**
     * 周末文本颜色
     * Color of Weekend text
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorWeekend() {
        return 0xEBF78082;
    }

    /**
     * 假期文本颜色
     * Color of Holiday text
     * @return 16进制颜色值 hex color
     */
    @Override
    public int colorHoliday() {
        return 0xFFFFFFFF;
    }
    
}
