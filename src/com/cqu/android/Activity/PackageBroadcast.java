
package com.cqu.android.Activity;

import com.cqu.android.bean.Api;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Broadcast receiver responsible for removing rules that affect uninstalled apps.
 * 对卸载的软件移除规则     忽略软件更新
 */
public class PackageBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Intent.ACTION_PACKAGE_REMOVED.equals(intent.getAction())) {
			// Ignore application updates
			/**---EXTRA_REPLACING
		     * Used as a boolean extra field in {@link android.content.Intent#ACTION_PACKAGE_REMOVED}
		     * intents to indicate that this is a replacement of the package, so this
		     * broadcast will immediately be followed by an add broadcast for a
		     * different version of the same package.-------default==false
		     */
			final boolean replacing = intent.getBooleanExtra(Intent.EXTRA_REPLACING, false);
			if (!replacing) {
				 /**---getIntExtra
			     * Retrieve extended data from the intent.
			     *
			     * @param name The name of the desired item.
			     * @param defaultValue the value to be returned if no value of the desired
			     * type is stored with the given name.
			     *
			     * @return the value of an item that previously added with putExtra()
			     * or the default value if none was found.
			     *
			     * @see #putExtra(String, int)
			     */
				/**Intent.EXTRA_UID   
			     * Used as an int extra field in {@link android.content.Intent#ACTION_UID_REMOVED}
			     * intents to supply the uid the package had been assigned.  Also an optional
			     * extra in {@link android.content.Intent#ACTION_PACKAGE_REMOVED} or
			     * {@link android.content.Intent#ACTION_PACKAGE_CHANGED} for the same
			     * purpose.
			     */
				final int uid = intent.getIntExtra(Intent.EXTRA_UID, -123);
				//调用对卸载应用程序的规则
				Api.applicationRemoved(context, uid);
			}
		}
	}

}
