package com.abct.tljr.service;

import java.util.Vector;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @author xbw 接收Umeng推送过来的分享广播
 * @version 创建时间：2015-6-2 上午10:29:06
 */
public class UmengPushReceiver extends BroadcastReceiver {
	Context context;
	IntentInfoVector intents = new IntentInfoVector();

	public UmengPushReceiver(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.umengPushReceiver");
		context.registerReceiver(this, filter);

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String sharetype = intent.getStringExtra("type");
		if (sharetype != null && sharetype.equals("share")) {
			intents.push(intent);
			// if (context instanceof MainActivity) {
			// new ReceiveDialog((MainActivity) context, intent).show();
			// } else if (context instanceof StartActivity) {
			// ((MainActivity) context).setIntent(intent);
			// }
			return;
		}

//		Log.i("mpush", "mymsg:" + intent.getStringExtra("mymsg"));
//
//		JSONObject jsonObj = JSONObject.parseObject(intent.getStringExtra(
//				"mymsg").toString());
//
//		String title = jsonObj.getString("title");
//		String author = jsonObj.getString("author");
//		String content = jsonObj.getString("content");
//		String id = jsonObj.getString("id");
//		String purl = jsonObj.getString("purl");
//		String sid = jsonObj.getString("sid");
//		String source = jsonObj.getString("source");
//		String species = jsonObj.getString("species");
//		String time = jsonObj.getString("time");
//		String type = jsonObj.getString("type");
//		String url = jsonObj.getString("url");
//		String zan="0";
//		if(jsonObj.containsKey("praise")){
//			zan = jsonObj.getString("praise");
//		}
//		
//
//		Log.i("mpush", "title" + title);
//
//		Intent notiIntent = new Intent(context, NewsActivity.class);
//		notiIntent.putExtra("name", title);
//		notiIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		Bundle bundle = new Bundle();
//		
//		News news = new News();
//		news.setTitle(title);
//		news.setTime(time);
//		news.setSource(source);
//		news.setId(id);
//		news.setLetterSpecies(species);
//		news.setUrl(url);
//		news.setType(type);
//		news.setContent(content);
//		news.setZan(zan);
//		 bundle.putSerializable("news", news);
//		
//		 
//		 
//		bundle.putSerializable("pushNews", "pushNews");
//		
//		
//		notiIntent.putExtras(bundle);
//		context.startActivity(notiIntent);


	}

	public Vector<?> getIntents() {
		return intents;
	}

	public boolean isNull() {
		return intents.isEmpty();
	}

}

class IntentInfoVector extends Vector<Object> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2747508756586413518L;

	public void push(Object x) {
		super.add(x); // 向队尾添加组件
	}

	public Object pop() { // 队首元素出队(从队列删除)
		Object x = super.elementAt(0); // 返回指定索引处的组件
		super.removeElementAt(0); // 删除指定索引处的组件
		return x;
	}

	public Object get() {
		Object x = super.elementAt(0); // 返回指定索引处的组件
		return x;
	}

	public void remove() {
		super.removeAllElements(); // removeAllElements()移除全部组件，并将其大小设置为零
	}
}