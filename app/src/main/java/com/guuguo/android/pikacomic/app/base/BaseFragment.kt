package com.guuguo.android.pikacomic.app.base

import android.view.View
import com.guuguo.android.lib.app.LBaseFragment
import com.guuguo.android.lib.app.LNBaseFragment
import com.guuguo.android.pikacomic.app.MyApplication

/**
 * Created by guodeqing on 7/23/16.
 */

abstract class BaseFragment : LNBaseFragment() {

    protected var myApplication = MyApplication.instance

}
