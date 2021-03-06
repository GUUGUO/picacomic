package com.guuguo.android.pikacomic.entity

import com.guuguo.android.pikacomic.db.UOrm
import com.litesuits.orm.db.annotation.Column
import com.litesuits.orm.db.annotation.PrimaryKey
import com.litesuits.orm.db.annotation.Table
import com.litesuits.orm.db.assit.QueryBuilder
import com.litesuits.orm.db.enums.AssignType
import java.io.Serializable

/**
 * guode 创造于 2017-05-23.
 * 项目 pika
 */
@Table("ep")
class EpEntity(var comicId: String = "", @Column("epOrder") var order: Int = 0) : Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    var _id = ""
    var title = ""
    var updated_at = ""
    var downloadCount = -1

    fun save(order: Int, comicId: String) {
        this.order = order
        this.comicId = comicId
        val entity = UOrm.db().queryById(_id, EpEntity::class.java)
        if (entity == null) {
            UOrm.db().insert(this)
        } else {
            this.downloadCount = entity.downloadCount
            UOrm.db().update(this)
        }
    }

    companion object {
        fun get(comicId: String, order: Int): EpEntity? {
            val entities = UOrm.db().query(QueryBuilder(EpEntity::class.java).whereEquals("comicId", comicId).whereAppendAnd().whereEquals("epOrder", order))
            return if (entities.isNotEmpty())
                entities.first()
            else null
        }

        fun queryByComicId(comicId:String): ArrayList<EpEntity>? {
            return UOrm.db().query(QueryBuilder(EpEntity::class.java).whereEquals("comicId", comicId).appendOrderAscBy("epOrder"))
        }
    }
}