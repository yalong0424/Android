package com.mediatek.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class CrimeListFragment extends Fragment {
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;

    private static final int REQUEST_CRIME = 1;
    private static final String TAG_CRIME_LIST_FRAGMENT = "CrimeListFragment";
    private static final String SAVE_SUBTITLE_VISIBLE = "subtitle_visible";

    private int mListRefreshIndex = -1;
    private boolean mSubtitleVisible;

    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTitleView;
        private TextView mDateView;
        private ImageView mSolvedImageView;
        private Crime mCrime;

        public CrimeHolder(LayoutInflater inflater, ViewGroup parent){
            super(inflater.inflate(R.layout.list_item_crime, parent, false));
            itemView.setOnClickListener(this);

            mTitleView = (TextView)itemView.findViewById(R.id.crime_title);
            mDateView = (TextView)itemView.findViewById(R.id.crime_date);
            mSolvedImageView = (ImageView)itemView.findViewById(R.id.crime_solved);
        }

        public void bind(Crime crime) {
            mCrime = crime;
            mTitleView.setText(mCrime.getTitle());
            //mDateView.setText(mCrime.getDate().toString());
            String sFormatedDate = DateFormat.format("EEE MMM dd yyyy HH:mm:ss aaa", mCrime.getDate()).toString();
            mDateView.setText(sFormatedDate);
            mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);
        }

        @Override
        public void onClick(View view) {
            //Toast.makeText(getActivity(), mCrime.getTitle() + "clicked!", Toast.LENGTH_SHORT).show();
            //Intent intent = new Intent(getActivity(), CrimePagerActivity.class);
            Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            //startActivity(intent);
            startActivityForResult(intent, REQUEST_CRIME);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new CrimeHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder crimeHolder, int position) {
            Crime crime = mCrimes.get(position);
            crimeHolder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*需要显示设置该方法，好让FragmentManager知道，当其托管的Activity收到操作系统的
        onCreateOptionsMenu()方法回调请求时，那么托管Activity的FragmentManager管理的Fragment也应
        接收onCreateOptionsMenu()方法的回调指令。当然，不显示调用setHasOptionsMenu()方法的话，
        FragmentManager就不会调用该Fragment的onCreateOptionsMenu()方法。 */
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);

        mCrimeRecyclerView = (RecyclerView)view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVE_SUBTITLE_VISIBLE);
        }

        updateUI();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG_CRIME_LIST_FRAGMENT, "CrimeListFragment::onResume() called");
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVE_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG_CRIME_LIST_FRAGMENT, "CrimeListFragment::onActivityResult() called");
        if (resultCode != Activity.RESULT_OK){ //验证子activity返回的结果代码是否符合预期
            return;
        }
        if (data == null){ //验证子activity是否返回有效的数据
            return;
        }
        if (requestCode == REQUEST_CRIME) //对CrimePagerActivity返回值进行处理,在CrimePagerActivity中修改Title时才会触发Activity的返回值
        {
            //String sResultTitle = "CrimePagerActivity's result: " + CrimeFragment.parseResultTitle(data);
            //Toast.makeText(getActivity(), sResultTitle, Toast.LENGTH_SHORT).show();
            mListRefreshIndex = CrimeFragment.parseResultIndex(data);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) { //Activity或Fragment创建菜单
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_crime_list, menu); //传入菜单定义文件的资源ID，将菜单布局文件中定义的菜单项目填充到Menu实例中

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subtitleItem.setTitle(R.string.hide_subtitle);
        }
        else {
            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { //item id就是item在菜单布局文件中定义的资源ID
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime); //将新建的Crime实例添加到底层模型层数据源中

                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent); //启动CrimePagerActivity实例，让用户编辑新创建的Crime记录
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu(); //刷新重建Menus
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        String subtitle = null;
        if (mSubtitleVisible) {
            int crimeCount = CrimeLab.get(getActivity()).getCrimes().size();
            //subtitle = getString(R.string.subtitle_format, crimeCount);
            subtitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        }

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        }
        else {
            mAdapter.notifyDataSetChanged(); //通知RecyclerView刷新所有的可见列表项，但是目前只有一个Crime实例会变更，所以不够高效
            //mAdapter.notifyItemChanged();
        }
        updateSubtitle();
    }
}
