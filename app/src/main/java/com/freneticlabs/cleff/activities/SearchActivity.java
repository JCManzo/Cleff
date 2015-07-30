package com.freneticlabs.cleff.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.freneticlabs.cleff.R;
import com.quinny898.library.persistentsearch.SearchBox;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class SearchActivity extends AppCompatActivity {
    @Bind(R.id.search_box) SearchBox mSearchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ButterKnife.bind(this);

        setUpSearch();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);

    }

    private void setUpSearch() {

        mSearchBox.enableVoiceRecognition(this);
        mSearchBox.setLogoText(getString(R.string.search_box_library_title));
        mSearchBox.toggleSearch();
        mSearchBox.setSearchListener(new SearchBox.SearchListener() {
            @Override
            public void onSearchOpened() {

            }

            @Override
            public void onSearchCleared() {
                Timber.d("SEARCH CLEARED");
            }

            @Override
            public void onSearchClosed() {
                Timber.d("SEARCH CLOSED");
                finish();
            }

            @Override
            public void onSearchTermChanged() {

            }

            @Override
            public void onSearch(String s) {

            }
        });

    }



}
