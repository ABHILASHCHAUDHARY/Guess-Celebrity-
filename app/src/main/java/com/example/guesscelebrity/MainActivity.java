package com.example.guesscelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebUrls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    ImageView imageView;
    int chossenCeleb =0;

    String[] answers = new String[4];
    int locationOfCorrectAnswer = 0;

    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void celebChosen(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Incorrect! Right one is "+ celebNames.get(chossenCeleb), Toast.LENGTH_SHORT).show();
        }

        newquestion();
    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection =(HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream in = urlConnection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return  myBitmap;

            } catch (Exception e){
                e.printStackTrace();
                return  null;
            }
        }
    }

    public class DownloadData extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result= "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){

                    char current = (char) data;
                    result += current;
                    data= reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    public void newquestion() {
        try {
            Random random = new Random();
            chossenCeleb = random.nextInt(celebUrls.size());

            ImageDownloader imageTask = new ImageDownloader();

            Bitmap celebImage = imageTask.execute(celebUrls.get(chossenCeleb)).get();
            imageView.setImageBitmap(celebImage);

            locationOfCorrectAnswer = random.nextInt(4);
            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {
                if (i == locationOfCorrectAnswer) {
                    answers[i] = celebNames.get(chossenCeleb);
                } else {
                    incorrectAnswerLocation = random.nextInt(celebUrls.size());

                    while (incorrectAnswerLocation == chossenCeleb) {
                        incorrectAnswerLocation = random.nextInt(celebUrls.size());
                    }
                    answers[i] = celebNames.get(incorrectAnswerLocation);
                }
            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       imageView = (ImageView) findViewById(R.id.imageView);
       button0 =(Button) findViewById(R.id.button0);
        button1 =(Button) findViewById(R.id.button1);
        button2 =(Button) findViewById(R.id.button2);
        button3 =(Button) findViewById(R.id.button3);



        DownloadData task = new DownloadData();
        String result;
        try {
            result = task.execute("https://www.indiaforums.com/person").get();

String[] splitResult = result.split("popular celebrities");


           Pattern p = Pattern.compile("<img src=\"(.*?)\"");
           Matcher  m = p.matcher(splitResult[1]);

            while (m.find()){
                celebUrls.add(m.group(1));
                if(m.group(1).equals("https://img.indiaforums.com/person/320x240/0/0004-amitabh-bachchan.jpg?c=5wED0D")){
                    break;
                }
            }


           p = Pattern.compile("alt=\"(.*?)\"");
           m = p.matcher(splitResult[1]);

            while (m.find()){
                celebNames.add(m.group(1));
                if(m.group(1).equals("Amitabh Bachchan")){
                    break;
                }
            }

            newquestion();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
