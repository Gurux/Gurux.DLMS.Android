package gurux.dlms.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

import gurux.dlms.GXSimpleEntry;

public class GXTab extends LinearLayout {

    private final ArrayList<GXSimpleEntry<String, Fragment>> mFragments = new ArrayList<>();

    private final TabLayout mTabLayout;
    private final ViewPager2 mViewPager;

    public GXTab(@NonNull Fragment fragment) {
        super(fragment.requireContext());
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        Context context = fragment.requireContext();
        mTabLayout = new TabLayout(context);
        mViewPager = new ViewPager2(context);
        mViewPager.setId(View.generateViewId());
        mViewPager.setLayoutParams(new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        addView(mTabLayout);
        addView(mViewPager);
        mViewPager.setAdapter(new androidx.viewpager2.adapter.FragmentStateAdapter(fragment) {
            @Override
            public int getItemCount() {
                return mFragments.size();
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return mFragments.get(position).getValue();
            }
        });
    }

    public void addTab(final String name, Fragment fragment) {
        mFragments.add(new GXSimpleEntry<>(name, fragment));
        new TabLayoutMediator(mTabLayout, mViewPager,
                (tab, position) -> tab.setText(mFragments.get(position).getKey())
        ).attach();
    }
}