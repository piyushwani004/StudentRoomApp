package com.piyush004.studentroom.Room.Topic;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.piyush004.studentroom.R;
import com.piyush004.studentroom.Room.storage.StorageHandler.RoomStorageHandler;
import com.piyush004.studentroom.URoom;

public class SubjectTopicActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private RecyclerView recyclerView;
    private EditText editText;
    private TextView textView;
    private FirebaseRecyclerOptions<TopicModel> options;
    private FirebaseRecyclerAdapter<TopicModel, TopicHolder> adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    int[] animationList = {R.anim.layout_animation_up_to_down,
            R.anim.layout_animation_right_to_left,
            R.anim.layout_animation_down_to_up,
            R.anim.layout_animation_left_to_right};
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_topic);

        recyclerView = findViewById(R.id.RecycleViewSubjectTopic);
        floatingActionButton = findViewById(R.id.floatingActionButtonSubjectTopic);
        toolbar = findViewById(R.id.toolbarSubjectTopic);
        swipeRefreshLayout = findViewById(R.id.swipeRoomTopic);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        toolbar.setTitle(URoom.RoomSubject);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

                finish();
            }
        });


        final DatabaseReference df = FirebaseDatabase.getInstance().getReference().child("ManagedRoom").child(URoom.UserRoom).child("Topics").child(URoom.RoomSubject);
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = getLayoutInflater();
                View dialogLayout = inflater.inflate(R.layout.subject_topic_dialog, null);
                editText = dialogLayout.findViewById(R.id.editTextSubjectTopic);
                textView = dialogLayout.findViewById(R.id.textViewSubject_Name_Topic);
                textView.setText(URoom.RoomSubject);
                builder.setTitle("Topic Name");
                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String TName = editText.getText().toString();

                        if (TName.isEmpty()) {
                            editText.setError("Please Enter Topic Name");
                            editText.requestFocus();
                        } else if (!(TName.isEmpty())) {

                            String key = df.push().getKey();
                            df.child(key + TName).setValue(TName);

                            Toast.makeText(getApplicationContext(), "Topic Created...", Toast.LENGTH_SHORT).show();

                            if (i < animationList.length - 1) {
                                i++;
                            } else {
                                i = 0;
                            }
                            runAnimationAgain();

                        }
                    }
                });

                builder.setNegativeButton("Closed", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.setView(dialogLayout);
                builder.show();
            }
        });


        options = new FirebaseRecyclerOptions.Builder<TopicModel>().setQuery(df, new SnapshotParser<TopicModel>() {

            @NonNull
            @Override
            public TopicModel parseSnapshot(@NonNull DataSnapshot snapshot) {
                return new TopicModel(

                        snapshot.getValue(String.class)
                );

            }
        }).build();

        adapter = new FirebaseRecyclerAdapter<TopicModel, TopicHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull TopicHolder holder, int position, @NonNull final TopicModel model) {

                holder.setTxtTopicName(model.getTopicName());
                holder.setTxtSubjectName(URoom.RoomSubject);

                holder.textViewTitle_Topic_Name.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        URoom.SubjectTopic = model.getTopicName();
                        Intent intent = new Intent(SubjectTopicActivity.this, RoomStorageHandler.class);
                        startActivity(intent);
//                        Toast.makeText(getContext(), "clicked...", Toast.LENGTH_SHORT).show();

                    }
                });

            }

            @NonNull
            @Override
            public TopicHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_topic_card, parent, false);

                return new TopicHolder(view);
            }
        };

        adapter.startListening();
        recyclerView.setAdapter(adapter);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                runAnimationAgain();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }, 1000);

            }
        });


    }

    private void runAnimationAgain() {
        final LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this, animationList[i]);
        recyclerView.setLayoutAnimation(controller);
        adapter.notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
    }

}