package com.mediatek.criminalintent;

import android.app.Activity;
import android.content.Intent;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import java.util.Date;
import java.util.UUID;

public class CrimeFragment extends Fragment {
    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private CheckBox mSolvedCheckBox;

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String EXTRA_CRIME_TITLE = "crime_index";
    private static final String DIALOG_DATE = "DialogDate";

    private static final int REQUESR_CODE_DATE = 0;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCrime = new Crime();
        //UUID crimeId = (UUID)getActivity().getIntent().getSerializableExtra(CrimePagerActivity.EXTRA_CRIME_ID);
        UUID crimeId = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);

        /*需要显示设置该方法，好让FragmentManager知道，当其托管的Activity收到操作系统的
        onCreateOptionsMenu()方法回调请求时，那么托管Activity的FragmentManager管理的Fragment也应
        接收onCreateOptionsMenu()方法的回调指令。当然，不显示调用setHasOptionsMenu()方法的话，
        FragmentManager就不会调用该Fragment的onCreateOptionsMenu()方法。 */
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
                //This space intentionally left blank
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                mCrime.setTitle(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //This one too
            }
        });

        mDateButton = (Button)v.findViewById(R.id.crime_date);
        updateDate();
        //mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                //DatePickerFragment dialogFragment = new DatePickerFragment();
                DatePickerFragment dateDialogFragment = DatePickerFragment.newInstance(mCrime.getDate());
                dateDialogFragment.setTargetFragment(CrimeFragment.this, REQUESR_CODE_DATE);
                dateDialogFragment.show(fragmentManager, DIALOG_DATE);
            }
        });

        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        setActivityResult();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK || data == null) {
            return;
        }
        if (requestCode == REQUESR_CODE_DATE) {
            Date date = DatePickerFragment.parseDateFromExtra(data);
            mCrime.setDate(date);
            updateDate();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) { //Activity或Fragment创建菜单
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.fragment_crime, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).delCrime(mCrime);
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void updateDate() {
        mDateButton.setText(mCrime.getDate().toString());
    }

    private void setActivityResult() {
        Intent data = new Intent();
        data.putExtra(EXTRA_CRIME_TITLE, CrimeLab.get(getActivity()).getIndex(mCrime.getId()));
        getActivity().setResult(Activity.RESULT_OK, data); //Fragment没有setResult方法，所以需要委托给其托管的activity的setResult方法
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static int parseResultIndex(Intent intent) {
        return intent.getIntExtra(EXTRA_CRIME_TITLE, 0);
    }
}
