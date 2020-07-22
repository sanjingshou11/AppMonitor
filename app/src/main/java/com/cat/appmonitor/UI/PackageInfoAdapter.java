package com.cat.appmonitor.UI;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import com.cat.appmonitor.R;

import java.util.ArrayList;
import java.util.List;


public class PackageInfoAdapter extends BaseAdapter implements Filterable {

	private LayoutInflater mlayoutInflater = null;
	private List<AppInfo> mpackageInfo = null;
	private boolean[] misSelected; 

	public PackageInfoAdapter(Context context, List<AppInfo> packageInfo, boolean[] isSelected) {
		super();
		mlayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mpackageInfo = packageInfo;
		misSelected = isSelected;
	}
	public List<AppInfo> getPackageInfo(){
		return mpackageInfo;
	}

	@Override
	public int getCount() {
		return mpackageInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mpackageInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		ViewHolder holder = null;

		if (convertView == null) {
			view = mlayoutInflater.inflate(R.layout.process_item, null);
			holder = new ViewHolder(view);
			view.setTag(holder);
		} else {
			view = convertView; 
			holder = (ViewHolder)view.getTag();
		}
		
		AppInfo appInfo = (AppInfo)getItem(position);
		holder.appIcon.setImageDrawable(appInfo.getAppIcon());
		holder.appName.setText(appInfo.getAppLabel());
		holder.appPkgName.setText(appInfo.getPkgName());
		holder.isChooseButton.setChecked(misSelected[position]);
		return view;
	}

	class ViewHolder {

		ImageView appIcon;
		TextView appName;
		TextView appPkgName;
		CheckBox isChooseButton;

		public ViewHolder(View view) {
			this.appIcon = (ImageView) view.findViewById(R.id.app_icon);
            this.appName = (TextView) view.findViewById(R.id.app_name);
			this.appPkgName = (TextView) view.findViewById(R.id.package_name);
			this.isChooseButton = (CheckBox)view.findViewById(R.id.isChoose);
		}
	}

	//过滤相关
	/**
	 * This lock is also used by the filter
	 * (see {@link #getFilter()} to make a synchronized copy of
	 * the original array of data.
	 * 过滤器上的锁可以同步复制原始数据。
	 */
	private final Object mLock = new Object();

	// A copy of the original mObjects array, initialized from and then used instead as soon as
	// the mFilter ArrayFilter is used. mObjects will then only contain the filtered values.
	//对象数组的备份，当调用ArrayFilter的时候初始化和使用。此时，对象数组只包含已经过滤的数据。
	private ArrayList<AppInfo> mOriginalValues;
	private ArrayFilter mFilter;

	@Override
	public Filter getFilter() {
		if (mFilter == null) {
			mFilter = new ArrayFilter();
		}
		return mFilter;
	}

	/**
	 * 过滤数据的类
	 */
	/**
	 * <p>An array filter constrains the content of the array adapter with
	 * a prefix. Each item that does not start with the supplied prefix
	 * is removed from the list.</p>
	 * <p/>
	 * 一个带有首字母约束的数组过滤器，每一项不是以该首字母开头的都会被移除该list。
	 */
	private class ArrayFilter extends Filter {
		//执行刷选
		@Override
		protected FilterResults performFiltering(CharSequence prefix) {
			FilterResults results = new FilterResults();//过滤的结果
			//原始数据备份为空时，上锁，同步复制原始数据
			if (mOriginalValues == null) {
				synchronized (mLock) {
					mOriginalValues = new ArrayList<>(mpackageInfo);
				}
			}
			//当首字母为空时
			if (prefix == null || prefix.length() == 0) {
				ArrayList<AppInfo> list;
				synchronized (mLock) {//同步复制一个原始备份数据
					list = new ArrayList<>(mOriginalValues);
				}
				results.values = list;
				results.count = list.size();//此时返回的results就是原始的数据，不进行过滤
			} else {
				String prefixString = prefix.toString().toLowerCase();//转化为小写

				ArrayList<AppInfo> values;
				synchronized (mLock) {//同步复制一个原始备份数据
					values = new ArrayList<>(mOriginalValues);
				}
				final int count = values.size();
				final ArrayList<AppInfo> newValues = new ArrayList<>();

				for (int i = 0; i < count; i++) {
					final AppInfo value = values.get(i);//从List<User>中拿到User对象
					//final String packagename = value.toString().toLowerCase();
					final String packagename = value.getPkgName().toString().toLowerCase();//User对象的name属性作为过滤的参数
					// First match against the whole, non-splitted value
					if (packagename.startsWith(prefixString) || packagename.indexOf(prefixString.toString()) != -1) {//第一个字符是否匹配
						newValues.add(value);//将这个item加入到数组对象中
					} else {//处理首字符是空格
						final String[] words = packagename.split(" ");
						final int wordCount = words.length;

						// Start at index 0, in case packagename starts with space(s)
						for (int k = 0; k < wordCount; k++) {
							if (words[k].startsWith(prefixString)) {//一旦找到匹配的就break，跳出for循环
								newValues.add(value);
								break;
							}
						}
					}
				}
				results.values = newValues;//此时的results就是过滤后的List<User>数组
				results.count = newValues.size();
			}
			return results;
		}

		//刷选结果
		@Override
		protected void publishResults(CharSequence prefix, FilterResults results) {
			//noinspection unchecked
			mpackageInfo = (List<AppInfo>) results.values;//此时，Adapter数据源就是过滤后的Results
			if (results.count > 0) {
				notifyDataSetChanged();//这个相当于从mDatas中删除了一些数据，只是数据的变化，故使用notifyDataSetChanged()
			} else {
				/**
				 * 数据容器变化 ----> notifyDataSetInValidated

				 容器中的数据变化  ---->  notifyDataSetChanged
				 */
				notifyDataSetInvalidated();//当results.count<=0时，此时数据源就是重新new出来的，说明原始的数据源已经失效了
			}
		}
	}
}
