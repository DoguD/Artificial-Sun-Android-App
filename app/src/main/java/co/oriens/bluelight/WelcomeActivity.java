package co.oriens.bluelight;

//Gerekli eklentiler import ediliyor
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    //Değişkenler oluşturuluyor
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private Button btnSkip, btnNext;
    private PrefManager prefManager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Uygulamanın ilk kere açılıp açılmadığı kontrol ediliyor
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) { //Daha önceden uygulama açılmışsa
            launchMainActivity(); //Ana aktivite başlatma methodu çalıştırılıyor
            finish(); //Bu aktivite sonlandırılıyor
        }

        //Telefonun SDK'sı 21 üstündeyse temiz bir görünüm için aksiyon çubuğu saydam yapılıyor
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome); //Görünüm activity_welcome.xml dosyasından alınıyor

        // Değişkenler tanımlanıyor
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (Button) findViewById(R.id.btn_skip);
        btnNext = (Button) findViewById(R.id.btn_next);

        layouts = new int[]{// layouts dizisi oluşturuluyor
                //Tanıtım slaytları diziye ekleniyor
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4};

        AddBottomDots(0); //İlerlemeyi belli eden noktaları oluşturan method

        ChangeStatusBarColor(); //Bildirim çubuğunu saydam yapan method çalışıyor

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchMainActivity(); //Slaytın sonuna gelince ana aktiviteyi açar
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*İleri tuşuna basılınca kontrol eder:
                * Son slayt değilse bir sonrakine gider
                * Son slayta gelince ana aktiviteyi başlatır*/
                int current = getItem(+1);
                if (current < layouts.length) {
                    viewPager.setCurrentItem(current);
                } else {
                    launchMainActivity();
                }
            }
        });
    }

    //Sayfanın altındaki noktaları kontrol eden method
    private void AddBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }


    //Tanıtımdan sonra hangi aktivitenin açılacağını kontrol eden method
    private void launchMainActivity() {
        prefManager.setFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, TabActivity.class));
        finish();
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            AddBottomDots(position);

            //Son sayfada olup olmadığını kontrol et
            if (position == layouts.length - 1) {
                btnNext.setText(getString(R.string.welcome_slide_end)); //Son sayfada butonun yazısını "ANLADIM" olarak değiştir
                btnSkip.setVisibility(View.GONE);
            } else {
                btnNext.setText(getString(R.string.next)); //Son sayfa değilse butonun yazısı "İLERİ" kalsın
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    //Eğer telefonun SDK'i LOLLIPOP'dan yüksekse statüs çubuğunu saydam yapan method
    private void ChangeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    //Fragmanları kontrol eden sınıf
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        //Görünümü ayarlayan method
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        //Kaç slayt olduğunu belirleyen method
        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}
