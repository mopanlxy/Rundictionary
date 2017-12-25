package com.example.mopan.runningdiary.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mopan.runningdiary.R;
import com.example.mopan.runningdiary.adapter.CommonAdapter;
import com.example.mopan.runningdiary.adapter.CommonViewHolder;
import com.example.mopan.runningdiary.step.bean.AverageStepsData;
import com.example.mopan.runningdiary.step.bean.StepData;
import com.example.mopan.runningdiary.step.utils.DbUtils;
import com.orhanobut.logger.Logger;

import java.util.List;

public class HistoryActivity extends Activity {
    private LinearLayout layout_titlebar;
    private ImageView iv_left;
    private ImageView iv_right;
    private ListView lv;
    private TextView tv_averagesteps;
    long AllSteps;
    int items;

    private void assignViews() {
        layout_titlebar = (LinearLayout) findViewById(R.id.layout_titlebar);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        lv = (ListView) findViewById(R.id.lv);
        tv_averagesteps = (TextView) findViewById(R.id.tv_averageSteps);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        assignViews();

        iv_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
        List<AverageStepsData> AveragestepDatas = DbUtils.getQueryAll(AverageStepsData.class);
        if(AveragestepDatas!= null && AveragestepDatas.size() != 0){

            tv_averagesteps.setText("Average Steps:"+AveragestepDatas.get(0).getStep()+"steps");
        }else{
            tv_averagesteps.setText("Average Steps:"+0+"steps");
        }
    }
    private void initData() {
        setEmptyView(lv);
        if(DbUtils.getLiteOrm()==null){
            DbUtils.createDb(this, "jingzhi");
        }
        final List<StepData> stepDatas = DbUtils.getQueryAll(StepData.class);
        Logger.d("stepDatas="+stepDatas);

        lv.setAdapter(new CommonAdapter<StepData>(this,stepDatas,R.layout.item) {


            @Override
            public int getCount() {

                return stepDatas.size();
            }

            @Override
            protected void convertView(View item, StepData stepData) {
                TextView tv_date= CommonViewHolder.get(item,R.id.tv_date);
                TextView tv_step= CommonViewHolder.get(item,R.id.tv_step);
                tv_date.setText(stepData.getToday());
                tv_step.setText(stepData.getStep()+"步");
                AllSteps += Long.parseLong(stepData.getStep());
            }
        });
    }

    protected <T extends View> T setEmptyView(ListView listView) {
        TextView emptyView = new TextView(this);
        emptyView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        emptyView.setText("暂无数据！");
        emptyView.setGravity(Gravity.CENTER);
        emptyView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        emptyView.setVisibility(View.GONE);
        ((ViewGroup) listView.getParent()).addView(emptyView);
        listView.setEmptyView(emptyView);
        return (T) emptyView;
    }
}
