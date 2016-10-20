package com.abct.tljr.ui.yousuan.util;

import com.github.mikephil.charting.utils.ValueFormatter;

/**
 * Created by Administrator on 2016/5/26.
 */

public class DuoKongItemValueFormatter  implements ValueFormatter {

    @Override
    public String getFormattedValue(float value) {
        return (value/100)+"%";
    }
}
