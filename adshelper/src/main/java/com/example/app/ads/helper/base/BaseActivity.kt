@file:Suppress("unused")

package com.example.app.ads.helper.base

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.AnimRes
import androidx.annotation.AnimatorRes
import androidx.annotation.LayoutRes
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.app.ads.helper.base.utils.isTiramisuPlus
import com.example.app.ads.helper.interstitialad.InterstitialAdHelper.showInterstitialAd
import com.example.app.ads.helper.utils.exitTheApp
import com.example.app.ads.helper.utils.isAppForeground
import com.example.app.ads.helper.utils.isInternetAvailable
import com.example.app.ads.helper.utils.isOnline
import com.example.app.ads.helper.widget.ReviewDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

/**
 * @author Akshay Harsoda
 * @since 18 Mar 2021
 * @updated 03 Jul 2024
 *
 * BaseActivity.kt - A simple class contains some modifications to the native Activity.
 * This Class use with or without ViewBinding property.
 * also you have use this class in JAVA or KOTLIN both language.
 *
 * use of this class
 * you have to extend your Activity using this class like.
 * in Java :- {class MainActivity extends BaseActivity}
 * in Kotlin :- {class MainActivity : BaseActivity()}
 *
 * NOTE :- if you use this class with ViewBinding then you must override onCreate() method of Activity
 */
@Suppress("MemberVisibilityCanBePrivate")
internal abstract class BaseActivity : AppCompatActivity(), CoroutineScope, View.OnClickListener {

    /**
     * your log tag name
     */
    @Suppress("PropertyName")
    val TAG: String = javaClass.simpleName

    var mJob: Job = Job()

    var isOnPause: Boolean = false

    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main

    var isSystemBackButtonPressed: Boolean = false

    val mReviewDialog: ReviewDialog by lazy {
        ReviewDialog(fActivity = mActivity)
    }

    private var mOnBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(
        true // default to enabled
    ) {
        override fun handleOnBackPressed() {
            isSystemBackButtonPressed = true
            customOnBackPressed()
        }
    }

    /**
     * your activity context object
     */
    val mActivity: BaseActivity
        get() {
            return getActivityContext()
        }

    //<editor-fold desc="For Start Activity Result">
    private var mOnActivityResult: (resultCode: Int, data: Intent?) -> Unit = { _, _ -> }

    private val intentLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            mOnActivityResult.invoke(resultCode, data)
        }

    private val intentSenderLauncher: ActivityResultLauncher<IntentSenderRequest> =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()
        ) { result: ActivityResult ->
            val resultCode = result.resultCode
            val data = result.data
            mOnActivityResult.invoke(resultCode, data)
        }
    //</editor-fold>

    override fun onCreate(savedInstanceState: Bundle?) {
        setParamBeforeLayoutInit()
        super.onCreate(null)

        getLayoutRes()?.let {
            setContentView(it)
        }
    }

    /**
     * If You Not Using viewBinding
     * @param layoutResID Pass Your Layout File Resource Id Like This 'R.layout.activity_main'
     */
    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        setContentView()
    }

    /**
     * If You Using viewBinding
     * @param view pass Your Layout File Lick 'ActivityMainBinding.inflate(getLayoutInflater()).getRoot()'
     */
    override fun setContentView(view: View?) {
        super.setContentView(view)
        setContentView()
    }

    private fun setContentView() {
        isInternetAvailable.observeForever {
//            if (it) {
//                if (mNoInternetDialog.isShowing) {
//                    mNoInternetDialog.dismiss()
//                }
//            } else {
//                mNoInternetDialog.show()
//            }
        }
        initView()
        loadAds()
        initViewAction()
        initViewListener()

        mActivity.onBackPressedDispatcher.addCallback(mActivity, mOnBackPressedCallback)
    }

    //<editor-fold desc="Default Function">
    /**
     * This Method for set you Activity Context
     *
     * @return
     * in Java :- {return MainActivity.this;}
     * in Kotlin :- {return this@MainActivity}
     */
    @UiThread
    abstract fun getActivityContext(): BaseActivity

    /**
     * This method for set your layout
     *
     * If you use this class without using ViewBinding,
     * then you don't need to override onCreate() method
     *
     * without using viewBinding @return Your Layout File Resource Id Lick This 'return R.layout.activity_main'
     *
     * or if you use viewBinding then @return 'return null' for base reference
     */
    @UiThread
    @LayoutRes
    abstract fun getLayoutRes(): Int?

    abstract fun getBindingView(): View?
    //</editor-fold>

    //<editor-fold desc="For Get & Set Edge-To-Edge Margin & Padding">
    open fun setEdgeToEdgeLayout() {
        enableEdgeToEdge()

        window?.let { lWindow ->
            lWindow.decorView.let { lDecorView ->
                WindowInsetsControllerCompat(lWindow, lDecorView).apply {
                    this.isAppearanceLightStatusBars = false // true or false as desired.
                    this.isAppearanceLightNavigationBars = false

                    lWindow.statusBarColor = Color.TRANSPARENT
                    lWindow.navigationBarColor = Color.TRANSPARENT

                    this.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    this.hide(WindowInsetsCompat.Type.systemBars())
                }
            }
        }
    }

    internal var isGetEdgeToEdgeLeftMargin: Boolean = false
    internal var isGetEdgeToEdgeTopMargin: Boolean = false
    internal var isGetEdgeToEdgeRightMargin: Boolean = false
    internal var isGetEdgeToEdgeBottomMargin: Boolean = false

    //    open fun View.getEdgeToEdgeMargin(
    open fun getEdgeToEdgeMargin(
        leftAction: (leftMargin: Int) -> Unit = {},
        topAction: (topMargin: Int) -> Unit = {},
        rightAction: (rightMargin: Int) -> Unit = {},
        bottomAction: (bottomMargin: Int) -> Unit = {},
    ) {
        getBindingView()?.let { lDecorView ->
            ViewCompat.setOnApplyWindowInsetsListener(lDecorView) { v: View, windowInsets: WindowInsetsCompat ->
                val systemBars = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                if (systemBars.left != 0) {
                    if (!isGetEdgeToEdgeLeftMargin) {
                        isGetEdgeToEdgeLeftMargin = true
                        leftAction.invoke(systemBars.left)
                    }
                }
                if (systemBars.top != 0) {
                    if (!isGetEdgeToEdgeTopMargin) {
                        isGetEdgeToEdgeTopMargin = true
                        topAction.invoke(systemBars.top)
                    }
                }
                if (systemBars.right != 0) {
                    if (!isGetEdgeToEdgeRightMargin) {
                        isGetEdgeToEdgeRightMargin = true
                        rightAction.invoke(systemBars.right)
                    }
                }
                if (systemBars.bottom != 0) {
                    if (!isGetEdgeToEdgeBottomMargin) {
                        isGetEdgeToEdgeBottomMargin = true
                        bottomAction.invoke(systemBars.bottom)
                    }
                }
                windowInsets
            }
        }
    }

    open fun View.setEdgeToEdgeLeftMargin(fLeftMargin: Int): View {
        val v: View = this@setEdgeToEdgeLeftMargin
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (fLeftMargin != 0) {
                this.leftMargin = (fLeftMargin + v.marginLeft)
            }
        }
        return v
    }

    open fun View.setEdgeToEdgeTopMargin(fTopMargin: Int): View {
        val v: View = this@setEdgeToEdgeTopMargin
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (fTopMargin != 0) {
                this.topMargin = (fTopMargin + v.marginTop)
            }
        }
        return v
    }

    open fun View.setEdgeToEdgeRightMargin(fRightMargin: Int): View {
        val v: View = this@setEdgeToEdgeRightMargin
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (fRightMargin != 0) {
                this.rightMargin = (fRightMargin + v.marginRight)
            }
        }
        return v
    }

    open fun View.setEdgeToEdgeBottomMargin(fBottomMargin: Int): View {
        val v: View = this@setEdgeToEdgeBottomMargin
        v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            if (fBottomMargin != 0) {
                this.bottomMargin = (fBottomMargin + v.marginBottom)
            }
        }
        return v
    }

    open fun View.setEdgeToEdgeMargin(fMargin: Int): View {
        this.setEdgeToEdgeLeftMargin(fLeftMargin = fMargin)
        this.setEdgeToEdgeTopMargin(fTopMargin = fMargin)
        this.setEdgeToEdgeRightMargin(fRightMargin = fMargin)
        this.setEdgeToEdgeBottomMargin(fBottomMargin = fMargin)
        return this
    }

    open fun View.setEdgeToEdgeMargin(fLeftMargin: Int, fTopMargin: Int, fRightMargin: Int, fBottomMargin: Int): View {
        this.setEdgeToEdgeLeftMargin(fLeftMargin = fLeftMargin)
        this.setEdgeToEdgeTopMargin(fTopMargin = fTopMargin)
        this.setEdgeToEdgeRightMargin(fRightMargin = fRightMargin)
        this.setEdgeToEdgeBottomMargin(fBottomMargin = fBottomMargin)
        return this
    }

    open fun View.setEdgeToEdgeLeftPadding(fLeftPadding: Int, isAddDefaultPadding: Boolean = true): View {
        this.setPadding(if (isAddDefaultPadding) (this.paddingLeft + fLeftPadding) else fLeftPadding, this.paddingTop, this.paddingRight, this.paddingBottom)
        return this
    }

    open fun View.setEdgeToEdgeTopPadding(fTopPadding: Int, isAddDefaultPadding: Boolean = true): View {
        this.setPadding(this.paddingLeft, if (isAddDefaultPadding) (this.paddingTop + fTopPadding) else fTopPadding, this.paddingRight, this.paddingBottom)
        return this
    }

    open fun View.setEdgeToEdgeRightPadding(fRightPadding: Int, isAddDefaultPadding: Boolean = true): View {
        this.setPadding(this.paddingLeft, this.paddingTop, if (isAddDefaultPadding) (this.paddingRight + fRightPadding) else fRightPadding, this.paddingBottom)
        return this
    }

    open fun View.setEdgeToEdgeBottomPadding(fBottomPadding: Int, isAddDefaultPadding: Boolean = true): View {
        this.setPadding(this.paddingLeft, this.paddingTop, this.paddingRight, if (isAddDefaultPadding) (this.paddingBottom + fBottomPadding) else fBottomPadding)
        return this
    }

    open fun View.setEdgeToEdgePadding(fLeftPadding: Int, fTopPadding: Int, fRightPadding: Int, fBottomPadding: Int, isAddDefaultPadding: Boolean = true): View {
        if (isAddDefaultPadding) {
            this.setPadding((this.paddingLeft + fLeftPadding), (this.paddingTop + fTopPadding), (this.paddingRight + fRightPadding), (this.paddingBottom + fBottomPadding))
        } else {
            this.setPadding(fLeftPadding, fTopPadding, fRightPadding, fBottomPadding)
        }
        return this
    }
    //</editor-fold>

    //<editor-fold desc="Open Function">
    /**
     * This method for set-up your data before call setContentView()
     */
    open fun setParamBeforeLayoutInit() {
//        setEdgeToEdgeLayout()
    }

    /**
     * This method For Init All Ads.
     */
    @UiThread
    open fun setDefaultAdUI() {
    }

    @UiThread
    open fun initAds() {
    }

    /**
     * This method For Init Your Layout File's All View
     */
    @UiThread
    open fun initView() {
    }

    /**
     * This method For Init Your Default Action Performance On View
     */
    @UiThread
    open fun initViewAction() {
    }

    /**
     * This method For Set Your All Type Of Listeners
     */
    @UiThread
    open fun initViewListener() {
    }

    /**
     * This method For Set Your All View Click Listener,
     * now you no need to write multiple line code for 'View.setOnClickListener(this)'
     * just call this method and pass your all view like
     *
     * setClickListener(view1, view2)
     *
     * @param fViews list of your all passed view's.
     */
    @UiThread
    open fun setClickListener(vararg fViews: View) {
        for (lView in fViews) {
            lView.setOnClickListener(this)
        }
    }

    @UiThread
    open fun onNoInternetDialogShow() {
    }

    @UiThread
    open fun onNoInternetDialogDismiss() {
    }
    //</editor-fold>

    //<editor-fold desc="Call On Start Screen">
    /**
     * This method for initialized your Coroutine job
     */
    private fun initJob() {
        mJob = Job()
    }

    /**
     * This method for load your all type of ads
     */
    private fun loadAds() {
        setDefaultAdUI()
        if (isOnline) {
            initAds()
        }
    }
    //</editor-fold>

    //<editor-fold desc="New Activity Intent">
    /**
     * This Method for get your next activity intent
     *
     * @param isAddFlag [Default value:- true] for set up your activity flag in your intent
     * @param fBundle lambda fun for pass data throw intent
     */
    inline fun <reified T : Activity> getActivityIntent(
        isAddFlag: Boolean = true,
        fBundle: Bundle.() -> Unit = {},
    ): Intent {
        val lIntent = Intent(mActivity, T::class.java)

        if (isAddFlag) {
            lIntent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            lIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        lIntent.putExtras(Bundle().apply(fBundle))

        return lIntent
    }

    /**
     * This Method will replace your default method of [startActivityForResult]
     *
     * @param fIntent Your Launcher Screen Intent
     * @param isCheckResolveActivity pass false if you don't check resolveActivity of this Intent
     * @param fEnterAnimId your activity Enter animation
     * @param fExitAnimId your activity Exit animation
     * @param onActivityResult in this method you get your Result
     */
    @SuppressLint("QueryPermissionsNeeded")
    private fun launchActivityForResult(
        fIntent: Intent,
        isCheckResolveActivity: Boolean,
        @AnimatorRes @AnimRes fEnterAnimId: Int,
        @AnimatorRes @AnimRes fExitAnimId: Int,
        onActivityResult: (resultCode: Int, data: Intent?) -> Unit
    ) {
        mOnActivityResult = onActivityResult

        if (isCheckResolveActivity) {
            if (fIntent.resolveActivity(packageManager) != null) {
                intentLauncher.launch(
                    fIntent,
                    ActivityOptionsCompat.makeCustomAnimation(mActivity, fEnterAnimId, fExitAnimId)
                )
            }
        } else {
            intentLauncher.launch(
                fIntent,
                ActivityOptionsCompat.makeCustomAnimation(mActivity, fEnterAnimId, fExitAnimId)
            )
        }
    }

    open fun launchActivityForResult(
        fIntent: Intent,
        isAdsShowing: Boolean = false,
        isCheckResolveActivity: Boolean = true,
        @AnimatorRes @AnimRes fEnterAnimId: Int = android.R.anim.fade_in,
        @AnimatorRes @AnimRes fExitAnimId: Int = android.R.anim.fade_out,
        onActivityResult: (resultCode: Int, data: Intent?) -> Unit
    ) {
        if (isAdsShowing) {
            mActivity.showInterstitialAd { _, _ ->
                launchActivityForResult(
                    fIntent = fIntent,
                    isCheckResolveActivity = isCheckResolveActivity,
                    fEnterAnimId = fEnterAnimId,
                    fExitAnimId = fExitAnimId,
                    onActivityResult = onActivityResult
                )
            }
        } else {
            launchActivityForResult(
                fIntent = fIntent,
                isCheckResolveActivity = isCheckResolveActivity,
                fEnterAnimId = fEnterAnimId,
                fExitAnimId = fExitAnimId,
                onActivityResult = onActivityResult
            )
        }
    }

    /**
     * This Method will replace your default method of [startIntentSenderForResult]
     *
     * @param fIntentSender Your Launcher Screen IntentSender
     * @param fEnterAnimId your activity Enter animation
     * @param fExitAnimId your activity Exit animation
     * @param onActivityResult in this method you get your Result
     */
    open fun launchActivityForResult(
        fIntentSender: IntentSender,
        @AnimatorRes @AnimRes fEnterAnimId: Int = android.R.anim.fade_in,
        @AnimatorRes @AnimRes fExitAnimId: Int = android.R.anim.fade_out,
        onActivityResult: (resultCode: Int, data: Intent?) -> Unit
    ) {
        mOnActivityResult = onActivityResult

        val intentSenderRequest: IntentSenderRequest = IntentSenderRequest.Builder(fIntentSender).build()

        intentSenderLauncher.launch(
            intentSenderRequest,
            ActivityOptionsCompat.makeCustomAnimation(mActivity, fEnterAnimId, fExitAnimId)
        )
    }

    /**
     * This Method will replace your default method of [startActivity]
     *
     * @param fIntent Your Launcher Screen Intent
     * @param isNeedToFinish [Default value:- false] pass [isNeedToFinish = true] for finish your caller screen after call next screen
     * @param fEnterAnimId your activity Enter animation
     * @param fExitAnimId your activity Exit animation
     */
    private fun launchActivity(
        fIntent: Intent,
        isNeedToFinish: Boolean,
        @AnimatorRes @AnimRes fEnterAnimId: Int,
        @AnimatorRes @AnimRes fExitAnimId: Int
    ) {
        mActivity.runOnUiThread {
            if (isTiramisuPlus) {
                val options = ActivityOptions.makeCustomAnimation(mActivity, fEnterAnimId, fExitAnimId)
                mActivity.startActivity(fIntent, options.toBundle())
            } else {
                mActivity.startActivity(fIntent)
                @Suppress("DEPRECATION")
                mActivity.overridePendingTransition(fEnterAnimId, fExitAnimId)
            }

            if (isNeedToFinish) {
                mActivity.finish()
            }
        }
    }

    open fun launchActivity(
        fIntent: Intent,
        isAdsShowing: Boolean = false,
        isNeedToFinish: Boolean = false,
        @AnimatorRes @AnimRes fEnterAnimId: Int = android.R.anim.fade_in,
        @AnimatorRes @AnimRes fExitAnimId: Int = android.R.anim.fade_out
    ) {
        if (isAdsShowing) {
            mActivity.showInterstitialAd { _, _ ->
                launchActivity(
                    fIntent = fIntent,
                    isNeedToFinish = isNeedToFinish,
                    fEnterAnimId = fEnterAnimId,
                    fExitAnimId = fExitAnimId
                )
            }
        } else {
            launchActivity(
                fIntent = fIntent,
                isNeedToFinish = isNeedToFinish,
                fEnterAnimId = fEnterAnimId,
                fExitAnimId = fExitAnimId,
            )
        }
    }
    //</editor-fold>

    //<editor-fold desc="Update Fragment">
    /**
     * This method For Update Fragment & Attach In FrameLayout
     *
     * you have call this method using FrameLayout.
     * @param fFragment your fragment
     * @param enterAnim your fragment enter animation file
     * @param exitAnim your fragment exit animation file
     */
    @UiThread
    open fun FrameLayout.updateFragment(
        fFragment: Fragment,
        @AnimatorRes @AnimRes enterAnim: Int = android.R.anim.fade_in,
        @AnimatorRes @AnimRes exitAnim: Int = android.R.anim.fade_out,
    ) {

        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(enterAnim, exitAnim)
            .replace(this.id, fFragment)
            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            .commit()
    }
    //</editor-fold>

    override fun onClick(v: View) {

    }

    @UiThread
    open fun needToShowReviewDialog(): Boolean {
        return false
    }
    @UiThread
    open fun showReviewDialog(onNextAction: () -> Unit) {

    }

    open fun customOnBackPressed() {
        if (isAppForeground && needToShowReviewDialog()) {
            showReviewDialog {
                backFromCurrentScreen()
            }
        } else {
            backFromCurrentScreen()
        }
    }

    @UiThread
    open fun needToShowBackAd(): Boolean {
        return false
    }

    fun backFromCurrentScreen() {
        if (isAppForeground && needToShowBackAd()) {
            mActivity.showInterstitialAd { _, _ ->
                directBack()
            }
        } else {
            directBack()
        }
    }



    @UiThread
    open fun onScreenFinishing() {
    }

    fun directBack() {
        mActivity.runOnUiThread {
            mActivity.finishAfterTransition()
            onScreenFinishing()
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    open fun exitApplication() {
        mActivity.exitTheApp()
    }

    override fun onResume() {
        super.onResume()
        initJob()
    }

    override fun onPause() {
        super.onPause()
        isOnPause = true
    }

    override fun onDestroy() {
        super.onDestroy()
        mReviewDialog.dismiss()
        mJob.cancel()
    }
}