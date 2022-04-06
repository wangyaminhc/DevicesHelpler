package com.sssoft.base.view;



import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.loong.base.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 创建自定义的dialog，主要学习其实现原理
 * Created by chengguo on 2016/3/22.
 */
public class SelfDialog extends Dialog {

    private Button yes;//确定按钮
    private Button no;//取消按钮
    private TextView titleTv;//消息标题文本
    private TextView messageTv;//消息提示文本
    private String titleStr;//从外界设置的title文本
    private String messageStr;//从外界设置的消息文本
    private EditText rrneditText;
    private EditText checkCodeeditText;
    private String rrnEditStr = "";
    private String checkCodeEditStr = "";
    private Boolean CanceledOnTouchOutside= false;
    //确定文本和取消文本的显示内容
    private String yesStr, noStr;

    private Timer timer = null;
    private int count=-1;

    private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器
    
    private Boolean showRrnEdit= false;
    private Boolean showChkCodeEdit= false;
    
	public EditText getRrneditText() {
		return rrneditText;
	}

	public EditText getCheckCodeeditText() {
		return checkCodeeditText;
	}

	public String getRrnEditStr() {
		return rrnEditStr;
	}

	public String getCheckCodeEditStr() {
		return checkCodeEditStr;
	}

	/**
     * 设置取消按钮的显示内容和监听
     *
     * @param str
     * @param onNoOnclickListener
     */
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {
        if (str != null) {
            noStr = str;
        }
        this.noOnclickListener = onNoOnclickListener;
    }

    /**
     * 设置确定按钮的显示内容和监听
     *
     * @param str
     * @param onYesOnclickListener
     */
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {
        if (str != null) {
            yesStr = str;
        }
        this.yesOnclickListener = onYesOnclickListener;
    }

    public SelfDialog(Context context) {
        super(context, R.style.MyDialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.free_exercise_sure_dialog_layout);
        //按空白处不能取消动画
        setCanceledOnTouchOutside(CanceledOnTouchOutside);

        //初始化界面控件
        initView();
        //初始化界面数据
        initData();
        //初始化界面控件的事件
        initEvent();
        //初始化倒计时器
        initTimer();
    }

    /*
    * 设置计数器倒计时时间（s）
    * */
    public void setCount(int count)
    {
        this.count = count;
    }
    /*
    * 初始化倒计时器
    * */
    private void initTimer()
    {
        if(count>0) {
            timer = new Timer();
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    Message message = new Message();
                    message.arg1 = count;
                    if (count != -1) {
                        count--;
                    } else {
                        return;
                    }
                    localHandler.sendMessage(message);
                }
            }, 3000, 1000);
        }
    }
    private Handler localHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(count>=0) {
                StringBuffer buffer = new StringBuffer();
                buffer.append(noStr)
                        .append(" (").append(count).append("s)");
                no.setText(buffer.toString());
            } else {
                //倒计时结束，默认触发no按钮
                no.performClick();
                timer.cancel();
            }
        }
    };


    /**
     * 初始化界面的确定和取消监听器
     */
    private void initEvent() {
        //设置确定按钮被点击后，向外界提供监听
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (yesOnclickListener != null) {
                	rrnEditStr = rrneditText.getText().toString();
                	checkCodeEditStr = checkCodeeditText.getText().toString();
                    yesOnclickListener.onYesClick();
                }
                //重置计数器
                if(timer !=null) {
                    timer.cancel();
                    count =-1;
                }
            }
        });
        //设置取消按钮被点击后，向外界提供监听
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noOnclickListener != null) {
                    noOnclickListener.onNoClick();
                }
                //重置计数器
                if(timer !=null) {
                    timer.cancel();
                    count =-1;
                }
            }
        });
    }

    /**
     * 初始化界面控件的显示数据
     */
    private void initData() {
        //如果用户自定了title和message
        if (titleStr != null) {
            titleTv.setText(titleStr);
        }
        if (messageStr != null) {
            messageTv.setText(messageStr);
        }else{
        	messageTv.setVisibility(View.GONE);
        }
        if (yesStr != null) {
            yes.setText(yesStr);
        }else{
        	yes.setVisibility(View.GONE);
        }
        if (noStr != null) {
            no.setText(noStr);
        }else{
        	no.setVisibility(View.GONE);
        }
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        yes = (Button) findViewById(R.id.yes);
        no = (Button) findViewById(R.id.no);
        titleTv = (TextView) findViewById(R.id.title);
        messageTv = (TextView) findViewById(R.id.message);
        rrneditText = (EditText) findViewById(R.id.editTextChannelTxnNo);
        checkCodeeditText = (EditText) findViewById(R.id.checkCodeEdittext);
        if(showRrnEdit)
        	rrneditText.setVisibility(View.VISIBLE);
        else
        	rrneditText.setVisibility(View.GONE);
        if(showChkCodeEdit)
        	checkCodeeditText.setVisibility(View.VISIBLE);
        else
        	checkCodeeditText.setVisibility(View.GONE);
    }

    /**
     * 从外界Activity为Dialog设置标题
     *
     * @param title
     */
    public void setTitle(String title) {
        titleStr = title;
    }

    /**
     * 从外界Activity为Dialog设置dialog的message
     *
     * @param message
     */
    public void setMessage(String message) {
        messageStr = message;
    }
    public void setCanceled(Boolean isshow) {
    	CanceledOnTouchOutside = isshow;
    }
    public void showRrnEdit(Boolean flag) {
    	showRrnEdit = flag;
    }
    public void showChkCodeEdit(Boolean flag) {
    	showChkCodeEdit = flag;
    }
    /**
     * 设置确定按钮和取消被点击的接口
     */
    public interface onYesOnclickListener {
        public void onYesClick();
    }

    public interface onNoOnclickListener {
        public void onNoClick();
    }
    public void showKeyboard() {  
        if(rrneditText!=null){  
            //设置可获得焦点  
        	rrneditText.setFocusable(true);  
        	rrneditText.setFocusableInTouchMode(true);  
            //请求获得焦点  
        	rrneditText.requestFocus();  
            //调用系统输入法  
            InputMethodManager inputManager = (InputMethodManager) rrneditText
                    .getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(rrneditText, 0);  
        }  
    } 
}
