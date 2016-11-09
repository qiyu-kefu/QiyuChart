package com.qiyukf.desk.chart.animation;

import android.animation.TimeInterpolator;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.qiyukf.desk.chart.RenderHandler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhoujianghua on 2016/10/3.
 */

public class TimingAnimation {

    private static final long PERIOD = 16; // 每隔16ms刷新一次

    private static final int STATUS_INIT = 0;
    private static final int STATUS_RUNNING = 1;
    private static final int STATUS_PAUSE = 2;
    private static final int STATUS_CANCEL = 3;
    private static final int STATUS_COMPLETE = 4;

    private TimeInterpolator interpolator = Interpolators.ACC_DEC;

    private long startTime;
    private long duration;

    private AnimationListener listener;

    private static AnimationHandler animationHandler = new AnimationHandler();

    private float initial;
    private float target;

    private AtomicInteger status = new AtomicInteger(STATUS_INIT);

    public TimingAnimation() {
        this(0, 1);
    }

    public TimingAnimation(float initial, float target) {
        this.initial = initial;
        this.target = target;
    }

    public TimingAnimation setInterpolator(TimeInterpolator interpolator) {
        this.interpolator = interpolator;
        return this;
    }

    public TimingAnimation setDuration(long duration) {
        this.duration = duration;
        return this;
    }

    public TimingAnimation setListener(AnimationListener listener) {
        this.listener = listener;
        return this;
    }

    public void start() {
        startTime = SystemClock.elapsedRealtime();
        status.set(STATUS_RUNNING);
        animationHandler.start(this);
    }

    public void complete() {
        // wait for next animation tick to remove and notify
        status.set(STATUS_COMPLETE);
    }

    public void cancel() {
        // wait for next animation tick to remove
        status.set(STATUS_CANCEL);
    }

    public boolean isRunning() {
        return status.get() == STATUS_RUNNING;
    }

    public boolean isDone() {
        return status.get() == STATUS_CANCEL || status.get() == STATUS_COMPLETE;
    }

    private boolean onTick(long current) {
        if (status.get() == STATUS_COMPLETE) {
            listener.onValueUpdate(TimingAnimation.this, target);
        } else if (status.get() != STATUS_CANCEL) {
            if (current - startTime >= duration) {
                status.set(STATUS_COMPLETE);
                listener.onValueUpdate(TimingAnimation.this, target);
            } else {
                float elapsed = current - startTime;
                float output = interpolator.getInterpolation(elapsed / duration);
                float fraction = initial + (target - initial) * output;
                listener.onValueUpdate(TimingAnimation.this, fraction);
            }
        }
        return isDone();
    }

    public interface AnimationListener {
        void onValueUpdate(TimingAnimation animation, float fraction);
    }

    private static class AnimationHandler {
        private final List<TimingAnimation> animations = new ArrayList<>();

        private Handler handler = RenderHandler.get();

        public void start(TimingAnimation animation) {
            if (handler.getLooper() == Looper.myLooper()) {
                add(animation);
            } else {
                handler.post(new ARRunnable(animation, true));
            }
        }

        public void stop(TimingAnimation animation) {
            if (handler.getLooper() == Looper.myLooper()) {
               remove(animation);
            } else {
                handler.post(new ARRunnable(animation, false));
            }
        }

        private void add(TimingAnimation animation) {
            animations.add(animation);
            if (animations.size() == 1) {
                handler.post(updateRunnable);
            }
        }

        private void remove(TimingAnimation animation) {
            animations.remove(animation);
            if (animations.size() == 0) {
                handler.removeCallbacks(updateRunnable);
            }
        }

        private class ARRunnable implements Runnable {

            TimingAnimation animation;
            boolean add;

            public ARRunnable(TimingAnimation animation, boolean add) {
                this.animation = animation;
                this.add = add;
            }

            @Override
            public void run() {
                if (add) {
                    add(animation);
                } else {
                    remove(animation);
                }
            }
        }

        private Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                long current = SystemClock.elapsedRealtime();
                Iterator<TimingAnimation> iterator = animations.iterator();
                while (iterator.hasNext()) {
                    TimingAnimation animation = iterator.next();
                    if (animation.onTick(current)) {
                        iterator.remove();
                    }
                }
                if (animations.size() > 0) {
                    handler.postDelayed(this, PERIOD);
                }
            }
        };
    }
}
