package lu.mike.uni.velohproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;
import mehdi.sakout.aboutpage.Element;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .setImage(R.drawable.velho)
                .setDescription(getResources().getString(R.string.ABOUT_DESCRIPTION))
                .addGroup(getResources().getString(R.string.ABOUT_AUTHORS))
                .addItem(createElementAutor(R.string.ABOUT_MIKE_NAME, R.string.ABOUT_MIKE_EMAIL))
                .addItem(createElementAutor(R.string.ABOUT_DREN_NAME, R.string.ABOUT_DREN_EMAIL))
                .addGitHub(getResources().getString(R.string.ABOUT_GITHUB))
                .create();
        setContentView(aboutPage);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Element createElementAutor(int nameID, int emailID) {
        Element elem = new Element();
        elem.setTitle(getResources().getString(nameID) + "\n" + getResources().getString(emailID));
        elem.setIcon(R.drawable.ic_person);
        return elem;
    }
}
