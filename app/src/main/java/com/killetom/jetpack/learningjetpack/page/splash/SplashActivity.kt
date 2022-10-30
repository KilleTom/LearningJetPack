package com.killetom.jetpack.learningjetpack.page.splash

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.RotateAnimation
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.splashscreen.SplashScreenViewProvider
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.airbnb.lottie.RenderMode
import com.killetom.jetpack.learningjetpack.MainActivity

import com.killetom.jetpack.learningjetpack.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

class SplashActivity : AppCompatActivity() {

    private var tempSSVP: SplashScreenViewProvider? = null
    private var keyOnScreenStatus: Boolean = false
    private var iconViewAnimation :Animation?= null

    private val vm by lazy { ViewModelProvider(this)[SplashViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val splashScreen = installSplashScreen()
        setContentView(R.layout.activity_splash)

        vm.stateLiveData.observe(this) {
            when (it) {
                SplashViewModel.SplashState.SplashStart -> {
                    keyOnScreenStatus = true
                }
                SplashViewModel.SplashState.SplashDone -> {
                    Log.i("ypz","remove")
                    iconViewAnimation?.cancel()
                    tempSSVP?.remove()
                    finish()
                    startActivity(
                        Intent(this@SplashActivity,
                            MainActivity::class.java))
                }
            }
        }
        splashScreen.setKeepOnScreenCondition { !keyOnScreenStatus }

        splashScreen.setOnExitAnimationListener { splashScreenVP ->
            //这里做页面过渡过程中交互逻辑
            runScreenExitActionFinal(splashScreenVP)
        }

        vm.doSplashLogic()

    }

    private fun runScreenExitActionByDIY(splashScreenVP: SplashScreenViewProvider) {

        tempSSVP = splashScreenVP
        val contentView = splashScreenVP.view
        val iconView = splashScreenVP.iconView
        Log.i("ypz", iconView::class.java.name)

        if (contentView is ViewGroup) {

            contentView.addView(
                ImageView(contentView.context).apply {
                    this.setImageResource(R.drawable.bg_splash)
                },
                0,
                MarginLayoutParams.MATCH_PARENT
            )
        }
    }

    private fun runScreenExitActionByAndimator(splashScreenVP: SplashScreenViewProvider) {

        val contentView = splashScreenVP.view
        val iconView = splashScreenVP.iconView
        tempSSVP = splashScreenVP

        if (contentView is ViewGroup) {

            contentView.addView(
                ImageView(contentView.context).apply {
                    this.setImageResource(R.drawable.bg_splash)
                },
                0,
                MarginLayoutParams.MATCH_PARENT
            )
        }
        val scaleAnimation = ScaleAnimation(
            0.6f,
            1.5f,
            0.6f,
            1.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            this.duration = 1000L

            this.repeatMode = Animation.REVERSE
            this.repeatCount = Animation.INFINITE
        }

        val rotateAnimation = RotateAnimation(
            0f,
            360f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        ).apply {
            this.duration = 1000L

            this.repeatMode = Animation.REVERSE
            this.repeatCount = Animation.INFINITE
        }

        val animSet = AnimationSet(true)

        animSet.addAnimation(scaleAnimation)
        animSet.addAnimation(rotateAnimation)

        Log.i("ypz", "startAnimation")
        iconView.startAnimation(animSet)
        iconViewAnimation = iconView.animation
    }

    private fun runScreenExitActionFinal(splashScreenVP: SplashScreenViewProvider) {

        val contentView = splashScreenVP.view

        tempSSVP = splashScreenVP

        if (contentView is ViewGroup) {

            contentView.addView(
                ImageView(contentView.context).apply {
                    this.setImageResource(R.drawable.bg_splash)
                },
                0,
                MarginLayoutParams.MATCH_PARENT
            )

            val iconView = splashScreenVP.iconView
            iconView.visibility = View.GONE

            val animationView = LottieAnimationView(contentView.context).apply {
                this.setImageResource(R.drawable.bg_splash)
                this.minimumWidth = iconView.minimumWidth
                this.minimumHeight = iconView.minimumHeight
                this.maxWidth = iconView.measuredWidth
                this.maxHeight = iconView.measuredHeight
                top = iconView.top
                left = iconView.left
                right = iconView.right
                bottom = iconView.bottom
                setAnimation(R.raw.splash_lottie)
                setCacheComposition(true)
                setRenderMode(RenderMode.HARDWARE)
                repeatMode = LottieDrawable.INFINITE
                setApplyingOpacityToLayersEnabled(true)
            }
            contentView.addView(animationView)
            animationView.playAnimation()
            contentView.addView(
                TextView(contentView.context).apply {

                    this.text = "L e a r n i n g J e t P a c K"
                    this.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                    this.setTextColor(Color.WHITE)
                    this.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC)
                    setPadding(0,0,0, resources.getDimension(android.R.dimen.app_icon_size).toInt())
                }
            )
        }


    }
}

class SplashViewModel() : ViewModel() {

    private val _stateLiveData: MutableLiveData<SplashState> =
        MutableLiveData(SplashState.SplashStart)
    val stateLiveData: LiveData<SplashState> = _stateLiveData

    fun doSplashLogic() {
        flow {

            emit(true)
            //模拟加载需要这么多时间
            Thread.sleep(6000)

        }.flowOn(Dispatchers.IO)
            .onCompletion {
                _stateLiveData.value = SplashState.SplashDone
            }
            .launchIn(this.viewModelScope)
    }

    sealed class SplashState {
        object SplashStart : SplashState()
        object SplashDone : SplashState()
    }
}