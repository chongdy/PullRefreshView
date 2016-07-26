/**
 * Copyright 2015 Pengyuan-Jiang
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * <p/>
 * Author：Ybao on 2015/11/7 ‏‎0:27
 * <p/>
 * QQ: 392579823
 * <p/>
 * Email：392579823@qq.com
 */
package com.ybao.pullrefreshview.layout;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.ybao.pullrefreshview.support.impl.Refreshable;
import com.ybao.pullrefreshview.support.type.LayoutType;

public abstract class BaseHeaderView extends RelativeLayout implements Refreshable {

    public final static int NONE = 0;
    public final static int PULLING = 1;
    public final static int LOOSENT_O_REFRESH = 2;
    public final static int REFRESHING = 3;
    public final static int REFRESH_CLONE = 4;
    private int stateType = NONE;

    PullRefreshLayout refreshLayout;
    protected boolean isLockState = false;

    public BaseHeaderView(Context context) {
        this(context, null);
    }

    public BaseHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setFocusable(false);
        setFocusableInTouchMode(false);
    }

    @Override
    public void onScroll(FlingLayout flingLayout, float y) {
        if (isLockState) {
            return;
        }
        if (-y < getSpanHeight() && stateType != PULLING) {
            setState(PULLING);
        } else if (-y > getSpanHeight() && stateType != LOOSENT_O_REFRESH) {
            setState(LOOSENT_O_REFRESH);
        }
    }

    @Override
    public void setPullRefreshLayout(PullRefreshLayout refreshLayout) {
        this.refreshLayout = refreshLayout;
    }

    @Override
    public void onScrollChange(FlingLayout flingLayout, int state, float y) {
        if (state != FlingLayout.FLING) {
            return;
        }
        if (y != 0 && -y == getSpanHeight() && !isLockState) {
            isLockState = true;
            setState(REFRESHING);
        } else if (y == 0 && isLockState) {
        }

    }

    public void stopRefresh() {
        setState(REFRESH_CLONE);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                isLockState = false;
                setState(NONE);
                hide();
            }
        }, 400);
    }

    private void setState(int state) {
        this.stateType = state;
        if (state == REFRESHING && onRefreshListener != null) {
            onRefreshListener.onRefresh(this);
        }
        onStateChange(state);
    }

    public int getType() {
        return stateType;
    }


    public void show() {
        if (this.refreshLayout != null) {
            this.refreshLayout.openHeader();
        }
    }

    public void hide() {
        if (this.refreshLayout != null) {
            refreshLayout.closeHeader();
        }
    }

    protected abstract void onStateChange(int state);

    OnRefreshListener onRefreshListener;

    public interface OnRefreshListener {
        void onRefresh(BaseHeaderView baseHeaderView);
    }

    public void setOnRefreshListener(OnRefreshListener onRefreshListener) {
        this.onRefreshListener = onRefreshListener;
    }


    @Override
    public int moveTo(View terget, float y) {
        int layoutType = getLayoutType();
        if (layoutType == LayoutType.LAYOUT_SCROLLER) {
            ViewCompat.setTranslationY(terget, -y);
            ViewCompat.setTranslationY(this, getMeasuredHeight());
        } else if (layoutType == LayoutType.LAYOUT_DRAWER) {
            ViewCompat.setTranslationY(terget, 0);
            ViewCompat.setTranslationY(this, -y);
        } else {
            ViewCompat.setTranslationY(this, -y);
            ViewCompat.setTranslationY(terget, -y);
        }
        return 0;
    }
}
