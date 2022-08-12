package com.example.google_lens_application;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    FloatingActionButton camera,search;
    ImageView imageView;
    RecyclerView recyclerView;
    SearchRVAdapter searchRVAdapter;
    ArrayList<SearchRVModal> searchRVModalArrayList;
    Bitmap bitmap;
    String title,displayed_link,snippet,link;
    int REQUEST_CODE=1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera=findViewById(R.id.camera_button);
        search=findViewById(R.id.search);
        imageView=findViewById(R.id.imageView);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false));

        searchRVModalArrayList=new ArrayList<>();
        searchRVAdapter=new SearchRVAdapter(this,searchRVModalArrayList);
        recyclerView.setAdapter(searchRVAdapter);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRVModalArrayList.clear();
                searchRVAdapter.notifyDataSetChanged();
                takePictureIntent();
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchRVModalArrayList.clear();
                getResults();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==REQUEST_CODE && resultCode==RESULT_OK)
        {
            Bundle bundle=data.getExtras();
            bitmap=(Bitmap) bundle.get("data");
            imageView.setImageBitmap(bitmap);
        }

    }

    private void takePictureIntent() {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intent.resolveActivity(getPackageManager())!=null)
        {
            startActivityForResult(intent,REQUEST_CODE);
        }
    }

    private void getResults()
    {
        FirebaseVisionImage image=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionImageLabeler labeler= FirebaseVision.getInstance().getOnDeviceImageLabeler();
        labeler.processImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
            @Override
            public void onSuccess(List<FirebaseVisionImageLabel> firebaseVisionImageLabels) {
                String searchQuery=firebaseVisionImageLabels.get(0).getText();
                getSearchResults(searchQuery);
            }
        }).addOnFailureListener(e -> Toast.makeText(MainActivity.this,"Failed to Detect Message",Toast.LENGTH_SHORT).show());
    }

    private void getSearchResults(String searchQuery) {
        String url="https://serpapi.com/search.json?q="+searchQuery+"&location=maharashtra,India&hl=en&api_key=9f4e077e209ef0fb90fa35123daab4fdb1929ae129091fcc07ad2b657d2fc879";
        RequestQueue queue= Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray organicArray=response.getJSONArray("organic_results");
                    for(int i=0;i<organicArray.length();i++)
                    {
                        JSONObject organicObj=organicArray.getJSONObject(i);
                        if(organicObj.has("title"))
                        {
                            title=organicObj.getString("title");
                        }

                        if(organicObj.has("link"))
                        {
                            link=organicObj.getString("link");
                        }

                        if(organicObj.has("displayed_link"))
                        {
                            displayed_link=organicObj.getString("displayed_link");
                        }

                        if(organicObj.has("snippet"))
                        {
                            snippet=organicObj.getString("snippet");
                        }

                        searchRVModalArrayList.add(new SearchRVModal(title,link,displayed_link,snippet));
                    }
                    searchRVAdapter.notifyDataSetChanged();


                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this,"Fail to get result",Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);
    }
}