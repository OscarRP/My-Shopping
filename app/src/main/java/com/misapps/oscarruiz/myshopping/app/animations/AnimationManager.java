package com.misapps.oscarruiz.myshopping.app.animations;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.misapps.oscarruiz.myshopping.R;

/**
 * Created by Oscar Ruiz on 22/08/2017.
 */

public class AnimationManager {

    /**
     * Animation
     */
    private Animation animation;

    /**
     * Starts left to right animnation
     */
    public void leftToRightAnimation (View view, Context context, int duration) {
        animation = AnimationUtils.loadAnimation(context, R.anim.left_to_right);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    /**
     * Starts right to left animnation
     */
    public void rightToLeftAnim (View view, Context context, int duration) {
        animation = AnimationUtils.loadAnimation(context, R.anim.right_to_left);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }
}
