
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutManager;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PageIndicator extends View {
    private static final String TAG = "PageIndicator";
    private int mDotVisibleCount = 5;
    private int mDotCount = 4;
    private int mDotRadius = 20;
    private int mDotRadiusMin = 10;
    private int mDotDelta = 10;
    private int mDotColor = -1;
    private float mDotAlphaMin = 0.2F;
    private float mDotAlpha = 1.0F;
    private Paint mDotPaint;
    private float mScrollY = 0.0F;
    private float mScrollPercent = 0.0F;
    private int mCurrentSelectedIndex = 0;
    private int mFirstVisibleIndex;
    private float mStartedPercent;
    private int mNextSelectedIndex;
    private int mPageFirstIndex;

    public PageIndicator(Context context) {
        super(context);
        this.mFirstVisibleIndex = this.mCurrentSelectedIndex;
        this.mStartedPercent = 0.0F;
        this.mNextSelectedIndex = 0;
        this.mPageFirstIndex = 0;
        this.init();
    }

    public PageIndicator(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mFirstVisibleIndex = this.mCurrentSelectedIndex;
        this.mStartedPercent = 0.0F;
        this.mNextSelectedIndex = 0;
        this.mPageFirstIndex = 0;
        this.init();
        float dpi = this.getResources().getDisplayMetrics().density;
        this.mDotRadius = (int)(3.0F * dpi);
        this.mDotRadiusMin = (int)(1.75F * dpi);
    }

    public PageIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mFirstVisibleIndex = this.mCurrentSelectedIndex;
        this.mStartedPercent = 0.0F;
        this.mNextSelectedIndex = 0;
        this.mPageFirstIndex = 0;
        this.init();
    }

    private void init() {
        this.mDotPaint = new Paint();
        this.mDotPaint.setColor(this.mDotColor);
        if (this.isInEditMode()) {
            this.mDotCount = 2;
        }

    }

    public void setDotCount(int dotCount) {
        this.mDotCount = dotCount;
        this.mPageFirstIndex = 0;
        this.invalidate();
    }

    public void setDotCountAndStartPosition(int firstIndex, int count) {
        this.mPageFirstIndex = firstIndex;
        this.mDotCount = count;
        this.invalidate();
    }

    public void setSelectedIndex(int selectedIndex, int dotCount) {
        this.mCurrentSelectedIndex = selectedIndex;
        this.mNextSelectedIndex = selectedIndex;
        this.mDotCount = dotCount;
        if (dotCount > this.mDotVisibleCount) {
            this.mFirstVisibleIndex = this.mCurrentSelectedIndex - this.mDotVisibleCount / 2;
            if (this.mFirstVisibleIndex < 0) {
                this.mFirstVisibleIndex = 0;
            } else if (this.mFirstVisibleIndex > dotCount - this.mDotVisibleCount) {
                this.mFirstVisibleIndex = dotCount - this.mDotVisibleCount;
            }
        }

        this.invalidate();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int drawHeight = this.getMeasuredHeight() - this.getPaddingTop() - this.getPaddingBottom();
        this.mDotDelta = (drawHeight - (this.mDotVisibleCount + 2) * this.mDotRadius * 2) / (this.mDotVisibleCount + 1);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isScrollUp = this.mNextSelectedIndex > this.mCurrentSelectedIndex;
        if (this.mNextSelectedIndex == this.mCurrentSelectedIndex) {
            if (this.mDotCount > this.mDotVisibleCount) {
                this.drawScrollNone(canvas, this.mDotVisibleCount);
            } else {
                this.drawScrollNoneNormal(canvas, this.mDotCount);
            }
        } else if (this.mDotCount > this.mDotVisibleCount) {
            if (isScrollUp) {
                this.drawScrollUp(canvas, this.mDotVisibleCount + 1);
            } else {
                this.drawScrollDown(canvas, this.mDotVisibleCount + 1);
            }
        } else if (isScrollUp) {
            this.drawScrollUpNormal(canvas, this.mDotCount);
        } else {
            this.drawScrollDownNormal(canvas, this.mDotCount);
        }

    }

    private void drawScrollUpNormal(Canvas canvas, int dotCount) {
        float rx = this.getDrawStartX();
        float ry = this.getDrawStartY();
        float radius = (float)this.mDotRadius;
        int firstIndex = this.mFirstVisibleIndex;
        int selectIndex = this.mCurrentSelectedIndex - firstIndex;
        int nextSelectIndex = this.mNextSelectedIndex - firstIndex;

        for(int i = 0; i < dotCount; ++i) {
            float alpha;
            if (i == selectIndex) {
                alpha = this.mDotAlpha + (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else if (i == nextSelectIndex) {
                alpha = this.mDotAlphaMin - (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else {
                alpha = this.mDotAlphaMin;
            }

            this.mDotPaint.setAlpha((int)(alpha * 255.0F));
            canvas.drawCircle(rx, ry, radius, this.mDotPaint);
            ry = ry + (float)this.mDotDelta + (float)(this.mDotRadius * 2);
        }

    }

    private void drawScrollDownNormal(Canvas canvas, int dotCount) {
        float rx = this.getDrawStartX();
        float ry = this.getDrawStartY();
        int firstIndex = this.mFirstVisibleIndex;
        int selectIndex = this.mCurrentSelectedIndex - firstIndex;
        int nextSelectIndex = this.mNextSelectedIndex - firstIndex;
        float radius = (float)this.mDotRadius;

        for(int i = 0; i < dotCount; ++i) {
            float alpha;
            if (i == selectIndex) {
                alpha = this.mDotAlphaMin - (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else if (i == nextSelectIndex) {
                alpha = this.mDotAlpha + (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else {
                alpha = this.mDotAlphaMin;
            }

            this.mDotPaint.setAlpha((int)(alpha * 255.0F));
            canvas.drawCircle(rx, ry, radius, this.mDotPaint);
            ry = ry + (float)this.mDotDelta + (float)(this.mDotRadius * 2);
        }

    }

    private float getDrawStartY() {
        float ry;
        if (this.mDotCount <= this.mDotVisibleCount) {
            ry = (float)((this.getHeight() - this.getPaddingTop() - this.getPaddingBottom() - this.mDotCount * this.mDotRadius * 2 - this.mDotDelta * (this.mDotCount - 1)) / 2 + this.mDotRadius);
        } else if (this.mCurrentSelectedIndex == this.mNextSelectedIndex) {
            ry = (float)(this.getPaddingTop() + this.mDotRadius * 3 + this.mDotDelta);
        } else {
            ry = this.mScrollY + (float)this.getPaddingTop() + (float)(this.mDotRadius * 3) + (float)this.mDotDelta;
        }

        return ry;
    }

    private float getDrawStartX() {
        return (float)(this.getWidth() / 2);
    }

    private void drawScrollUp(Canvas canvas, int dotCount) {
        float rx = this.getDrawStartX();
        float ry = this.getDrawStartY();
        int firstIndex = this.mFirstVisibleIndex;
        int selectIndex = this.mCurrentSelectedIndex - firstIndex;
        int nextSelectIndex = this.mNextSelectedIndex - firstIndex;

        for(int i = 0; i < dotCount; ++i) {
            float radius;
            if (i == 0) {
                if (firstIndex == 0) {
                    if (nextSelectIndex > this.mDotVisibleCount / 2) {
                        radius = (float)this.mDotRadius + (float)(this.mDotRadius - this.mDotRadiusMin) * this.mScrollPercent;
                    } else {
                        radius = (float)this.mDotRadius;
                    }
                } else {
                    radius = (float)this.mDotRadiusMin;
                }
            } else if (i == 1) {
                if (this.mScrollY == 0.0F) {
                    radius = (float)this.mDotRadius;
                } else if (firstIndex >= 0) {
                    radius = (float)this.mDotRadius + (float)(this.mDotRadius - this.mDotRadiusMin) * this.mScrollPercent;
                } else {
                    radius = (float)this.mDotRadius;
                }
            } else if (i == dotCount - 2) {
                if (this.mScrollY == 0.0F) {
                    if (this.mFirstVisibleIndex == this.mDotCount - this.mDotVisibleCount) {
                        radius = (float)this.mDotRadius;
                    } else {
                        radius = (float)this.mDotRadiusMin;
                    }
                } else if (this.mFirstVisibleIndex == this.mDotCount - this.mDotVisibleCount) {
                    radius = (float)this.mDotRadius;
                } else {
                    radius = (float)this.mDotRadiusMin - (float)(this.mDotRadius - this.mDotRadiusMin) * this.mScrollPercent;
                }
            } else if (i == dotCount - 1) {
                if (this.mScrollY == 0.0F) {
                    if (this.mFirstVisibleIndex == this.mDotCount - this.mDotVisibleCount) {
                        radius = (float)this.mDotRadius;
                    } else {
                        radius = (float)this.mDotRadiusMin;
                    }
                } else if (this.mFirstVisibleIndex == this.mDotCount - this.mDotVisibleCount) {
                    radius = (float)this.mDotRadius;
                } else if (this.mNextSelectedIndex == this.mDotCount - this.mDotVisibleCount / 2 - 1) {
                    radius = (float)this.mDotRadiusMin - (float)(this.mDotRadius - this.mDotRadiusMin) * this.mScrollPercent;
                } else {
                    radius = (float)this.mDotRadiusMin;
                }
            } else {
                radius = (float)this.mDotRadius;
            }

            float alpha;
            if (i == selectIndex) {
                alpha = this.mDotAlpha + (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else if (i == nextSelectIndex) {
                alpha = this.mDotAlphaMin - (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else if (i == 0) {
                if (this.mScrollY == 0.0F) {
                    alpha = this.mDotAlphaMin;
                } else if (nextSelectIndex > this.mDotVisibleCount / 2) {
                    alpha = this.mDotAlphaMin + this.mDotAlphaMin * this.mScrollPercent;
                } else {
                    alpha = this.mDotAlphaMin;
                }
            } else if (i == dotCount - 1) {
                if (this.mScrollY == 0.0F) {
                    alpha = this.mDotAlphaMin;
                } else {
                    alpha = -this.mDotAlphaMin * this.mScrollPercent;
                }
            } else {
                alpha = this.mDotAlphaMin;
            }

            if ((i != 0 || ry + (float)this.mDotRadius >= (float)this.getPaddingTop()) && (i != dotCount - 1 || this.mScrollY != 0.0F && ry - (float)this.mDotRadius <= (float)(this.getHeight() - this.getPaddingBottom() - this.getPaddingTop()))) {
                this.mDotPaint.setAlpha((int)(alpha * 255.0F));
                canvas.drawCircle(rx, ry, radius, this.mDotPaint);
            }

            ry = ry + (float)this.mDotDelta + (float)(this.mDotRadius * 2);
        }

    }

    private void drawScrollDown(Canvas canvas, int dotCount) {
        float rx = this.getDrawStartX();
        float ry = this.getDrawStartY();
        Log.d("PageIndicator", "++++++ scroll down Y = " + this.mScrollY);
        Log.d("PageIndicator", "++++++ scroll down rY= " + ry);
        int firstIndex = this.mFirstVisibleIndex;
        int selectIndex = this.mCurrentSelectedIndex - firstIndex;
        int nextSelectIndex = this.mNextSelectedIndex - firstIndex;
        ++selectIndex;
        ++nextSelectIndex;

        for(int i = 0; i < dotCount; ++i) {
            float radius;
            if (i == 0) {
                if (this.mScrollY == (float)(-(this.mDotRadius * 2 + this.mDotDelta))) {
                    radius = (float)this.mDotRadius;
                } else if (this.mNextSelectedIndex == this.mDotVisibleCount / 2) {
                    radius = (float)this.mDotRadiusMin + (float)(this.mDotRadius - this.mDotRadiusMin) * (this.mScrollPercent + 1.0F);
                } else {
                    radius = (float)this.mDotRadiusMin;
                }
            } else if (i == 1) {
                if (firstIndex == 0) {
                    radius = (float)this.mDotRadius;
                } else if (this.mCurrentSelectedIndex > this.mDotCount - this.mDotVisibleCount / 2 - 1) {
                    radius = (float)this.mDotRadiusMin;
                } else if (this.mScrollY == 0.0F) {
                    radius = (float)this.mDotRadius;
                } else {
                    radius = (float)this.mDotRadiusMin + (float)(this.mDotRadius - this.mDotRadiusMin) * (this.mScrollPercent + 1.0F);
                }
            } else if (i == 2) {
                if (firstIndex == 0) {
                    radius = (float)this.mDotRadius;
                } else if (this.mCurrentSelectedIndex > this.mDotCount - this.mDotVisibleCount / 2 - 1) {
                    radius = (float)this.mDotRadius;
                } else if (this.mScrollY == 0.0F) {
                    radius = (float)this.mDotRadius;
                } else {
                    radius = (float)this.mDotRadius;
                }
            } else if (i == dotCount - 2) {
                if (firstIndex == 0) {
                    radius = (float)this.mDotRadius;
                } else if (this.mCurrentSelectedIndex > this.mDotCount - this.mDotVisibleCount / 2 - 1) {
                    radius = (float)this.mDotRadius;
                } else {
                    radius = (float)this.mDotRadius - (float)(this.mDotRadius - this.mDotRadiusMin) * (this.mScrollPercent + 1.0F);
                }
            } else if (i == dotCount - 1) {
                if (firstIndex == 0) {
                    radius = (float)this.mDotRadiusMin;
                } else if (this.mCurrentSelectedIndex > this.mDotCount - this.mDotVisibleCount / 2 - 1) {
                    radius = (float)this.mDotRadius;
                } else if (this.mCurrentSelectedIndex == this.mDotCount - this.mDotVisibleCount / 2 - 1) {
                    radius = (float)this.mDotRadius - (float)(this.mDotRadius - this.mDotRadiusMin) * (this.mScrollPercent + 1.0F);
                } else {
                    radius = (float)this.mDotRadiusMin;
                }
            } else {
                radius = (float)this.mDotRadius;
            }

            float alpha;
            if (i == selectIndex) {
                alpha = this.mDotAlphaMin - (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else if (i == nextSelectIndex) {
                alpha = this.mDotAlpha + (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else if (i == dotCount - 1) {
                if (this.mScrollY == (float)(-(this.mDotRadius * 2 + this.mDotDelta))) {
                    alpha = this.mDotAlphaMin;
                } else {
                    alpha = this.mDotAlphaMin - this.mDotAlphaMin * (1.0F + this.mScrollPercent);
                }
            } else if (i == 0) {
                if (this.mScrollY == (float)(-(this.mDotRadius * 2 + this.mDotDelta))) {
                    alpha = this.mDotAlphaMin;
                } else {
                    alpha = this.mDotAlphaMin * (1.0F + this.mScrollPercent);
                }
            } else {
                alpha = this.mDotAlphaMin;
            }

            if ((i != 0 || this.mScrollY != (float)(-(this.mDotRadius * 2 + this.mDotDelta)) && ry + (float)this.mDotRadius >= (float)this.getPaddingTop()) && (i != dotCount - 1 || ry - (float)this.mDotRadius <= (float)(this.getHeight() - this.getPaddingBottom() - this.getPaddingTop()))) {
                Log.d("PageIndicator", "++++++  index " + i + ", radius " + radius + "  dot count " + dotCount + "  ry " + ry);
                this.mDotPaint.setAlpha((int)(alpha * 255.0F));
                this.drawDot(canvas, rx, ry, radius, this.mDotPaint);
            }

            ry = ry + (float)this.mDotDelta + (float)(this.mDotRadius * 2);
        }

    }

    private void drawDot(Canvas canvas, float rx, float ry, float radius, Paint paint) {
        canvas.drawCircle(rx, ry, radius, paint);
    }

    private void drawScrollNone(Canvas canvas, int dotCount) {
        Log.d("PageIndicator", "draw none");
        float rx = this.getDrawStartX();
        float ry = this.getDrawStartY();
        int firstIndex = this.mFirstVisibleIndex;
        int selectIndex = this.mCurrentSelectedIndex - firstIndex;

        for(int i = 0; i < dotCount; ++i) {
            float radius;
            if (i == 0) {
                if (firstIndex == 0) {
                    if (this.mCurrentSelectedIndex > this.mDotVisibleCount / 2) {
                        radius = (float)this.mDotRadiusMin;
                    } else {
                        radius = (float)this.mDotRadius;
                    }
                } else {
                    radius = (float)this.mDotRadiusMin;
                }
            } else if (i == dotCount - 1) {
                if (this.mFirstVisibleIndex == this.mDotCount - this.mDotVisibleCount) {
                    radius = (float)this.mDotRadius;
                } else {
                    radius = (float)this.mDotRadiusMin;
                }
            } else {
                radius = (float)this.mDotRadius;
            }

            float alpha;
            if (i == selectIndex) {
                alpha = this.mDotAlpha + (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else {
                alpha = this.mDotAlphaMin;
            }

            if ((i != 0 || ry + (float)this.mDotRadius >= (float)this.getPaddingTop()) && (i != dotCount - 1 || ry - (float)this.mDotRadius <= (float)(this.getHeight() - this.getPaddingBottom() - this.getPaddingTop()))) {
                this.mDotPaint.setAlpha((int)(alpha * 255.0F));
                this.drawDot(canvas, rx, ry, radius, this.mDotPaint);
            }

            ry = ry + (float)this.mDotDelta + (float)(this.mDotRadius * 2);
        }

    }

    private void drawScrollNoneNormal(Canvas canvas, int dotCount) {
        Log.d("PageIndicator", "draw none");
        float rx = this.getDrawStartX();
        float ry = this.getDrawStartY();
        float radius = (float)this.mDotRadius;
        int firstIndex = this.mFirstVisibleIndex;
        int selectIndex = this.mCurrentSelectedIndex - firstIndex;

        for(int i = 0; i < dotCount; ++i) {
            float alpha;
            if (i == selectIndex) {
                alpha = this.mDotAlpha + (this.mDotAlpha - this.mDotAlphaMin) * this.mScrollPercent;
            } else {
                alpha = this.mDotAlphaMin;
            }

            this.mDotPaint.setAlpha((int)(alpha * 255.0F));
            this.drawDot(canvas, rx, ry, radius, this.mDotPaint);
            ry = ry + (float)this.mDotDelta + (float)(this.mDotRadius * 2);
        }

    }

    public void setScrollPosition(float percent, int index) {
        if (index < this.mPageFirstIndex + this.mDotCount - 1) {
            index -= this.mPageFirstIndex;
            if (index >= 0) {
                if (percent == 0.0F) {
                    Log.d("PageIndicator", "++++++++++++++ percent = 0");
                    if (index > this.mCurrentSelectedIndex) {
                        if (index <= this.mDotVisibleCount / 2) {
                            this.mFirstVisibleIndex = 0;
                        } else if (index == this.mDotCount - 1 && this.mDotCount < this.mDotVisibleCount) {
                            this.mFirstVisibleIndex = 0;
                        } else if (index >= this.mDotCount - this.mDotVisibleCount / 2) {
                            this.mFirstVisibleIndex = this.mDotCount - this.mDotVisibleCount;
                        } else {
                            this.mFirstVisibleIndex = index - this.mDotVisibleCount / 2;
                        }
                    } else if (index < this.mCurrentSelectedIndex) {
                        if (index <= this.mDotVisibleCount / 2) {
                            this.mFirstVisibleIndex = 0;
                        } else if (index >= this.mDotCount - this.mDotVisibleCount / 2 - 1) {
                            this.mFirstVisibleIndex = this.mDotCount - this.mDotVisibleCount;
                        } else {
                            this.mFirstVisibleIndex = index - this.mDotVisibleCount / 2;
                        }
                    }

                    this.mCurrentSelectedIndex = index;
                    this.mNextSelectedIndex = index;
                    this.mScrollPercent = 0.0F;
                } else if (this.mStartedPercent == 0.0F) {
                    this.mStartedPercent = percent;
                } else {
                    if (index == this.mCurrentSelectedIndex) {
                        this.mNextSelectedIndex = index + 1;
                    } else if (index + 1 == this.mCurrentSelectedIndex) {
                        this.mNextSelectedIndex = index;
                    } else if (index > this.mCurrentSelectedIndex) {
                        this.mNextSelectedIndex = index;
                        this.mCurrentSelectedIndex = index + 1;
                        this.mFirstVisibleIndex = this.mCurrentSelectedIndex - this.mDotVisibleCount / 2;
                        if (this.mFirstVisibleIndex > this.mDotCount - this.mDotVisibleCount) {
                            this.mFirstVisibleIndex = this.mDotCount - this.mDotVisibleCount;
                        }
                    } else if (index - 1 == this.mCurrentSelectedIndex) {
                        this.mNextSelectedIndex = index;
                    } else if (index < this.mCurrentSelectedIndex) {
                        this.mNextSelectedIndex = index;
                        this.mCurrentSelectedIndex = index - 1;
                        this.mFirstVisibleIndex = this.mCurrentSelectedIndex - this.mDotVisibleCount / 2;
                        if (this.mFirstVisibleIndex < 0) {
                            this.mFirstVisibleIndex = 0;
                        }
                    }

                    Log.d("PageIndicator", "------ 向上滑动" + (this.mNextSelectedIndex == this.mCurrentSelectedIndex));
                }

                this.mScrollPercent = percent;
                Log.d("PageIndicator", "scroll percent = " + percent);
                Log.d("PageIndicator", "started  selected dot precent = " + this.mStartedPercent);
                Log.d("PageIndicator", "        selected dot index = " + index);
                Log.d("PageIndicator", "current selected dot index = " + this.mCurrentSelectedIndex);
                Log.d("PageIndicator", "current selected dot nextIndex = " + this.mNextSelectedIndex);
                if (this.mNextSelectedIndex > this.mCurrentSelectedIndex) {
                    if (this.mCurrentSelectedIndex < this.mDotVisibleCount / 2) {
                        this.mScrollY = 0.0F;
                    } else if (this.mNextSelectedIndex >= this.mDotCount - this.mDotVisibleCount / 2) {
                        this.mScrollY = 0.0F;
                    } else {
                        this.mScrollY = percent * (float)(this.mDotRadius * 2 + this.mDotDelta);
                    }
                } else if (this.mNextSelectedIndex < this.mCurrentSelectedIndex) {
                    if (this.mNextSelectedIndex < this.mDotVisibleCount / 2) {
                        this.mScrollY = (float)(-(this.mDotRadius * 2 + this.mDotDelta));
                    } else if (this.mCurrentSelectedIndex >= this.mDotCount - this.mDotVisibleCount / 2) {
                        this.mScrollY = (float)(-(this.mDotRadius * 2 + this.mDotDelta));
                    } else {
                        this.mScrollY = percent * (float)(this.mDotRadius * 2 + this.mDotDelta);
                        Log.d("PageIndicator", "++++++ scroll Y = " + this.mScrollY);
                    }
                } else {
                    this.mScrollY = percent * (float)(this.mDotRadius * 2 + this.mDotDelta);
                }

                this.invalidate();
            }
        }
    }

    public void attachToRecyclerView(@NonNull RecyclerView recyclerView) {
        LayoutManager layoutManger = recyclerView.getLayoutManager();
        if (layoutManger == null) {
            Log.e("PageIndicator", "not supported");
        } else if (layoutManger instanceof LinearLayoutManager) {
            if (((LinearLayoutManager)layoutManger).getOrientation() == 1) {
                recyclerView.addOnScrollListener(new PageIndicator.IndicatorScrollListener(this, (LinearLayoutManager)layoutManger));
            } else {
                Log.e("PageIndicator", "not supported");
            }
        } else {
            Log.e("PageIndicator", "not supported");
        }

    }

    public static final class IndicatorScrollListener extends OnScrollListener {
        private PageIndicator mIndicator;
        private LinearLayoutManager mLinearLayoutManager;

        public IndicatorScrollListener(@NonNull PageIndicator pageIndicator, @NonNull LinearLayoutManager linearLayoutManager) {
            this.mIndicator = pageIndicator;
            this.mLinearLayoutManager = linearLayoutManager;
        }

        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            Log.d("PageIndicator", "onScrolled " + recyclerView.getScrollY());
            Log.d("PageIndicator", "onScrolled dy = " + dy);
            Log.d("PageIndicator", "linearLayout " + this.mLinearLayoutManager.getHeight());
            int firstVisibleItem = this.mLinearLayoutManager.findFirstVisibleItemPosition();
            ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(firstVisibleItem);
            float positionX = viewHolder.itemView.getY();
            float height = (float)viewHolder.itemView.getHeight();
            this.mIndicator.setScrollPosition(positionX / height, firstVisibleItem);
        }
    }
}
