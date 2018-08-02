package com.mediatek.photogallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

public class PhotoGalleryFragment extends VisibleFragment {
    private static final String TAG ="PhotoGalleryFragment";

    private RecyclerView mPhotoRecyclerView;
    private List<GalleryItem> mGalleryItems = new ArrayList<>();
    private ThumbnailDownloader<PhotoHolder> mThumbnailDownloader; //PhotoHolder是最终放置并显示下载图片的地方

    public static PhotoGalleryFragment newInstance() {
        return new PhotoGalleryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); //设置为保留fragment
        setHasOptionsMenu(true);

        updateItems();

        /*添加服务启动代码方式一*/
        //Intent intent = PollService.newIntent(getActivity());
        //getActivity().startService(intent);
        /*添加服务启动代码方式二*/
        //PollService.setServiceAlarm(getActivity(), true);

        /* 主线程是一个拥有Handler和Looper的消息循环。主线程上创建的Handler会自动与主线程的Looper相关联。
        * 主线程上创建的Handler也可以传递给另外一个线程，传递出现的Handler与创建它的线程Looper始终保持着联系。
        * 因此，已传递出去的Handler负责处理的所有消息都将在主线程的消息队列中处理。*/
        Handler responseHandler = new Handler();
        mThumbnailDownloader = new ThumbnailDownloader<>(responseHandler);
        mThumbnailDownloader.setThumbnailDownloadListener(
                //处理已下载的图片，执行更新UI的任务
                new ThumbnailDownloader.ThumbnailDownloadListener<PhotoHolder>() {
                    @Override
                    public void onThumbnailDownloaded(PhotoHolder photoHolder, Bitmap bitmap) {
                        Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                        photoHolder.bindDrawable(drawable);
                    }
                }
        );
        mThumbnailDownloader.start();
        mThumbnailDownloader.getLooper();
        Log.i(TAG, "Background thread started");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_gallery, container, false);
        mPhotoRecyclerView = (RecyclerView)view.findViewById(R.id.photo_recycler_view);
        mPhotoRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        setupAdapter();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownloader.cleanQueue();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mThumbnailDownloader.quit();
        Log.i(TAG, "Background thread destroyed");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_photo_gallery, menu);

        MenuItem searchItem = menu.findItem(R.id.menu_item_search);
        final SearchView searchView = (SearchView)searchItem.getActionView();

        //SearchView.OnQueryTextListener接口已提供了接收回调的方式，可以响应查询指令.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //当用户提交搜索查询请求时，onQueryTextSubmit(String)回调方法就会执行。用户提交的搜索
            //字符串会传给它。搜索请求受理后，该方法会返回true。
            @Override
            public boolean onQueryTextSubmit(String s) {
                Log.d(TAG, "QueryTextSubmit: " + s);
                QueryPreferences.setStoredQuery(getActivity(), s);
                updateItems();
                return true;
            }

            //只要SearchView文本框中的文字有变化，onQueryTextChange(String)回调方法就会被执行。
            @Override
            public boolean onQueryTextChange(String s) {
                Log.d(TAG, "QueryTextChange: " + s);
                return false;
            }
        });

        //用户点击搜索按钮时，SearchView的View.OnClickListener.onClick()方法会被调用
        //优化点：可以让用户点击搜索按钮展开SearchView时，搜索文本框中就能默认显示已保存的查询字符串。
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = QueryPreferences.getStoredQuery(getActivity());
                searchView.setQuery(query, false);
            }
        });

        //依据定时器的开关状态，正确显示menu_item_toggle_polling菜单项的标题文字
        MenuItem toggleItem = menu.findItem(R.id.menu_item_toggle_polling);
        if (PollService.isServiceAlarmOn(getActivity())) {
            toggleItem.setTitle(R.string.stop_polling);
        }
        else {
            toggleItem.setTitle(R.string.start_polling);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_clear:
                QueryPreferences.setStoredQuery(getActivity(), null);
                updateItems();
                return true;
            case R.id.menu_item_toggle_polling:
                boolean shouldStartAlarm = !PollService.isServiceAlarmOn(getActivity());
                PollService.setServiceAlarm(getActivity(), shouldStartAlarm);
                //菜单项标题等属性发生变更时，需要调用getActivity().invalidateOptionsMenu()方法刷新菜单，
                //这样，才能正确显示menu_item_toggle_polling菜单项的标题文字
                getActivity().invalidateOptionsMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateItems() {
        String query = QueryPreferences.getStoredQuery(getActivity());
        new FetchItemsTask(query).execute();
    }

    private void setupAdapter() {
        if (isAdded()) { //检查确认该fragment已与目标activity相关联，从而保证getActivity() 返回结果非空
            mPhotoRecyclerView.setAdapter(new PhotoAdapter(mGalleryItems));
        }
    }

    private class PhotoHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView mItemImageView;
        private GalleryItem mGalleryItem;

        public PhotoHolder(View view) {
            super(view);
            mItemImageView = (ImageView) view.findViewById(R.id.item_image_view);
            itemView.setOnClickListener(this);
        }

        public void bindDrawable(Drawable drawable) {
            mItemImageView.setImageDrawable(drawable);
        }

        public void bindGalleryItem(GalleryItem galleryItem) {
            mGalleryItem = galleryItem;
        }

        @Override
        public void onClick(View view) {
            //方式一：监听RecyclerView显示项的点击事件，通过隐式Intent启动一个浏览器，实现指定URL的网页浏览
            //Intent intent = new Intent(Intent.ACTION_VIEW, mGalleryItem.getPhotoPageUri());

            //方式二：弃用隐式Intent，启动PhotoPageActivity，使用WebView在应用内浏览指定网页
            Intent intent = PhotoPageActivity.newIntent(getActivity(), mGalleryItem.getPhotoPageUri());

            startActivity(intent);
        }
    }

    private class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {
        private List<GalleryItem> mGalleryItems;

        public PhotoAdapter(List<GalleryItem> galleryItems) {
            mGalleryItems = galleryItems;
        }

        @Override
        public PhotoHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
            return new PhotoHolder(view);
        }

        @Override
        public void onBindViewHolder(PhotoHolder photoHolder, int position) {
            GalleryItem galleryItem = mGalleryItems.get(position);
            photoHolder.bindGalleryItem(galleryItem);
            //获取res/Drawable目录下的Drawable资源
            Drawable placeholder = getResources().getDrawable(R.drawable.bill_up_close);
            photoHolder.bindDrawable(placeholder);
            mThumbnailDownloader.queueThumbnail(photoHolder, galleryItem.getUrl());
        }

        @Override
        public int getItemCount() {
            return mGalleryItems.size();
        }
    }

    /* 获取网络数据的代码应该在后台线程中运行，而使用后台线程最简单的方式便是使用AsyncTask工具类。
    * AsyncTask创建后台线程后，便可在该线程上调用 doInBackground(...) 方式运行线程代码。
    * AsyncTask第一个泛型参数指定传递给execute(...)方法的输入参数的类型，
    * 进而确定传递给doInBackground(...)方法的输入参数的类型；
    * 第二个泛型参数可指定发送进度更新所需要的类型。
    * AsyncTask第三个泛型参数是AsyncTask返回结果的数据类型，也就是DoInBackground(...)方法的返回结果的数据类型，
    * 同时也是onPostExecute(...)方法输入参数的数据类型。*/
    private class FetchItemsTask extends AsyncTask<Void, Void, List<GalleryItem>> {
        private String mQuery;

        public FetchItemsTask(String query) {
            mQuery = query;
        }

        @Override
        protected List<GalleryItem> doInBackground(Void... params) {
            if (mQuery == null) {
                return new FlickrFetcher().fetchRecentPhotos();
            }
            else {
                return new FlickrFetcher().searchPhotos(mQuery);
            }
        }

        /*为安全起见，只有主线程才能更新UI，不推荐也不允许从后台线程更新UI。
        * onPostExecute(...)方法在doInBackground(...)方法执行完毕后才会运行。
        * 更为重要的是，它是在主线程而非后台线程上运行的。因此，在该方法中更新UI比较安全。*/
        @Override
        protected void onPostExecute(List<GalleryItem> items) {
            mGalleryItems = items;
            setupAdapter();
        }
    }
}
