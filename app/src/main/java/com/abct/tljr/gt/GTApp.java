/*
 * Tencent is pleased to support the open source community by making
 * Tencent GT (Version 2.4 and subsequent versions) available.
 *
 * Notwithstanding anything to the contrary herein, any previous version
 * of Tencent GT shall not be subject to the license hereunder.
 * All right, title, and interest, including all intellectual property rights,
 * in and to the previous version of Tencent GT (including any and all copies thereof)
 * shall be owned and retained by Tencent and subject to the license under the
 * Tencent GT End User License Agreement (http://gt.qq.com/wp-content/EULA_EN.html).
 * 
 * Copyright (C) 2015 THL A29 Limited, a Tencent company. All rights reserved.
 * 
 * Licensed under the MIT License (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://opensource.org/licenses/MIT
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.abct.tljr.gt;

import com.abct.tljr.MyApplication;
import com.abct.tljr.data.Constant;
import com.tencent.wstt.gt.client.AbsGTParaLoader;
import com.tencent.wstt.gt.client.GT;
import com.tencent.wstt.gt.client.InParaManager;
import com.tencent.wstt.gt.client.OutParaManager;

import android.content.Context;

public class GTApp extends Thread {
	public static final String 内存 = "已用内存";
	public static final String 流量 = "流量";
	public static final String CPU = "CPU";

	private boolean running;

	public void setRunning(boolean running) {
		this.running = running;
	}

	private CpuUtils utils;
	private long l;

	public GTApp() {
		super();
		/*
		 * GT usage 与GT控制台连接，同时注册输入输出参数
		 */
		GT.connect(MyApplication.getInstance(), new AbsGTParaLoader() {

			@Override
			public void loadInParas(InParaManager inPara) {
			}

			@Override
			public void loadOutParas(OutParaManager outPara) {
				outPara.register(内存, "内存");
				outPara.register(流量, "流量");
				outPara.register(CPU, "CPU");

				outPara.defaultOutParasInAC(内存, 流量, CPU);
			}
		});

		// 默认在GT一连接后就展示悬浮窗（GT1.1支持）
		GT.setFloatViewFront(true);

		// 默认打开性能统计开关（GT1.1支持）
		GT.setProfilerEnable(true);
		utils = new CpuUtils();
	}

	@Override
	public void run() {

		while (running) {
			try {
				// send the thread to sleep for a short period
				// very useful for battery saving
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			MyApplication.getInstance().getUidByte();
			GT.setOutPara(流量, Constant.Liuliang - l + "KB");
			l = Constant.Liuliang;
			GT.setOutPara(内存, MemUtils.getFreeAndTotalMem());
			// GT.setOutPara(CPU, CpuUtils.getCpuUsage() + "/" +
			// CpuUtils.getCpuUsage0() + "/" + CpuUtils.getCpuUsage1());
			GT.setOutPara(CPU, utils.getProcessCpuUsage(android.os.Process.myPid()));
		}
	}

}
