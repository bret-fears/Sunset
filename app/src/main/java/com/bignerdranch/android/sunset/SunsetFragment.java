package com.bignerdranch.android.sunset;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by bretfears on 12/15/16.
 */

public class SunsetFragment extends Fragment {

    private static long DURATION = 3000L;

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;
    private SkyType mCurrentSkyType;

    private AnimatorSet mCurrentAnimation;

    private enum SkyType {
        DAY, NIGHT
    }

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        mCurrentAnimation = new AnimatorSet();

        Resources resources = getResources();
        mBlueSkyColor = resources.getColor(R.color.blue_sky);
        mSunsetSkyColor = resources.getColor(R.color.sunset_sky);
        mNightSkyColor = resources.getColor(R.color.night_sky);

        Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
        mSunView.startAnimation(pulse);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAnimation();
            }
        });

        return view;
    }

    private void runAnimation() {
        float sunsetYstart = mSunView.getTop();
        float sunriseYstart = mSkyView.getHeight();
        float sunYend = mSkyView.getHeight() + 60; // pulsating offset
        ObjectAnimator sunHeightAnimator;
        ObjectAnimator sunSkyAnimator;
        ObjectAnimator nightSkyAnimator;

        if (mCurrentAnimation.isRunning()) {
            return;
        }

        mCurrentAnimation.end();

        if (mCurrentSkyType == null || mCurrentSkyType == SkyType.DAY) {
            sunHeightAnimator = ObjectAnimator
                    .ofFloat(mSunView, "y", sunsetYstart, sunYend)
                    .setDuration(3000);
            sunHeightAnimator.setInterpolator(new AccelerateInterpolator());

            sunSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor)
                    .setDuration(3000);
            sunSkyAnimator.setEvaluator(new ArgbEvaluator());

            nightSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor)
                    .setDuration(1500);
            nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        } else {
            sunHeightAnimator = ObjectAnimator
                    .ofFloat(mSunView, "y", sunriseYstart + 60, mSunView.getTop())
                    .setDuration(3000);
            sunHeightAnimator.setInterpolator(new AccelerateInterpolator());

            sunSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", mNightSkyColor, mSunsetSkyColor)
                    .setDuration(1000);
            sunSkyAnimator.setEvaluator(new ArgbEvaluator());

            nightSkyAnimator = ObjectAnimator
                    .ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mBlueSkyColor)
                    .setDuration(500);
            nightSkyAnimator.setEvaluator(new ArgbEvaluator());
        }

        mCurrentAnimation = new AnimatorSet();
        mCurrentAnimation
                .play(sunHeightAnimator)
                .with(sunSkyAnimator)
                .before(nightSkyAnimator);
        mCurrentAnimation.start();

        mCurrentAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (mCurrentSkyType == null || mCurrentSkyType == SkyType.DAY) {
                    mCurrentSkyType = SkyType.NIGHT;
                } else {
                    mCurrentSkyType = SkyType.DAY;
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }
}
