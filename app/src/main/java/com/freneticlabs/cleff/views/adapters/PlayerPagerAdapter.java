package com.freneticlabs.cleff.views.adapters;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.freneticlabs.cleff.R;

/**
 * Created by jcmanzo on 3/29/15.
 */
public abstract class PlayerPagerAdapter extends PagerAdapter{

        private static final String TAG = "FragmentPagerAdapter";
        private static final boolean DEBUG = false;

        private final FragmentManager mFragmentManager;
        private FragmentTransaction mCurTransaction = null;
        private Fragment mCurrentPrimaryItem = null;

        public PlayerPagerAdapter(FragmentManager fm) {
            mFragmentManager = fm;
        }

        /**
         * Return the Fragment associated with a specified position.
         */
        public abstract Fragment getItem(int position);

        @Override
        public void startUpdate(ViewGroup container) {
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }

            final long itemId = getItemId(position);

            // Do we already have this fragment?
            String name = makeFragmentName(container.getId(), itemId);
            Fragment fragment = mFragmentManager.findFragmentByTag(name);
            if (fragment != null) {
                if (DEBUG) Log.v(TAG, "Attaching item #" + itemId + ": f=" + fragment);
                mCurTransaction.attach(fragment);
            } else {
                fragment = getItem(position);
                if (DEBUG) Log.v(TAG, "Adding item #" + itemId + ": f=" + fragment);
                mCurTransaction.add(container.getId(), fragment,
                        makeFragmentName(container.getId(), itemId));
            }
            if (fragment != mCurrentPrimaryItem) {
                fragment.setMenuVisibility(false);
                fragment.setUserVisibleHint(false);
            }

            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            if (DEBUG) Log.v(TAG, "Detaching item #" + getItemId(position) + ": f=" + object
                    + " v=" + ((Fragment)object).getView());
            mCurTransaction.detach((Fragment)object);

            View view = (View) object;
            ImageView imageView = (ImageView)view.findViewById(R.id.player_song_art);
            Drawable drawable = imageView.getDrawable();
            if(drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable != null) {
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    if(bitmap != null && !bitmap.isRecycled()) bitmap.recycle();
                }
            }
            ((ViewPager) container).removeView(view);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            Fragment fragment = (Fragment)object;
            if (fragment != mCurrentPrimaryItem) {
                if (mCurrentPrimaryItem != null) {
                    mCurrentPrimaryItem.setMenuVisibility(false);
                    mCurrentPrimaryItem.setUserVisibleHint(false);
                }
                if (fragment != null) {
                    fragment.setMenuVisibility(true);
                    fragment.setUserVisibleHint(true);
                }
                mCurrentPrimaryItem = fragment;
            }
        }

        @Override
        public void finishUpdate(ViewGroup container) {
            if (mCurTransaction != null) {
                mCurTransaction.commitAllowingStateLoss();
                mCurTransaction = null;
                mFragmentManager.executePendingTransactions();
            }
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return ((Fragment)object).getView() == view;
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        /**
         * Return a unique identifier for the item at the given position.
         *
         * <p>The default implementation returns the given position.
         * Subclasses should override this method if the positions of items can change.</p>
         *
         * @param position Position within this adapter
         * @return Unique identifier for the item at position
         */
        public long getItemId(int position) {
            return position;
        }

        private static String makeFragmentName(int viewId, long id) {
            return "android:switcher:" + viewId + ":" + id;
        }

}
