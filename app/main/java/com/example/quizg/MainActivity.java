package com.example.quizg;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ChasingDots;
import com.github.ybq.android.spinkit.style.CubeGrid;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.github.ybq.android.spinkit.style.Pulse;
import com.github.ybq.android.spinkit.style.RotatingCircle;
import com.github.ybq.android.spinkit.style.RotatingPlane;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.github.ybq.android.spinkit.style.Wave;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView question, answer, category;
    private Button next;
    String text;
    ImageView info;
    Spinner dropdown;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        question = findViewById(R.id.question2);
        answer = findViewById(R.id.answer2);
        category = findViewById(R.id.category);
        next = findViewById(R.id.next);
        dropdown = findViewById(R.id.spin);
        info = findViewById(R.id.info);
        progressBar = findViewById(R.id.spin_kit);
        progressBar.setVisibility(View.INVISIBLE);
        Sprite doubleBounce = new ThreeBounce();
        progressBar.setIndeterminateDrawable(doubleBounce);

        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories, android.R.layout.simple_spinner_item);
        //set the spinners adapter to the previously created one.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = dropdown.getSelectedItem().toString();
                fetchData(text);
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });
    }

    private void fetchData(String text) {

        progressBar.setVisibility(View.VISIBLE);

        String url;
//        Toast.makeText(MainActivity.this, text, Toast.LENGTH_LONG).show();
        if (text.equals("Category")) {
            url = "https://trivia-by-api-ninjas.p.rapidapi.com/v1/trivia";
        } else {
            url = "https://trivia-by-api-ninjas.p.rapidapi.com/v1/trivia?category=" + text;
        }


        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("x-rapidapi-host", "trivia-by-api-ninjas.p.rapidapi.com")
                .addHeader("x-rapidapi-key", "1e400b32cbmsh08a602c81f4a21dp163aeejsnc477b3cb72b7")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Toast.makeText(MainActivity.this, "There is something wrong", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resp = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                JSONArray jsonArray = new JSONArray(resp);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                String q = jsonObject.getString("question");
                                String a = jsonObject.getString("answer");
                                String cat = jsonObject.getString("category");
                                progressBar.setVisibility(View.GONE);
                                question.setText(q + "?");
                                answer.setText(a + ".");
                                category.setText(cat);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });

    }
}