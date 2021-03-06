package com.guuguo.gank.net

import com.google.gson.GsonBuilder
import com.guuguo.android.lib.net.LBaseCallback
import com.guuguo.android.pikacomic.constant.jsonDataFormatStr
import com.guuguo.android.pikacomic.entity.*
import com.guuguo.android.pikacomic.net.http.ResponseModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MediaType
import okhttp3.RequestBody
import java.util.*

/**
 * Created by guodeqing on 7/14/16.
 */
object MyApiServer {
    val gson = GsonBuilder().setDateFormat(jsonDataFormatStr).create()
    val service by lazy { MyRetrofit.myRetrofit.create(Service::class.java) }
    fun signIn(email: String, password: String): Single<ResponseModel<TokenResponse>> {
        val map = HashMap<String, String>()
        map.put("email", email)
        map.put("password", password)
        return service.signIn(getRequestJsonBody(map))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun register(email: String, password: String, birthday: String, gender: String, name: String): Single<ResponseModel<String>> {
        val map = HashMap<String, String>()
        map.put("email", email)
        map.put("password", password)
        map.put("birthday", birthday)
        map.put("gender", gender)
        map.put("name", name)
        return service.register(getRequestJsonBody(map))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun resend_activation(email: String): Single<ResponseModel<String>> {
        val map = HashMap<String, String>()
        map.put("email", email)
        return service.resend_activation(getRequestJsonBody(map))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun forgot_password(email: String): Single<ResponseModel<String>> {
        val map = HashMap<String, String>()
        map.put("email", email)
        return service.forgot_password(getRequestJsonBody(map))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun userProfile(): Single<ResponseModel<UserResponse>> {
        return service.userProfile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
    fun keywords(): Single<ResponseModel<KeyWordResponse>> {
        return service.keywords()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getAnnouncements(): Single<ResponseModel<AnnouncementsResponse>> {
        return service.getAnnouncements(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getCategory(): Single<ResponseModel<CategoryResponse>> {
        return service.getCategory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getComics(page: Int, category: String?): Single<ResponseModel<ComicsResponse>> {
        return service.getComics(page, category, "ua")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
    fun searchComics(page: Int, query: String): Single<ResponseModel<ComicsResponse>> {
        return service.searchComics(page, query)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }
    fun getComicsContent(id: String, ep: Int, page: Int): Single<ResponseModel<ComicsContentResponse>> {
        return service.getComicContent(id, ep, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getComicDetail(id: String): Single<ResponseModel<ComicDetailResponse>> {
        return service.getComicDetail(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun favoriteComic(id: String): Single<ResponseModel<ActionResponse>> {
        return service.favoriteComic(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun likeComic(id: String): Single<ResponseModel<ActionResponse>> {
        return service.likeComic(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getComicsRandom(page: Int): Single<ResponseModel<ComicsRandomResponse>> {
        return service.getComicsRandom(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getComicsRank(): Single<ResponseModel<ComicsRandomResponse>> {
        return service.getComicsRank()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun getMyFavorite(page: Int): Single<ResponseModel<ComicsResponse>> {
        return service.myFavorites(page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun punchIn(): Single<ResponseModel<PunchInResponse>> {
        return service.punch_in()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    private fun getRequestJsonBody(map: HashMap<String, String>): RequestBody = RequestBody.create(
            MediaType.parse("application/json; charset=UTF-8"),
            LBaseCallback.gson.toJson(map))
}
