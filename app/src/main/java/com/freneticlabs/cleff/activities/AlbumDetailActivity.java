package com.freneticlabs.cleff.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.freneticlabs.cleff.R;
import com.freneticlabs.cleff.fragments.AlbumDetailFragment;

public class AlbumDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);

        AlbumDetailFragment albumDetailFragment = new AlbumDetailFragment();
        int album_id = (int)getIntent().getSerializableExtra(AlbumDetailFragment.ALBUM_INFO_ID);
        String album_name = (String)getIntent().getSerializableExtra(AlbumDetailFragment.ALBUM_INFO_NAME);

        Bundle bundle = new Bundle();
        bundle.putInt(AlbumDetailFragment.ALBUM_INFO_ID, album_id);
        bundle.putString(AlbumDetailFragment.ALBUM_INFO_NAME, album_name);

        albumDetailFragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.album_detail_container, albumDetailFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_album_detail, menu);
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


}
