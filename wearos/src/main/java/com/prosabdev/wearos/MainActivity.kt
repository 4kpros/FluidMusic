package com.prosabdev.wearos

import android.app.Activity
import android.os.Bundle
import com.prosabdev.wearos.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }
}