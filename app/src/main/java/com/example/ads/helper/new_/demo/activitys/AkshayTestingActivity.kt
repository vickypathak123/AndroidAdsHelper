package com.example.ads.helper.new_.demo.activitys

import com.example.ads.helper.new_.demo.base.BaseActivity
import com.example.ads.helper.new_.demo.base.BaseBindingActivity
import com.example.ads.helper.new_.demo.databinding.ActivityAkshayTestingBinding

class AkshayTestingActivity : BaseBindingActivity<ActivityAkshayTestingBinding>() {

    override fun setBinding(): ActivityAkshayTestingBinding {
        return ActivityAkshayTestingBinding.inflate(layoutInflater)
    }

    override fun getActivityContext(): BaseActivity {
        return this@AkshayTestingActivity
    }
}