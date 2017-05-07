package co.oriens.bluelight;

//Gerekli eklentiler import ediliyor
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import layout.SetAlarmFragment;
import layout.WakeUp;
import layout.WorkingSessionFragment;

public class TabActivity extends AppCompatActivity {

    //Değişkenler oluşturuluyor
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab); //Görünüm activity_tab.xml dosyasından alınıyor

        //Sekmeli aktiviteyi oluşturan Android Studio hazır kodları
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }

    // Fragmanların görünmesini sağlayan sınıf
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        //Hangi sayfada olduğunu kontrol edip fragmanın layoutunu oluşturan method
        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    WakeUp wakeUp= new WakeUp();
                    return wakeUp;
                case 1:
                    WorkingSessionFragment workingSessionFragment= new WorkingSessionFragment();
                    return workingSessionFragment;
                case 2:
                    SetAlarmFragment setAlarmFragment=new SetAlarmFragment();
                    return  setAlarmFragment;
            }
            return null;
        }

        //Kaç fragman olduğunu belirleyen Method
        @Override
        public int getCount() {
            return 3; //Toplam 3 sayfa fragman var
        }

        //Fragman başlıklarını tanımlayan method
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.title_wake_up); //1. Fragmanın başlığı
                case 1:
                    return getString(R.string.title_work); //2. Fragmanın başlığı
                case 2:
                    return getString(R.string.title_set_alarm); //3. Fragmanın başlığı
            }
            return null;
        }
    }
}
