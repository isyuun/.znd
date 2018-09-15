/*
 * Copyright 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * 2016 All rights (c)KYGroup Co.,Ltd. reserved.
 * <p/>
 * This software is the confidential and proprietary information
 * of (c)KYGroup Co.,Ltd. ("Confidential Information").
 * <p/>
 * project	:	.prj
 * filename	:	PermissionUtil.java
 * author	:	isyoon
 * <p/>
 * <pre>
 * kr.kymedia.karaoke
 *    |_ PermissionUtil.java
 * </pre>
 */
package kr.kymedia.karaoke;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <pre>
 *
 * </pre>
 *
 * @author isyoon
 * @version 1.0
 * @since 2016-05-17
 */
public class PermissionUtil {
	///**
	// * Uses PackageManager getAllPermissionGroups() and queryPermissionsByGroup()
	// * to enumerate the Android permission hierarchy.
	// */
	//private void showPermissionTree(Context context) {
	//	final PackageManager pm = context.getPackageManager();
	//	if (pm == null) return;
	//
   // /*
   //  * Get a list of all permission groups and sort them alphabetically.
   //  * Then add to the end of the list the special case of a null group name. There can be
   //  * numerous permissions that are not listed under a group name.
   //  */
	//	List<PermissionGroupInfo> groupInfoList = pm.getAllPermissionGroups(0);
	//	if (groupInfoList == null) return;
	//
	//	ArrayList<String> groupNameList = new ArrayList<>();
	//	for (PermissionGroupInfo groupInfo : groupInfoList) {
	//		String groupName = groupInfo.name;
	//		if (groupName != null) {
	//			if (Build.VERSION.SDK_INT >= 17) {
	//	            /*
   //              * SDK 17 added the flags field. If non-zero, the permission group contains
   //              * permissions that control access to user personal data.
   //              * N.B. These are the permissions groups that are called "dangerous" in
   //              * Marshmallow.
   //              */
	//				if (groupInfo.flags != 0) {
	//					groupName += " (personal)";
	//				}
	//			}
	//			groupNameList.add(groupName);
	//		}
	//	}
	//
	//	Collections.sort(groupNameList);
	//	groupNameList.add(null);
	//
   // /*
   //  * Loop though each permission group, adding to the StringBuilder the group name and
   //  * the list of all permissions under that group.
   //  */
	//	StringBuilder sb = new StringBuilder(10000);
	//	final String INDENT = "   ";
	//
	//	for (String groupName : groupNameList) {
	//		if (groupName == null) groupName = "null";
	//
	//		sb.append("* ").append(groupName).append("\n");
	//
	//		ArrayList<String> permissionNameList = getPermissionsForGroup(groupName);
	//		if (permissionNameList.size() > 0) {
	//			for (String permission : permissionNameList) {
	//				sb.append(INDENT).append(permission).append("\n");
	//			}
	//		} else {
	//			sb.append(INDENT).append("no permissions under group\n");
	//		}
	//
	//		sb.append("\n");
	//	}
	//
	//	//m_textView.setText(sb.toString());
	//}


	static public ArrayList<String> getPermissionGroupNames(Context context) {
		final PackageManager pm = context.getPackageManager();
		if (pm == null) return null;

    /*
     * Get a list of all permission groups and sort them alphabetically.
     * Then add to the end of the list the special case of a null group name. There can be
     * numerous permissions that are not listed under a group name.
     */
		List<PermissionGroupInfo> groupInfoList = pm.getAllPermissionGroups(0);
		if (groupInfoList == null) return null;

		ArrayList<String> groupNameList = new ArrayList<>();
		for (PermissionGroupInfo groupInfo : groupInfoList) {
			String groupName = groupInfo.name;
			if (groupName != null) {
				if (Build.VERSION.SDK_INT >= 17) {
		            /*
                 * SDK 17 added the flags field. If non-zero, the permission group contains
                 * permissions that control access to user personal data.
                 * N.B. These are the permissions groups that are called "dangerous" in
                 * Marshmallow.
                 */
					if (groupInfo.flags != 0) {
						groupName += " (personal)";
					}
				}
				groupNameList.add(groupName);
			}
		}

		Collections.sort(groupNameList);
		groupNameList.add(null);

		return groupNameList;
	}

	static public List<PermissionGroupInfo> getPermissionGroupInfos(Context context) {
		final PackageManager pm = context.getPackageManager();
		if (pm == null) return null;

    /*
     * Get a list of all permission groups and sort them alphabetically.
     * Then add to the end of the list the special case of a null group name. There can be
     * numerous permissions that are not listed under a group name.
     */
		List<PermissionGroupInfo> groupInfoList = null;
		try {
			groupInfoList = pm.getAllPermissionGroups(PackageManager.GET_META_DATA);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return groupInfoList;
	}

	/*
	 * Gets and returns a list of all permission under the specified group, sorted alphabetically.
	 *
	 * N.B. groupName can be null. The docs for PackageManager.queryPermissionsByGroup() say
	 * "Use null to find all of the permissions not associated with a group."
	 */
	static public ArrayList<String> getPermissionNames4Group(Context context, String groupName) {
		final PackageManager pm = context.getPackageManager();
		final ArrayList<String> permissionNameList = new ArrayList<>();

		try {
			List<PermissionInfo> permissionInfoList = pm.queryPermissionsByGroup(groupName, PackageManager.GET_META_DATA);
			if (permissionInfoList != null) {
				for (PermissionInfo permInfo : permissionInfoList) {
					String permName = permInfo.name;
					if (permName == null) {
						permName = "null";
					} else if (permName.isEmpty()) {
						permName = "empty";
					}
					permissionNameList.add(permName);
				}
			}
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		Collections.sort(permissionNameList);

		return permissionNameList;
	}

	/*
	 * Gets and returns a list of all permission under the specified group, sorted alphabetically.
	 *
	 * N.B. groupName can be null. The docs for PackageManager.queryPermissionsByGroup() say
	 * "Use null to find all of the permissions not associated with a group."
	 */
	static public List<PermissionInfo> getPermissionInfos4Group(Context context, String groupName) {
		final PackageManager pm = context.getPackageManager();
		List<PermissionInfo> permissionInfoList = null;

		try {
			permissionInfoList = pm.queryPermissionsByGroup(groupName, PackageManager.GET_META_DATA);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		return permissionInfoList;
	}
}
