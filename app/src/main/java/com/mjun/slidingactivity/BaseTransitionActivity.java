package com.mjun.slidingactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import com.mjun.mtransition.ITransitPrepareListener;
import com.mjun.mtransition.MTransition;
import com.mjun.mtransition.MTransitionManager;
import com.mjun.mtransition.MTransitionView;
import com.mjun.mtransition.MTranstionUtil;
import com.mjun.mtransition.TransitListenerAdapter;

/**
 * Created by huijun on 2018/6/11.
 */

public class BaseTransitionActivity extends Activity {

    private String mFromActivityId = "";
    private FullMaskView mWrapperView;
    private View mMaskView;

    private int mDownX = 0;
    private boolean mHasBeginDrag = false;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        long id = intent.getLongExtra("_fromActivity", -1);
        if (id != -1) {
            mFromActivityId = String.valueOf(id);
        }
    }

    private void startTransition() {
        final MTransition transition = MTransitionManager.getInstance().getTransition(mFromActivityId);
        if (transition != null && mWrapperView != null) {
            transition.toPage().setContainer(mWrapperView, new ITransitPrepareListener() {
                @Override
                public void onPrepare(MTransitionView container) {
                    int width = container.getWidth();
                    transition.fromPage().getContainer().translateX(0, -width / 4);
                    if (transition.fromPage().getTransitionView("_mask") != null) {
                        transition.fromPage().getTransitionView("_mask").alpha(0f, 1f);
                    }
                    container.translateX(container.getWidth(), 0);
                    transition.setDuration(300);
                    transition.start();
                }
            });

            transition.setOnTransitListener(new TransitListenerAdapter() {
                @Override
                public void onTransitEnd(MTransition transition, boolean reverse) {
                    if (reverse) {
                        finish();
                        MTranstionUtil.removeActivityAnimation(BaseTransitionActivity.this);
                    }
                }
            });
        }
    }

    @Override
    public void startActivity(Intent intent) {
        beforeStartActivity(intent);
        super.startActivity(intent);
        MTranstionUtil.removeActivityAnimation(this);
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        beforeStartActivity(intent);
        super.startActivity(intent, options);
        MTranstionUtil.removeActivityAnimation(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        beforeStartActivity(intent);
        super.startActivityForResult(intent, requestCode);
        MTranstionUtil.removeActivityAnimation(this);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        beforeStartActivity(intent);
        super.startActivityForResult(intent, requestCode, options);
        MTranstionUtil.removeActivityAnimation(this);
    }

    private void beforeStartActivity(Intent intent) {
        long activityId = System.currentTimeMillis();
        intent.putExtra("_fromActivity", activityId);
        if (mWrapperView != null) {
            final MTransition transition = MTransitionManager.getInstance().createTransition(String.valueOf(activityId));
            transition.fromPage().setContainer(mWrapperView, new ITransitPrepareListener() {
                @Override
                public void onPrepare(MTransitionView container) {
                    if (mMaskView != null) {
                        transition.fromPage().addTransitionView("_mask", mMaskView);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MTransitionManager.getInstance().destoryTransition(mFromActivityId);
    }

    @Override
    public void onBackPressed() {
        final MTransition transition = MTransitionManager.getInstance().getTransition(mFromActivityId);
        if (transition != null) {
            transition.reverse();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initAfterSetContentView();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initAfterSetContentView();
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        initAfterSetContentView();
    }

    private void initAfterSetContentView() {
        ViewGroup rootView = (ViewGroup)getWindow().getDecorView().findViewById(android.R.id.content);
        if (rootView != null && rootView.getChildCount() != 0) {
            View container = rootView.getChildAt(0);
            rootView.removeView(container);
            mWrapperView = new FullMaskView(this);
            mMaskView = new View(this);
            mMaskView.setBackgroundColor(0x88000000);
            mMaskView.setAlpha(0f);
            mWrapperView.addView(container);
            mWrapperView.addView(mMaskView);
            rootView.addView(mWrapperView);
            startTransition();
        }
    }

    private class FullMaskView extends FrameLayout {

        public FullMaskView(Context context) {
            super(context);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            final MTransition transition = MTransitionManager.getInstance().getTransition(mFromActivityId);
            if (transition != null) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    mHasBeginDrag = false;
                    mDownX = (int) event.getX();
                    if (mDownX < 100) {
                        transition.onBeginDrag();
                        mHasBeginDrag = true;
                    }
                }
                if (mHasBeginDrag) {
                    return true;
                } else {
                    return super.onInterceptTouchEvent(event);
                }
            } else {
                return super.onTouchEvent(event);
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            final MTransition transition = MTransitionManager.getInstance().getTransition(mFromActivityId);
            if (transition != null) {
                int action = event.getAction();
                if (action == MotionEvent.ACTION_MOVE) {
                    if (mHasBeginDrag) {
                        int delta = (int)(event.getX() - mDownX);
                        transition.setProgress(1f - delta / (float)mWrapperView.getMeasuredWidth());
                    }
                } else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
                    if (mHasBeginDrag) {
                        int delta = (int)(event.getX() - mDownX);
                        float progress = 1f - delta / (float)mWrapperView.getMeasuredWidth();
                        if (progress < 0.7f) {
                            transition.gotoCeil();
                        } else {
                            transition.gotoFloor();
                        }
                    }
                }
                if (mHasBeginDrag) {
                    return true;
                } else {
                    return super.onTouchEvent(event);
                }
            } else {
                return super.onTouchEvent(event);
            }
        }
    }
}
