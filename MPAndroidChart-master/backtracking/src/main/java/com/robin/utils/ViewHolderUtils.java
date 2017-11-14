package com.robin.utils;

import android.util.SparseArray;
import android.view.View;

/** 
 * @项目名:		dianjia
 * @包名：		com.sz1card1.commonmodule.holder
 * @类名：		ViewHolderUtils
 * @创建者:		robin
 * @创建时间:	2015-11-12 下午4:28:04
 * 
 * @描述：		ViewHolder 工具类
 * 
 */

public class ViewHolderUtils
{
	@SuppressWarnings("unchecked")
	public static <T extends View> T get(View view, int id) {
		SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
		if (viewHolder == null) {
			viewHolder = new SparseArray<View>();
			view.setTag(viewHolder);
		}
		View childView = viewHolder.get(id);
		if (childView == null) {
			childView = view.findViewById(id);
			viewHolder.put(id, childView);
		}
		return (T) childView;
	}
}
