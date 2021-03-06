package com.guuguo.android.pikacomic.app.fragment

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.guuguo.android.lib.app.LNBaseActivity
import com.guuguo.android.lib.extension.safe
import com.guuguo.android.pikacomic.R
import com.guuguo.android.pikacomic.app.activity.BaseTitleFragmentActivity
import com.guuguo.android.pikacomic.app.adapter.ComicsAdapter
import com.guuguo.android.pikacomic.app.viewModel.ComicsViewModel
import com.guuguo.android.pikacomic.base.BaseFragment
import com.guuguo.android.pikacomic.databinding.FragmentComicsBinding
import com.guuguo.android.pikacomic.db.UOrm
import com.guuguo.android.pikacomic.entity.CategoryEntity
import com.guuguo.android.pikacomic.entity.ComicsEntity
import com.guuguo.android.pikacomic.entity.ComicsResponse

/**
 * mimi 创造于 2017-05-22.
 * 项目 pika
 */
class ComicsFragment : BaseFragment() {
    lateinit var binding: FragmentComicsBinding
    val viewModel by lazy { ComicsViewModel(this) }
    val comicsAdapter = ComicsAdapter()
    override fun getLayoutResId() = R.layout.fragment_comics
    var getComicsType = 0
    var categoryEntity: CategoryEntity? = null

    override fun getHeaderTitle(): String {
        return when (getComicsType) {
            TYPE_COMICS_CATEGORY -> categoryEntity?.title.safe()
            TYPE_COMICS_RECENTLY -> "最近更新"
            TYPE_COMICS_RANK -> "排行榜"
            TYPE_COMICS_MY_FAVORITE -> "我的收藏"
            else -> ""
        }
    }

    override fun setLayoutResId(inflater: LayoutInflater?, resId: Int, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(inflater, resId, container, false)
        binding.viewModel = viewModel
        return binding.root
    }


    companion object {
        val ARG_GET_COMICS = "ARG_GET_COMICS"
        val ARG_CATEGORY = "ARG_CATEGORY"
        val TYPE_COMICS_RECENTLY = 0
        val TYPE_COMICS_CATEGORY = 1
        val TYPE_COMICS_RANK = 2
        val TYPE_COMICS_MY_FAVORITE = 3

        fun intentTo(activity: Activity, type: Int, category: CategoryEntity? = null) {
            val intent = Intent(activity, BaseTitleFragmentActivity::class.java)
            intent.putExtra(LNBaseActivity.SIMPLE_ACTIVITY_INFO, ComicsFragment::class.java)

            val bundle = Bundle()
            bundle.putInt(ARG_GET_COMICS, type)
            category?.let {
                bundle.putSerializable(ARG_CATEGORY, category)
            }
            intent.putExtras(bundle)
            activity.startActivity(intent)
        }
    }

    override fun initVariable(savedInstanceState: Bundle?) {
        super.initVariable(savedInstanceState)
        getComicsType = arguments.getInt(ARG_GET_COMICS)
        categoryEntity = arguments.getSerializable(ARG_CATEGORY) as CategoryEntity?

    }

    override fun initView() {
        super.initView()
        binding.recycler.layoutManager = GridLayoutManager(activity, 3)
        comicsAdapter.bindToRecyclerView(binding.recycler)
        comicsAdapter.setOnLoadMoreListener({
            page++
            loadData()
        }, binding.recycler)
        comicsAdapter.setOnItemClickListener { _, _, position ->
            ComicDetailFragment.intentTo(activity, comicsAdapter.getItem(position)!!)
        }
    }


    override fun loadData() {
        super.loadData()
        when (getComicsType) {
            TYPE_COMICS_CATEGORY -> viewModel.getComics(page, categoryEntity?.title)
            TYPE_COMICS_RECENTLY -> viewModel.getComics(page)
            TYPE_COMICS_MY_FAVORITE -> viewModel.getComics(page, null, viewModel.TYPE_MY_FAVORITE)
            TYPE_COMICS_RANK -> {
                comicsAdapter.setEnableLoadMore(false)
                viewModel.getComicsRank()
            }
        }
    }

    var page = 1;
    fun setUpComicsPage(comics: ComicsResponse.ComicsBean) {
        setUpComics(comics.docs)
        if (comics.pages <= comics.page)
            comicsAdapter.loadMoreEnd()
        else {
            comicsAdapter.loadMoreComplete()
        }
        comicsAdapter.disableLoadMoreIfNotFullPage()   }

    fun setUpComics(comics: List<ComicsEntity>) {
        if (comicsAdapter.emptyViewCount < 1)
            comicsAdapter.setEmptyView(R.layout.simple_empty_view)
        UOrm.db().insert(comics)
        comicsAdapter.addData(comics)
    }
}