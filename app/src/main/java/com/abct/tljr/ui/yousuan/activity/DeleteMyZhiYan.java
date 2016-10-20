package com.abct.tljr.ui.yousuan.activity;

import android.os.Bundle;
import com.abct.tljr.BaseActivity;
import com.abct.tljr.MyApplication;
import com.abct.tljr.R;
import com.abct.tljr.model.ZhongchouBean;
import com.abct.tljr.ui.fragments.zhiyanFragment.adapter.MyViewAdapter;
import com.qh.common.listener.NetResult;
import com.qh.common.login.Configs;
import com.qh.common.model.User;
import com.qh.common.util.NetUtil;
import com.qh.common.util.UrlUtil;

import java.util.ArrayList;

import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

/**
 * Created by Administrator on 2016/5/27.
 */

public class DeleteMyZhiYan extends BaseActivity implements View.OnClickListener {

    public ArrayList<ZhongchouBean> listBean=null;
    public MyViewAdapter adapter=null;
    private RecyclerView recyclerView;
    private View back;
    private LinearLayoutManager manager;
    private Button deletezhiyan_quxiao;
    private Button deletezhiyan_quedin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tljr_deletezhiyan);
        recyclerView=(RecyclerView)findViewById(R.id.deleteRecycler);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        back=(View)findViewById(R.id.tljr_my_fanhui);
        back.setOnClickListener(this);
        deletezhiyan_quxiao=(Button)findViewById(R.id.deletezhiyan_quxiao);
        deletezhiyan_quxiao.setOnClickListener(this);
        deletezhiyan_quedin=(Button)findViewById(R.id.deletezhyan_quedin);
        deletezhiyan_quedin.setOnClickListener(this);
        //更新数据
        initData();
    }

    public void initData(){
        listBean= MyApplication.getInstance().getMainActivity().zhiyanFragment.mMyView.listBean;
        AddCheckData();
        adapter=new MyViewAdapter(this,listBean);
        recyclerView.setAdapter(adapter);
    }

    public void AddCheckData(){
        for(int i=0;i<listBean.size();i++){
            listBean.get(i).setCheckStatus(true);
        }
    }

    public void DeleteCheckData(){
        for(int i=0;i<listBean.size();i++){
            listBean.get(i).setCheckStatus(false);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeleteCheckData();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.tljr_my_fanhui:
                finish();
                break;
            case R.id.deletezhiyan_quxiao:
                for(int i=0;i<listBean.size();i++){
                    listBean.get(i).setCheckAction(false);
                }
                adapter.notifyDataSetChanged();
                break;
            case  R.id.deletezhyan_quedin:

                break;
        }
    }

    //删除众筹
    public void deleteZongcou(String beanid){
//        NetUtil.sendPost(UrlUtil.URL_ZR + "crowd/focus",
//                "id=" + bean.getId() + "&uid=" + User.getUser().getId() + "&token=" + Configs.token + "&type=" + type,
//                new NetResult() {
//                    @Override
//                    public void result(String s) {
//                    }
//                }
//        );
    }

}
