package com.mediatek.beatbox;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mediatek.beatbox.databinding.FragmentBeatBoxBinding;
import com.mediatek.beatbox.databinding.ListItemSoundBinding;

import java.util.List;

public class BeatBoxFragment extends Fragment {
    private BeatBox mBeatBox;

    @NonNull
    public static BeatBoxFragment newInstance() {
        return new BeatBoxFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保留fragment，对于已保留的fragment在设备旋转时不会随着设备一起销毁重建，相反，它会一直保留，并在需要时，
        //原封不动的转给新的activity。对于已保留的fragment，其全部实例变量(如mBeatBox)的值也会保持不变。
        setRetainInstance(true);

        mBeatBox = new BeatBox(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FragmentBeatBoxBinding binding = DataBindingUtil.inflate(inflater, R.layout.fragment_beat_box,
                container, false);

        binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        binding.recyclerView.setAdapter(new SoundAdapter(mBeatBox.getSounds()));

        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mBeatBox.release();
    }

    private class SoundHolder extends RecyclerView.ViewHolder {
        private  ListItemSoundBinding mSoundBinding;

        private SoundHolder(ListItemSoundBinding binding) {
            super(binding.getRoot());
            mSoundBinding = binding;
            mSoundBinding.setViewModel(new SoundViewModel(mBeatBox));
        }

        public void bind(Sound sound) {
            mSoundBinding.getViewModel().setSound(sound);
            mSoundBinding.executePendingBindings();
        }
    }

    private class SoundAdapter extends RecyclerView.Adapter<SoundHolder> {
        private List<Sound> mSounds;

        public SoundAdapter(List<Sound> sounds) {
            mSounds = sounds;
        }

        @NonNull
        @Override
        public SoundHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            ListItemSoundBinding soundBinding = DataBindingUtil.inflate(inflater,
                    R.layout.list_item_sound, viewGroup, false);
            return new SoundHolder(soundBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull SoundHolder soundHolder, int position) {
            Sound sound = mSounds.get(position);
            soundHolder.bind(sound);
        }

        @Override
        public int getItemCount() {
            return mSounds.size();
        }
    }
}
