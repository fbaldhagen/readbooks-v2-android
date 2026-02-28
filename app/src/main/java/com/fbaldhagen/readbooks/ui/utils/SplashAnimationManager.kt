package com.fbaldhagen.readbooks.ui.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.graphics.Rect
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.compose.runtime.mutableStateOf
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreenViewProvider

class SplashAnimationManager {

    var logoTargetBounds = mutableStateOf<Rect?>(null)
        private set

    fun onLogoPositioned(bounds: Rect) {
        logoTargetBounds.value = bounds
    }

    fun animateAway(splashScreenView: SplashScreenViewProvider) {
        val iconView = splashScreenView.iconView
        val targetBounds = logoTargetBounds.value

        if (targetBounds == null) {
            splashScreenView.remove()
            return
        }

        val iconLocation = IntArray(2)
        iconView.getLocationOnScreen(iconLocation)
        val startSize = iconView.width.toFloat()
        val targetSize = targetBounds.width().toFloat()

        // Calculate centers
        val startCenterX = iconLocation[0] + startSize / 2f
        val startCenterY = iconLocation[1] + startSize / 2f
        val targetCenterX = targetBounds.left + targetSize / 2f
        val targetCenterY = targetBounds.top + targetSize / 2f

        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(iconView, View.TRANSLATION_X, 0f, targetCenterX - startCenterX),
                ObjectAnimator.ofFloat(iconView, View.TRANSLATION_Y, 0f, targetCenterY - startCenterY),
                ObjectAnimator.ofFloat(iconView, View.SCALE_X, 1f, targetSize / startSize),
                ObjectAnimator.ofFloat(iconView, View.SCALE_Y, 1f, targetSize / startSize),
                ObjectAnimator.ofFloat(splashScreenView.view, View.ALPHA, 1f, 0f)
            )
            duration = 400
            interpolator = DecelerateInterpolator()
            doOnEnd { splashScreenView.remove() }
            start()
        }
    }
}