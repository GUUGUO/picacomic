package com.guuguo.android.pikacomic.app.activity

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.graphics.Color
import android.os.Bundle
import android.support.v4.view.GestureDetectorCompat
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import com.bm.library.Info
import com.flyco.systembar.SystemBarHelper
import com.github.florent37.viewanimator.ViewAnimator
import com.guuguo.android.pikacomic.R
import com.guuguo.android.pikacomic.app.adapter.ComicContentAdapter
import com.guuguo.android.pikacomic.app.viewModel.ComicContentViewModel
import com.guuguo.android.pikacomic.base.BaseActivity
import com.guuguo.android.pikacomic.databinding.ActivityComicContentBinding
import com.guuguo.android.pikacomic.entity.ComicsContentResponse
import com.guuguo.android.pikacomic.entity.ComicsEntity
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration


class ComicContentActivity : BaseActivity() {
    lateinit var binding: ActivityComicContentBinding
    val viewModel = ComicContentViewModel(this)
    val comicsContentAdapter = ComicContentAdapter()
    //argument
    var ep = 1
    lateinit var comic: ComicsEntity
    override fun getLayoutResId() = R.layout.activity_comic_content

    override val isFullScreen: Boolean
        get() = true

    override fun setLayoutResId(layoutResId: Int) {
        binding = DataBindingUtil.setContentView(activity, layoutResId)
        binding.viewModel = viewModel
    }

    override fun initStatusBar() {
        SystemBarHelper.setHeightAndPadding(activity, binding.llTopBar)
    }

    companion object {
        val ACTIVITY_COMIC_CONTENT = 0x12
        val ARG_COMIC = "ARG_COMIC"
        val ARG_EP = "ARG_EP"
        fun intentTo(context: Activity, comic: ComicsEntity, ep: Int) {
            val intent = Intent(context, ComicContentActivity::class.java)
            intent.putExtra(ARG_COMIC, comic)
            intent.putExtra(ARG_EP, ep)
            context.startActivityForResult(intent, ACTIVITY_COMIC_CONTENT)
        }
    }

    override fun initVariable(savedInstanceState: Bundle?) {
        super.initVariable(savedInstanceState)
        comic = intent.getSerializableExtra(ARG_COMIC) as ComicsEntity
        ep = intent.getIntExtra(ARG_EP, 1)
    }

    internal lateinit var mInfo: Info
    internal var animationIn = AlphaAnimation(0f, 1f)
    internal var animationOut = AlphaAnimation(1f, 0f)
    lateinit var mDetector: GestureDetectorCompat
    override fun initView() {
        super.initView()
        mDetector = GestureDetectorCompat(this, MyGestureListener());
        binding.recycler.layoutManager = LinearLayoutManager(activity)
        binding.recycler.addItemDecoration(HorizontalDividerItemDecoration.Builder(activity).color(Color.BLACK).build())
        comicsContentAdapter.bindToRecyclerView(binding.recycler)
//        comicsContentAdapter.setOnItemLongClickListener { adapter, view, position ->
//            val p = view as PhotoView
//            mInfo = p.info
//
//            binding.photoView.setImageDrawable(p.drawable)
//            binding.bg.startAnimation(animationIn)
//            binding.bg.visibility = View.VISIBLE
//            binding.photoView.animaFrom(mInfo)
//            true
//        }
        comicsContentAdapter.setOnLoadMoreListener({
            page++
            loadData()
        }, binding.recycler)
        binding.llTopBar.post {
            ViewCompat.setTranslationY(binding.llTopBar, -binding.llTopBar.height.toFloat())
            binding.llTopBar.visibility = View.VISIBLE
        }
        binding.llTopBar.visibility = View.INVISIBLE
        binding.recycler.addOnItemTouchListener(viewModel.scrollShowBarListener)

        binding.recycler.addOnScrollListener(viewModel.scrollReadInfoChangeListener)
    }

    inner class MyGestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDoubleTap(e: MotionEvent): Boolean {
            scaleRecycler(e.rawX, e.rawY)
            return super.onDoubleTap(e)
        }

        override fun onScroll(e1: MotionEvent?, e2: MotionEvent?, distanceX: Float, distanceY: Float): Boolean {
            scrollRecycler(distanceX, distanceY)
            return true
        }

        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            if (!showBar())
                hideBar()
            return super.onSingleTapConfirmed(e)
        }
    }

    fun setUpReadInfo(ep: Int, position: Int, total: Int) {
        binding.tvEp.text = "第${ep}/${comic.epsCount}话 $position/$total"
    }

    fun showBar(): Boolean {
        if (binding.llTopBar.translationY != 0f) {
            SystemBarHelper.immersiveStatusBar(activity, 0f)
            setFullScreen(false)
            ViewAnimator.animate(binding.llTopBar).translationY(-1 * binding.llTopBar.height.toFloat(), 0f).decelerate().duration(200).startDelay(200).start()
            return true
        }
        return false
    }

    fun hideBar(): Boolean {
        if (binding.llTopBar.translationY != -binding.llTopBar.height.toFloat()) {
            viewModel.unImmersiveStatusBar(activity.window)
            setFullScreen(true)
            ViewAnimator.animate(binding.llTopBar).translationY(0f, -1 * binding.llTopBar.height.toFloat()).accelerate().duration(200).start()
            return true
        }
        return false
    }


    override fun loadData() {
        super.loadData()
        if (firstLoad) {
            activity.dialogLoadingShow("正在加载中")
        }
        viewModel.getContent(comic._id, ep, page)
    }

    var firstLoad = true
    var page = 1
    //漫画列表保存三个page数量的漫画内容，再有新的列表加载好会移除原先的第一个列表
    fun setContent(data: ComicsContentResponse.PagesEntity) {
        firstLoad = false
        (0..data.docs.size - 1).map {
            data.docs[it].ep = ep
            data.docs[it].position = ((data.page - 1) * data.limit) + it + 1
            data.docs[it].total = data.total
        }
        if (page == 1) {
            setUpReadInfo(ep, 0, data.total)
            viewModel.setReadStatus(ep)
        }

        if (data.pages <= data.page) {
            if (comic.epsCount > ep) {
                ep++
                page = 0
                comicsContentAdapter.loadMoreComplete()
            } else {
                comicsContentAdapter.loadMoreEnd()
            }
        } else {
            comicsContentAdapter.loadMoreComplete()
        }

        comicsContentAdapter.data.removeAll(comicsContentAdapter.firstEpList)
        comicsContentAdapter.notifyItemRangeRemoved(0, comicsContentAdapter.firstEpList.size)

        comicsContentAdapter.firstEpList.clear()
        comicsContentAdapter.firstEpList.addAll(comicsContentAdapter.secondEpList)

        comicsContentAdapter.secondEpList.clear()
        comicsContentAdapter.secondEpList.addAll(comicsContentAdapter.data.takeLast(comicsContentAdapter.itemCount - comicsContentAdapter.firstEpList.size))

        comicsContentAdapter.addData(data.docs)
    }

    fun scaleRecycler(rawX: Float, rawY: Float) {

        if (binding.recycler.scaleX > 1) {
            ViewAnimator.animate(binding.recycler).scale(1f).translationX(0f).accelerate().duration(200).start()
       } else
            ViewAnimator.animate(binding.recycler).scale(1.5f).accelerate().duration(200).start()

    }

    fun scrollRecycler(distanceX: Float, distanceY: Float) {
        with(binding.recycler) {
            if (scaleX > 1) {
                Log.i("scroll", ViewCompat.getTranslationX(this).toString())
                ViewCompat.setTranslationX(this, ViewCompat.getTranslationX(this) - distanceX)
            }
        }
    }
}
