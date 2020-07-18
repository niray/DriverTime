package org.niray.drivertime.main;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class TimeActivity extends Activity {

    private final int OVERLAY_PERMISSION_REQ_CODE = 0x01;

    private final Activity mAct = TimeActivity.this;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(mAct)) {
                //有悬浮窗权限开启服务绑定 绑定权限
                PermissionFinish();

            } else {
                //没有悬浮窗权限m,去开启悬浮窗权限
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + mAct.getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            //默认有悬浮窗权限  但是 华为, 小米,oppo等手机会有自己的一套Android6.0以下  会有自己的一套悬浮窗权限管理 也需要做适配
            PermissionFinish();
        }

//        List<PermissionItem> permissionItems = new ArrayList<PermissionItem>();
//        permissionItems.add(new PermissionItem(Manifest.permission.SYSTEM_ALERT_WINDOW, "悬浮权限", R.drawable.permission_ic_phone));
//        HiPermission.create(mAct)
//                .permissions(permissionItems)
//                .animStyle(R.style.PermissionAnimModal)
//                .checkMutiPermission(new PermissionCallback() {
//                    @Override
//                    public void onClose() {
//
//                    }
//
//                    @Override
//                    public void onFinish() {
//
//                    }
//
//                    @Override
//                    public void onDeny(String permission, int position) {
//
//                    }
//
//                    @Override
//                    public void onGuarantee(String permission, int position) {
//
//                    }
//                });


        //结束
//        finish();

//      stopService(serviceStop);

    }

    //启动服务
    private void PermissionFinish() {
        Intent service = new Intent(mAct, FloatService.class);
        startService(service);

        //打开行者App
        startXingZhe();
        finish();
    }

    /**
     * 打开行者App
     */
    private void startXingZhe() {
        if (getPackageManager().getLaunchIntentForPackage("im.xingzhe") != null) {
            startActivity(getPackageManager().getLaunchIntentForPackage("im.xingzhe"));
        } else {
            Toast.makeText(this, "行者未安装", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "权限授予失败，无法开启悬浮窗", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "权限授予成功！", Toast.LENGTH_SHORT).show();
                    //有悬浮窗权限开启服务绑定 绑定权限
                    PermissionFinish();
                }
            }
        }
    }


}
