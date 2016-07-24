

package com.cqu.android.Activity;



import com.cqu.android.bean.Api;
import com.cqu.android.bean.Api.DroidApp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.provider.Settings;
import android.widget.Toast;

public class MiscUtil {

    public static final int MENU_CANCEL = 0;
    public static final int MENU_SWITCH = 1;
    public static final int MENU_KILL = 2;
    public static final int MENU_DETAIL = 3;
    public static final int MENU_UNINSTALL = 4;

    public static PackageInfo getPackageInfo(PackageManager pm, String name) {
        PackageInfo ret = null;
        try {
            ret = pm.getPackageInfo(name, PackageManager.GET_ACTIVITIES);
        } catch (NameNotFoundException e) {
        	//TODO: 异常处理
        }
        return ret;
    }

    public static Dialog getTaskMenuDialog(final MainActivity ma , final DroidApp dd) {

        return new AlertDialog.Builder(ma).setTitle(R.string.operation).setItems(
                R.array.menu_task_operation, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case MENU_KILL: {
                            	//2.2以上版本
                               // ctx.am.restartPackage(dp.getPackageName());
                            	ma.am.killBackgroundProcesses(dd.packageName); 
                                if (dd.packageName.equals(ma.getPackageName())) return;
                                Api.applications = null;
                                ma.showOrLoadApplications();
                                //return;
                            }break;
                            case MENU_SWITCH: {
                                if (dd.packageName.equals(ma.getPackageName())) return;
                                Intent i = dd.getIntent();
                                if (i == null) {
                                    Toast.makeText(ma, R.string.message_switch_fail, Toast.LENGTH_SHORT)
                                            .show();
                                    return;
                                }
                                try {
                                    ma.startActivity(i);
                                } catch (Exception ee) {
                                    Toast.makeText(ma, ee.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                return;
                            }
                            case MENU_UNINSTALL: {
                                Uri uri = Uri.fromParts("package",dd.packageName, null);
                                Intent it = new Intent(Intent.ACTION_DELETE, uri);
                                try {
                                    ma.startActivity(it);
                                } catch (Exception e) {
                                    Toast.makeText(ma, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                          
                                return;
                            }
                            case MENU_DETAIL: {
                            	Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            	Uri uri = Uri.fromParts("package",dd.packageName, null);
                            	intent.setData(uri);
                            	ma.startActivity(intent);
//                                Intent detailsIntent = new Intent();
//                                detailsIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
//                                detailsIntent.putExtra("com.android.settings.ApplicationPkgName", dp.getPackageName());
//                                ctx.startActivity(detailsIntent);
                                return;
                            }
                        }
                    }
                }).create();
    }
}
