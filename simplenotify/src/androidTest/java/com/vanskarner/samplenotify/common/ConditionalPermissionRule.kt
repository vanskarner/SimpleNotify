package com.vanskarner.samplenotify.common

import android.os.Build
import androidx.test.rule.GrantPermissionRule
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class ConditionalPermissionRule (
    permission: String,
    minSdk: Int
) : TestRule {
    private var grantPermissionRule: GrantPermissionRule? = null

    init {
        if (Build.VERSION.SDK_INT >= minSdk) {
            grantPermissionRule = GrantPermissionRule.grant(permission)
        }
    }

    override fun apply(base: Statement, description: Description): Statement {
        return grantPermissionRule?.apply(base, description) ?: base
    }
}