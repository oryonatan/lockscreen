package org.example.lockscreen;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by yonatan on 21-Nov-15.
 */
public class LockScreenView extends RelativeLayout {
    private Context mActivityContext = null;
    private View mLayoutView = null;
    public LockScreenView(Context context) {
        super(context);
        mActivityContext = context;
        initView();
    }

    public LockScreenView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mActivityContext = context;
        initView();
    }

    public LockScreenView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mActivityContext = context;
        initView();
    }

    @TargetApi(21)
    public LockScreenView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mActivityContext = context;
        initView();
    }

    private void initView() {

    }

}
