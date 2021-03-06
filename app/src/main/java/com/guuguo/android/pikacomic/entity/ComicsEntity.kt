package com.guuguo.android.pikacomic.entity

import com.guuguo.android.pikacomic.db.UOrm
import com.litesuits.orm.db.annotation.Ignore
import com.litesuits.orm.db.annotation.Mapping
import com.litesuits.orm.db.annotation.PrimaryKey
import com.litesuits.orm.db.annotation.Table
import com.litesuits.orm.db.assit.QueryBuilder
import com.litesuits.orm.db.enums.AssignType
import com.litesuits.orm.db.enums.Relation
import java.io.Serializable
import java.util.*
import kotlin.collections.ArrayList

/**
 * mimi 创造于 2017-05-24.
 * 项目 pika
 */
@Table("comics")
class ComicsEntity : Serializable {
    @PrimaryKey(AssignType.BY_MYSELF)
    var _id = ""

    @Mapping(Relation.OneToOne)
    var _creator: UserEntity? = null
    var title = ""
    var description = ""

    @Mapping(Relation.OneToOne)
    var thumb: ThumbEntity? = null
    var author = ""
    var chineseTeam = ""
    var pagesCount: Int = 0
    var epsCount: Int = 0
    var isFinished: Boolean = false
    var updated_at: Date? = null
    var created_at: Date? = null
    var viewsCount: Int = 0
    var likesCount: Int = 0
    var isFavourite: Boolean = false
    var isLiked: Boolean = false
    var commentsCount: Int = 0
    var readEp: Int=1
    var readPosition: Int = 1
    var lastReadTime: Long = 0L
    var addDownloadTime: Long = 0L

    @Ignore
    var categories: ArrayList<String> = ArrayList<String>()
    @Ignore
    var tags: ArrayList<String> = ArrayList<String>()


    fun save() {
        val entity = UOrm.db().queryById(_id, ComicsEntity::class.java)
        if (entity == null)
            UOrm.db().insert(this)
        else {
            readEp = entity.readEp
            lastReadTime = entity.lastReadTime
            addDownloadTime = entity.addDownloadTime
            readPosition=entity.readPosition
            UOrm.db().update(this)
        }
    }

    companion object {
        fun queryDownloadComics(): ArrayList<ComicsEntity>? {
            return UOrm.db().query(QueryBuilder(ComicsEntity::class.java).whereNoEquals("addDownloadTime", 0).appendOrderDescBy("addDownloadTime"))
        }

    }
}