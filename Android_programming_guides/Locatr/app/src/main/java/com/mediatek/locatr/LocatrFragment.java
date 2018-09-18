package com.mediatek.locatr;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;

public class LocatrFragment extends Fragment {
    private static final String TAG = "LocatrFragment";

    //权限常量数组，列出应用需要的全部权限
    //在代码中使用的全部Android标准权限都声明在Manifest.permission类里
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private static final int REQUEST_LOCATION_PERMISSIONS = 0; //权限请求代码

    private ImageView mImageView;
    private GoogleApiClient mGoogleApiClient;

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); //允许创建菜单项

        /** 要使用Play服务，需要创建一个客户端类，这个客户端是个GoogleApiClient类的实例。
         * 为创建客户端，首先创建一个GoogleApiClient.Builder，然后，使用我们要使用的API配置它，
         * 最后，调用build()方法创建实例。
         * 连接状态信息要通过两个回调接口传递：
         * ConnectionCallbacks和OnConnectionFailedListener
         * */
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API) //将定位服务的API传递给它
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu(); //连上客户端后，刷新菜单项
                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        Log.i(TAG, "GoogleApiClient::ConnectionCallbacks::onConnectionSuspended called...");
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.i(TAG, "GoogleApiClient::OnConnectionFailedListener::onConnectionFailed called...");
                    }
                })
                .build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locatr, container, false);

        mImageView = (ImageView) view.findViewById(R.id.image);

        return view;
    }

    /** 创建了mGoogleApiClient客户端之后，需要连接它。
     * Google推荐在onStart()方法里连接客户端，在onStop()方法里断开连接。*/
    @Override
    public void onStart() {
        super.onStart();

        /** 调用客户端的connect()方法会改变菜单按钮的行为，所以要调用invalidateOptionsMenu()方法刷新它的状态。*/
        getActivity().invalidateOptionsMenu();
        mGoogleApiClient.connect();
    }

    /** 创建了mGoogleApiClient客户端之后，需要连接它。
     * Google推荐在onStart()方法里连接客户端，在onStop()方法里断开连接。*/
    @Override
    public void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        //如果连不上客户端，应用就什么也做不了。所以，为反映客户端连接状况，按钮的启用和禁用状态应作相应切换
        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mGoogleApiClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                if (hasLocationPermission()) {
                    findImage();
                } else {
                    /** 如果权限检查不过，就调用requestPermissions()方法请求权限.
                     * requestPermissions(...)是个异步请求方法。调用它之后，
                     * Android会弹出系统权限授权对话框要求用户反馈。 */
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /** Android弹出系统权限授权对话框要求用户反馈时，为响应用户操作，我们还要写个
     * onRequestPermissionsResult(...)响应方法。
     * 用户点按ALLOW或DENY按钮后，Android就会调用这个回调方法。
     * 再次检查授权结果，如果用户给予授权，* 就调用findImage(...)方法。
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            /** onRequestPermissionsResult(int, String[ ], int[ ])方法有个参数叫grantResults。
             * 如果愿意，也可以查看这个参数值确认授权结果。此处我们采用了更方便方式：
             * 调用hasLocationPermission()方法时，它里面的checkSelfPermission(...)方法会给出授权结果。
             * 所以，只要再调一次hasLocationPermission()方法就可以了。
             * */
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    findImage();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /** */
    private void findImage() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setNumUpdates(1);
        locationRequest.setInterval(0);
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(TAG, "Got a fix: " + location);
                    }
                });
    }


    /** 检查使用权限。
     * 权限安全类型，如dangerous，是赋予权限组而非单个权限的。权限组包含各类具体的使用权限。
     * 例如，ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION都属于LOCATION权限组。
     * 授予权限给权限组内任何单一权限时，同组内的其他所有权限也会获得授权。
     * 既然应用需要检查的ACCESS_FINE_LOCATION和ACCESS_COARSE_LOCATION都属于LOCATION权限组，
     * 那只要对其中一种权限进行检查就可以了。*/
    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private class SearchTask extends AsyncTask<Location, Void, Void> {
        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;

        @Override
        protected Void doInBackground(Location... locations) {
            FlickrFetcher flickrFetcher = new FlickrFetcher();
            List<GalleryItem> items = flickrFetcher.searchPhotos(locations[0]);
            if (items.size() == 0) {
                return null;
            }
            mGalleryItem = items.get(0);
            try {
                byte[] bytes = flickrFetcher.getUrlBytes(mGalleryItem.getUrl());
                mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
            catch (IOException ioe) {
                Log.i(TAG, "Unable to download bitmap", ioe);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            mImageView.setImageBitmap(mBitmap);
        }
    }
}
