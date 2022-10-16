package com.noel.bar_hub;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

/*  @Author Noel.Eugene.Habaa */

public class MainActivity extends AppCompatActivity {

      // Variable declaration

      // Text to speech variables

       private TextToSpeech textToSpeech;
       private Button btn , btn2;
       private EditText editText;

       // This info will come from you database where admin will add products.

       private String welcome = "Hello and welcome to this app.Speak to search for products. Currently the only product is milk";
       private String milkInfo = "Name of brand is brook side. Size 250 ML. Expiry data 3/30/2021";

       // Speech to text variables

       private static final int REQUEST_CODE = 100;
       private TextView textOutput;

  @Override
  public void onCreate(Bundle savedInstanceState){
      super.onCreate(savedInstanceState);

      setContentView(R.layout.activity_main);


      // check for permissions (needs update)
     // if(ContextCompat.checkSelfPermission( this, Manifest.permission) != PackageManager.PERMISSION_GRANTED)


      btn = (Button) findViewById(R.id.btn);
      btn2 = (Button)findViewById(R.id.btn2);

      editText = (EditText) findViewById(R.id.et);

      textOutput = (TextView) findViewById(R.id.textOutput);

      // where the magic happens. Initializing androids tts

      textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
          @Override
          public void onInit(int status) {
              if (status == TextToSpeech.SUCCESS){

                  // Use this line to change language that the user is going to speak e.g locale.Kiswahili as long as language is
                  // suppported.

                  int ttsLang = textToSpeech.setLanguage(Locale.ENGLISH);

                  // error handling and debugging

                  if(ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED){
                      Log.e("TTS" , "The language is not supported!");
                  }else {
                      Log.i("TTS","Language Supported.");
                  }
                      Log.i("TTS", "Init success.");
              }else {

                      Toast.makeText(getApplicationContext(),"TTS Init failed!", Toast.LENGTH_SHORT).show();
              }
          }
      });

      // Speak when button is pressed!. Text comes from edittext but can easily be changed to text saved from speech to text.

      btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              String data = editText.getText().toString();

              // text to speak just replace 'data' to something else for example hello
              int speechStatus = textToSpeech.speak(welcome,TextToSpeech.QUEUE_FLUSH,null);

              if(speechStatus == TextToSpeech.ERROR){
                  Log.e("TTS","Error with speech");
              }

              new CountDownTimer(50000, 100){

                  @Override
                  public void onTick(long millisUntilFinished) {

                  }

                  @Override
                  public void onFinish() {
                      textToSpeech.speak("Please request the product you want by speaking to your phone",TextToSpeech.QUEUE_FLUSH,null);
                  }
              }.start();
          }
      });

     // App to listen
      btn2.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              listenToUser();
          }
      });

  }

  // start intent to speech recognizer

  public void listenToUser(){

      Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
      intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
      intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
      intent.putExtra(RecognizerIntent.EXTRA_PROMPT ,"Need to speak");

      try{
          startActivityForResult(intent, REQUEST_CODE);
      }catch(ActivityNotFoundException a){
          Toast.makeText(getApplicationContext(), "Sorry your device is not supported", Toast.LENGTH_SHORT).show();
      }
  }

  // get results from speech recognizer and pass to textOutput

  @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
      super.onActivityResult(requestCode,resultCode,data);

      switch (requestCode){
          case REQUEST_CODE:{
              if(resultCode == RESULT_OK && null != data){
                  ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                  textOutput.setText(result.get(0));
                  checkWordResults();
              }
              break;
          }
      }
  }

  private void checkWordResults(){

      String RText = textOutput.getText().toString();

      // use editText.setText() to add the response to your edit text search bar then send it to database
      // very simplified but you get the gist. We get response from our speech to text and compare

      if(RText == "Milk"){
          textToSpeech.speak(milkInfo,TextToSpeech.QUEUE_FLUSH,null);
      }else {
          textToSpeech.speak("That Product does not exist in our database, try again!",TextToSpeech.QUEUE_FLUSH,null);
      }
  }


  @Override
    public void onDestroy(){
      super.onDestroy();
      if(textToSpeech != null){
          textToSpeech.stop();
          textToSpeech.shutdown();
      }
  }

}
