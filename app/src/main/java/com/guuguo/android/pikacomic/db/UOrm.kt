package com.guuguo.android.pikacomic.db

import android.database.sqlite.SQLiteDatabase
import com.guuguo.android.pikacomic.app.MyApplication
import com.guuguo.android.pikacomic.entity.ComicsEntity
import com.guuguo.android.pikacomic.entity.EpEntity
import com.litesuits.orm.LiteOrm
import com.litesuits.orm.db.DataBaseConfig
import com.litesuits.orm.db.assit.SQLiteHelper

object UOrm : SQLiteHelper.OnUpdateListener {

    private val mLiteOrm: LiteOrm
    private var config = DataBaseConfig(MyApplication.Companion.instance)
    private var isDbUpdate = false

    init {
        config.dbVersion = 11//新增拼音字段,字母大写模式
        config.debugged = true
        config.onUpdateListener = this
        mLiteOrm = LiteOrm.newCascadeInstance(config)
        if (isDbUpdate)
            updateDb()
    }

    override fun onUpdate(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {
        isDbUpdate = true
    }

    private fun updateDb() {
     
    }

    fun db(): LiteOrm {
        return mLiteOrm.cascade()
    }


}
