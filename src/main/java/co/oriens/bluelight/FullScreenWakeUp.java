package co.oriens.bluelight;

//Gerekli eklentiler import ediliyor

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class FullScreenWakeUp extends AppCompatActivity {

    //Objeler ve değişkenler oluşturuluyor
    ImageButton stopButton;//Seansı bitirme butonu
    Window window;//Telefon ekranı
    RelativeLayout relativeLayout;//Aktvitenin layoutu
    TextView timerTextView;//Geri sayım için metin kutusu
    TextView questionTextView;//Matematik sorusu metin kutusu
    Button answerButton1, answerButton2, answerButton3;//Matematik soruları için cevap butonları
    int correctAnswerButton;//Doğru cevabın hangi cevap buttonunda olduğunu belirleyen tam sayı değişkeni

    //Şu anki saniye değişkeni

    int currentSecond;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_wake_up);

        //Objeler tanımlanıyor
        //Matematik soruları için
        questionTextView = (TextView) findViewById(R.id.questionTextView);
        answerButton1 = (Button) findViewById(R.id.answer1Button);
        answerButton2 = (Button) findViewById(R.id.answer2Button);
        answerButton3 = (Button) findViewById(R.id.answer3Button);
        AnswerButtonsSetOnClickListener(); //Cevap buttonlarına tıklanabilme özelliği veren method çalıştırılıyor
        //Layout için
        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        window = getWindow();//window değişkenine telefon ekranı atanıyor
        //Durdurma butonu için
        stopButton = (ImageButton) findViewById(R.id.pauseButton);
        stopButton.setOnClickListener(new View.OnClickListener() { //stopButton a tıklanabilme özelliği veriliyor
            @Override
            public void onClick(View v) {
                StopButtonPressed();//Butona tıklandığında StopButtonPressed() methodu çalışıyor
            }
        });

        currentSecond=60; //Kalan saniye değşkenini 60 a olarak belirle
        StartTimer();//Geri sayım başlatma methodu çalıştırılıyor
        NewQuestion();//Yeni soru ekrana getiren method çalıştırılıyor
    }

    //Ekran parlaklığını arttırma methodu
    void MakeBright(float targetBrigthness) {
        WindowManager.LayoutParams layoutParams = window.getAttributes();//Telefon ekranı parametreleri alınıyor

        layoutParams.screenBrightness = targetBrigthness;//Parlaklık parametresi targetBrigthness değerine göre artırılıyor
        window.setAttributes(layoutParams);//Değişiklikler telefon ekranına uygulanıyor
    }

    //Gei sayım başlatma methodu
    void StartTimer() {
        timerTextView.setVisibility(View.VISIBLE);//Başlangıçta görünmez olan geri sayım metin kutusu görünür yapılıyor
        new CountDownTimer(60000, 1000) {//Yeni geri sayım methodu oluşturuluyor

            public void onTick(long millisUntilFinished) { //Her saniye geçtiğinde çalışan method
                timerTextView.setText((millisUntilFinished / 1000) + " " + getString(R.string.wake_up_session_countdown)); //Metin kutusundaki kalan saniyeler güncelleniyor

                //Kademeli olarak parlaklığı artırma koşulu
                int currentSecond=60;
                if( millisUntilFinished>=44000 && (millisUntilFinished / 1000) != currentSecond){ // Eğer 44 saniyeden fazla kalmışssa parlaklığı %5 artır (her saniye)
                    float targetBrigthness; // Hedef parlaklık değişkeni
                    targetBrigthness = (61-(millisUntilFinished/1000)) * 0.05f; // Her 4 saniyede bir hedef parlaklık %20 artırılıyor
                    MakeBright(targetBrigthness); //Parlaklığı artırma methodu çalıştırılıyor

                    currentSecond=(int)(millisUntilFinished / 1000); //Şu anki saniye değişkenini yenile
                }
            }

            //Geri sayım bittiğinde uygulanacaklar methodu
            public void onFinish() {
                timerTextView.setText(getString(R.string.wake_up_session_finished));//Geri sayım metin kutusuna "ARTIK ENERJİKSİNİZ" yazısı atanıyor
                stopButton.setVisibility(View.VISIBLE);//Seansı bitirme butonu görünür yapılıyor
            }
        }.start();
    }

    //Seansı bitirme butonunu tıklanınca çalışan method
    void StopButtonPressed() {
        finish();//Seans sonlandırılıyor (Ana aktiviteye geri dönülüyor)
    }

    //Cevap butonlarını tıklanabilri yapan method
    void AnswerButtonsSetOnClickListener() {
        //3 butondan biri tıklanınca butonun numarası girdi olucak şekilde cevap kontrol etme methodu çalışıyor
        answerButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnswer(0);
            }
        });
        answerButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnswer(1);
            }
        });
        answerButton3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckAnswer(2);
            }
        });
    }

    //Yeni soru oluşturma methodu
    void NewQuestion() {
        //Tüm cevap butonları yeniden görünür yapılıyor
        answerButton1.setVisibility(View.VISIBLE);
        answerButton2.setVisibility(View.VISIBLE);
        answerButton3.setVisibility(View.VISIBLE);
        //Rastgele sayı oluşturan operatör tanımlanıyor
        Random random = new Random();
        //Rastgele sayılar ve yapılacak olan matematiksel işlem belirleniyor
        int firstNumber = random.nextInt(50);//1 ve 50 arasında rastgele tam sayı belirleniyor
        int secondNumber = random.nextInt(50);
        int operation = random.nextInt(3);// 0,1 veya 2 sayısı işlem için rastgele seçiliyor(0: toplama, 1: çıkarma, 2:çarpma)
        String operationSymbol;//İşlem operatörünün ekrana yazlması için string değişkeni oluşturuluyor
        int answer;//Cevap için tam sayı değişkeni oluşturuluyor

        //Cevap değişkeni oluşturulan rastgele sayılar ve işlem sonucu tanımlanıyor
        if (operation == 0) {
            operationSymbol = " + ";
            answer = firstNumber + secondNumber;
        } else if (operation == 1) {
            operationSymbol = " - ";
            answer = firstNumber - secondNumber;
        } else {
            operationSymbol = " x ";
            answer = firstNumber * secondNumber;
        }

        questionTextView.setText(firstNumber + operationSymbol + secondNumber + " = ?");//Soru ekrana yazdırılıyor
        //Cevap buttonlarına rastgele tam sayılar yazılıyor
        answerButton1.setText("" + random.nextInt(200));
        answerButton2.setText("" + random.nextInt(200));
        answerButton3.setText("" + random.nextInt(200));
        //Bir button rastgele seçiliyor ve ona da cevap tam sayısı yazılıyor
        correctAnswerButton = random.nextInt(3);
        if (correctAnswerButton == 0) {
            answerButton1.setText("" + answer);
        } else if (correctAnswerButton == 1) {
            answerButton2.setText("" + answer);
        } else if (correctAnswerButton == 2) {
            answerButton3.setText("" + answer);
        }

    }

    //Cevap kontrol etme methodu
    void CheckAnswer(int choice) {
        //Cevap değişkeninin(correctAnswerButton) tam sayı değeriyle kullanıcın seçtiği butonun(choice) tam sayı değerleri karşılaştırılıyor

        if (choice == correctAnswerButton) {//Seçim ve cevap uyuşuyor ise
            CorrectAnswer();//Doğru cevap methodu çalıştırılıyor
        } else {//Seçim ve cevap uyuşmuyorsa
            WrongAnswer(choice);//Yanlış cevap methodu çalıştırılıyor
        }
    }

    //Doğru cevap methodu
    void CorrectAnswer() {
        NewQuestion();//Yeni soru oluşturma methodunu çalıştırıyor
    }

    //Yanlış cevap methodu
    void WrongAnswer(int choice) {
        //Seçilen ve cevap ile uyuşmayan buton görünmez yapılıyor
        if (choice == 0) {
            answerButton1.setVisibility(View.INVISIBLE);
        } else if (choice == 1) {
            answerButton2.setVisibility(View.INVISIBLE);
        } else if (choice == 2) {
            answerButton3.setVisibility(View.INVISIBLE);
        }
    }

    //Geri butonuna basılarak çıkılmasını engelleme methodu
    @Override
    public void onBackPressed() {
        //hiçbir şey yapma
    }
}
