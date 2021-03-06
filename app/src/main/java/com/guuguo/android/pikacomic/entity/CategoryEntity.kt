package com.guuguo.android.pikacomic.entity

import com.litesuits.orm.db.annotation.Mapping
import com.litesuits.orm.db.annotation.PrimaryKey
import com.litesuits.orm.db.annotation.Table
import com.litesuits.orm.db.enums.AssignType
import com.litesuits.orm.db.enums.Relation
import java.io.Serializable

/**
 * mimi 创造于 2017-05-23.
 * 项目 pika
 */
@Table("category")
class CategoryEntity : Serializable {
    /**
     * _id : 5821859b5f6b9a4f93dbf6e9
     * title : 嗶咔漢化
     * description : 未知
     * thumb : {"originalName":"translate.png","path":"f541d9aa-e4fd-411d-9e76-c912ffc514d1.png","fileServer":"https://storage1.picacomic.com"}
     */
    @PrimaryKey(AssignType.BY_MYSELF)
    var _id: String? = null
    var title: String? = null
    var description: String? = null
    @Mapping(Relation.OneToOne)
    var thumb: ThumbEntity? = null
}
