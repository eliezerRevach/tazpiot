package com.example.tazpitapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * An activity representing a list of Scenarios. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link SceneriosDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class SceneriosListActivity extends AppCompatActivity {

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;
    private static boolean gpsState; //gps on off
    private static String getlatofgps;//lat of gps
    private static String getlongofgps; //long of gps
    private  static  int bit=0;
    static boolean isInit = true;
    private  List <String> list = new ArrayList<>();
    List<DummyContent.DummyItem> items_all;
    CollectionReference itemRef;

    Button login;
    TextView tv;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference document_Ref =db.collection("Scenarios");
    @Override
    public void onResume() {
        super.onResume();
        setContentView(R.layout.activity_scenerios_list);
        Log.d("Restart", "dummy_01="+DummyContent.DummyItem.class);

        Log.d("Restart", " onResume1");

        creat_list();
    }
//        @Override
//        protected void onCreate (Bundle savedInstanceState){
//            super.onCreate(savedInstanceState);
//            creat_list();
//        }

    public void  creat_list(){
        Log.d("Restart", "dummy_02="+DummyContent.ITEMS);
        if (isInit) {
            isInit = false;
            startActivity(new Intent(this, SceneriosListActivity.class));
            finish();
            Log.d("Restart", "creat_list");
        }
        Log.d("Restart", "dummy_03="+DummyContent.ITEMS);
        FirebaseFirestore firestoreRootRef = FirebaseFirestore.getInstance();
        itemRef = firestoreRootRef.collection("Scenarios");

        readData(new FirestoreCallback() {
            @Override
            public void onCallback(List<String> list) {
                Log.d("Restart", "dummy2="+DummyContent.ITEMS);
                View recyclerView = findViewById(R.id.scenerios_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);
            }
        });

        gpsState = getStateOfGps();//start_Scen12
        if (gpsState) {
            //get lat and long
            Log.d("onComplet", "start_Scen_indx1");
            getlatofgps = getlatOfGps();
            getlongofgps = getlongOfGps();
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onComplet", "onClick_1");
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        if (findViewById(R.id.scenerios_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            Log.d("onComplet", "start_Scen_indx2");
            mTwoPane = true;
        }
        Log.d("onComplet", "start_Scen+indx=" + DummyContent.ITEMS);
        Log.d("onComplet", "start_Scen0");

    }
    private  void readData( FirestoreCallback firestoreCallback){
        Log.d("onComplet","readData_1=");
        itemRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (DocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                        Log.d("output", "i am accepted="+String.valueOf(document.getId()));
                    }
                    Log.d("Restart", "dummy1="+DummyContent.ITEMS);
                    firestoreCallback.onCallback(list);
                }
                else{
                    Log.d("onComplet","inside onComplete () ERRORR");
                }
            }
        });
    }
    private  interface  FirestoreCallback{
        void onCallback(List<String>list);
    }
    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        Log.d("Restart", "dummy3="+DummyContent.ITEMS);

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, DummyContent.ITEMS, mTwoPane));

    }
    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        private final SceneriosListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues; //items of the list showing
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("onComplet","onClick1");
                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();
                if (mTwoPane) {
                    Log.d("onComplet","onClick2");
                    Bundle arguments = new Bundle();
                    arguments.putString(SceneriosDetailFragment.ARG_ITEM_ID, item.id);
                    SceneriosDetailFragment fragment = new SceneriosDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.scenerios_detail_container, fragment)
                            .commit();
                } else {//clicking on item send intent
                    Log.d("onComplet","onClick3");
                    Context context = view.getContext();
                    Intent intent = new Intent(context, SceneriosDetailActivity.class);
                    intent.putExtra(SceneriosDetailFragment.ARG_ITEM_ID, item.id); //sent trow intent the id[number in list] ,and content [name of scenerio]
                    intent.putExtra(SceneriosDetailFragment.ARG_ITEM_CONTENT, item.content);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(SceneriosListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane) {
            Log.d("onComplet","   SimpleItemRecyclerViewAdapter4");
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Log.d("onComplet","onCreateViewHolder5");
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.scenerios_list_content, parent, false);
            return new ViewHolder(view);
        }
        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Log.d("onComplet","onBindViewHolder6");
            String str="";
            DocumentReference mDocRef= FirebaseFirestore.getInstance().document("Scenarios/"+mValues.get(position).content);
            mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mDocRef.collection("accepted").get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int indicator=0;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            if(user.getUid().toString().equals(document.getId().toString())) {
                                                holder.mIdView.setText(mValues.get(position).id);
                                                String str="רשום- "+mValues.get(position).content+"-Range -רשום"+Range((GeoPoint)documentSnapshot.getData().get("מיקום"));
                                                holder.mContentView.setText(str);//add range with the name of the sceneriro
                                                holder.itemView.setTag(mValues.get(position));
                                                holder.itemView.setOnClickListener(mOnClickListener);
                                                break;
                                            }
                                            holder.mIdView.setText(mValues.get(position).id);
                                            holder.mContentView.setText(mValues.get(position).content+"-Range  -");//add range with the name of the sceneriro
                                            holder.itemView.setTag(mValues.get(position));
                                            holder.itemView.setOnClickListener(mOnClickListener);
                                        }
                                    }
                                    else {
                                        Log.d("output", "Error getting documents: ", task.getException());
                                    }
                                }
                            });
                    return;
                }
            });
            FirebaseFirestore.getInstance() .collection("Scenarios").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        int i=0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            holder.mIdView.setText(mValues.get(position).id);
                            holder.mContentView.setText(mValues.get(position).content+"-Range  -"+Range((GeoPoint) document.getData().get("מיקום")));//add range with the name of the sceneriro
                            holder.itemView.setTag(mValues.get(position));
                            holder.itemView.setOnClickListener(mOnClickListener);
                        }
                    } else {
                        Log.d("onComplet","No data");
                    }
                }
            });
        }
        @Override
        public int getItemCount() {
            Log.d("onComplet","getItemCount7");
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            final TextView mIdView;
            final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }
    public  boolean getStateOfGps(){//return true or false if the gps is working
        Log.d("onComplet","getStateOfGps12");
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(constants.gpsState,false);
    }
    public  String getlatOfGps(){//get latitude of gps
        Log.d("onComplet","getlatOfGps13");
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.latOfGps,"");
    }
    public  String getlongOfGps(){//get longtitude of gps
        Log.d("onComplet","getlongOfGps14");
        SharedPreferences sharedPreferences = getSharedPreferences(constants.SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(constants.longOfGps,"");
    }

    public static String Range(GeoPoint gpsLocation) {
        Log.d("onComplet","Range_15");
        String re="";
        if(gpsState){
            double latCurrent=Double.parseDouble(getlatofgps);
            double lonCurrent=Double.parseDouble(getlongofgps);
            double latScenerio=gpsLocation.getLatitude();
            double lonScenerio=gpsLocation.getLongitude();
            double result=Math.pow(Math.pow((111*(latCurrent-latScenerio)),2.0)+Math.pow((111*(lonCurrent-lonScenerio)),2.0),0.5);
            System.out.println(result);
            return String.valueOf(result);

        }

        return re;
    }

}

