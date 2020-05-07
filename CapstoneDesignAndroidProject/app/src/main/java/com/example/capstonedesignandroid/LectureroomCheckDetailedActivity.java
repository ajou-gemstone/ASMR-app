package com.example.capstonedesignandroid;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.example.capstonedesignandroid.DTO.DummyLectureRoomReservationState;
import com.example.capstonedesignandroid.DTO.DummyReservationDetail;
import com.example.capstonedesignandroid.DTO.DummyResponse;
import com.example.capstonedesignandroid.StaticMethodAndOthers.DefinedMethod;
import com.example.capstonedesignandroid.StaticMethodAndOthers.MyConstants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//사진은 내부 저장소
public class LectureroomCheckDetailedActivity extends AppCompatActivity {

    private static final int CAPTURE_IMAGE = 99;
    private static final int REQUEST_IMAGE_CAPTURE = 11111;
    private Button takePictureButton1;
    private ImageView pictureImageView1;
    private Bitmap bitmap;
    private Button transportPictureButton1;
    private Bitmap img = null;
    private String imageFilePath1;
    private Uri photoUri1;
    private Uri photoUri2;
    private FirebaseStorage storage;
    private StorageReference storageRef;
    private String resId;
    private Retrofit retrofit;
    private boolean IOexception = true;
    private DummyReservationDetail dummy;
    private Button takePictureButton2;
    private ImageView pictureImageView2;
    private Button transportPictureButton2;
    private boolean before = false;
    private boolean after = false;
    private int beforeOrAfter = -1;
    private String imageFilePath2;
    private TextView date;
    private TextView day;
    private TextView lectureroom;
    private TextView startTime;
    private TextView lastTime;
    private boolean alreadyBefore = false;
    private boolean alreadyAfter = false;
    private TextView reservationIntent;
    private TextView beforeUploadTime;
    private TextView afterUploadTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lectureroom_checkdetailed);

        //------------------퍼미션 코드----------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //퍼미션 상태 확인
            if (!hasPermissions(PERMISSIONS)) {
                //퍼미션 허가 안되어있다면 사용자에게 요청
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            } else {

            }
        }
        //------------------퍼미션 코드----------------

        Intent intent = getIntent();
        resId = intent.getExtras().getString("reservationId");

        //----------------서버에서 받기 코드-------------------
        retrofit = new Retrofit.Builder()
                .baseUrl(MyConstants.BASE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GetService service = retrofit.create(GetService.class);
        Call<DummyReservationDetail> call = service.getReservationDetail(resId);

//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    dummy = call.execute().body();
//                    IOexception = false;
//                    Log.d("run: ", "run: ");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    IOexception = true;
//                    Log.d("IOException: ", "IOException: ");
//                }
//            }
//        });
//        thread.start();
//        try {
//            thread.join();
//        } catch (Exception e) {
//            // TODO: handle exception
//        }

        //----------------서버에서 받기 코드-------------------

//        mockup data로 대체
        if(IOexception){
            String[] userids = {"user1", "user2", "user3"};
            dummy = new DummyReservationDetail("2020-05-01", "월", "8:00", "10:00", "성101",
                    userids, "/images/TEST_20200412_190805_1263752076698054948.png",
                    "/images/TEST_20200427_203734_8578526890362646423.png", "studying algorithm",
                    "2020-05-01 08:05", "2020-05-01 10:23");
        }

        Log.d("getFilesDir", "" + getFilesDir());
        Log.d("getPackageName", "" + getPackageName());
        Log.d("getExternalFilesDir", "" + getExternalFilesDir(Environment.DIRECTORY_PICTURES));

        //모임원 정보 보기 리사이클러뷰는 스터디 액티비피 부분에서 재사용 한다.

        //Todo: 오늘의 예약이나 앞으로의 예약인 경우, 예약을 취소, 삭제하는 기능 넣기

        //------------초기 설정----------------
        takePictureButton1 = findViewById(R.id.takePictureButton1);
        pictureImageView1 = findViewById(R.id.pictureImageView1);
        transportPictureButton1 = findViewById(R.id.transportPictureButton1);

        takePictureButton2 = findViewById(R.id.takePictureButton2);
        pictureImageView2 = findViewById(R.id.pictureImageView2);
        transportPictureButton2 = findViewById(R.id.transportPictureButton2);

//        date = findViewById(R.id.date);
        day = findViewById(R.id.day);
        lectureroom = findViewById(R.id.lectureroom);
        startTime = findViewById(R.id.startTime);
        lastTime = findViewById(R.id.lastTime);
        reservationIntent = findViewById(R.id.reservationIntent);
        beforeUploadTime = findViewById(R.id.beforeUploadTime);
        afterUploadTime = findViewById(R.id.afterUploadTime);

//        date.setText(""+dummy.getDate());
        day.setText(""+dummy.getDay());
        lectureroom.setText(""+dummy.getLectureRoom());
        startTime.setText(""+dummy.getStartTime());
        lastTime.setText(""+dummy.getLastTime());
        reservationIntent.setText(""+dummy.getReservationIntent());
        beforeUploadTime.setText("업로드 시간: "+dummy.getBeforeUploadTime());
        afterUploadTime.setText("업로드 시간: "+dummy.getAfterUploadTime());

        //app에 등록된 firebase storage의 instance를 가져온다. (싱글톤)
        storage = FirebaseStorage.getInstance("gs://asmr-799cf.appspot.com");

        //스토리지의 레퍼런스(주소)를 가져온다.
        storageRef = storage.getReference();

        if(!dummy.getBeforeuri().equals("")){
            //downloadUrl을 이용하여 이미지를 다운로드한다.
            StorageReference storageReference = storage.getReference(""+dummy.getBeforeuri());
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with(getApplicationContext())
                                .load(task.getResult())
                                .into(pictureImageView1);
                        alreadyBefore = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        if(!dummy.getAfteruri().equals("")){
            //downloadUrl을 이용하여 이미지를 다운로드한다.
            StorageReference storageReference = storage.getReference(""+dummy.getAfteruri());
            storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        // Glide 이용하여 이미지뷰에 로딩
                        Glide.with(getApplicationContext())
                                .load(task.getResult())
                                .into(pictureImageView2);
                        alreadyAfter = true;
                    } else {
                        Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        //------------초기 설정----------------
        //-----------------------------------------------------------------------

        takePictureButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alreadyBefore){
                    Toast.makeText(getApplicationContext(),"이미 이미지를 업로드 했습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                beforeOrAfter = 1;
                sendTakePhotoIntent();
            }
        });

        takePictureButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alreadyAfter){
                    Toast.makeText(getApplicationContext(),"이미 이미지를 업로드 했습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                beforeOrAfter = 2;
                sendTakePhotoIntent();
            }
        });

        transportPictureButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alreadyBefore){
                    Toast.makeText(getApplicationContext(),"이미 이미지를 업로드 했습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!before){
                    Toast.makeText(getApplicationContext(), "사진을 찍어주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                //photoUri는 content provider 경로이므로 file 경로로 재설정 해주어야 한다.
                //content provider경로에서 file 경로로 재설정 하는 것은 굉장히 어렵기 때문에 이전에 구했던
                //file경로인 externalFile경로를 이용하여 uri를 설정한다.
                //실제 file path도
                final Uri fileuri = Uri.fromFile(new File(imageFilePath1));//내장 데이터(앨범)의 uri를 가져옴
                //파이어 베이스에 이미지를 업로드 하면서 node서버에 어떤 이미지가 어떤 사용자의 것인지도 추가를 해야 한다.
                String firebasefileuri = "images/" + fileuri.getLastPathSegment();
                StorageReference riversRef = storageRef.child(firebasefileuri);//저장소에 파일명으로 저장함
                UploadTask uploadTask = riversRef.putFile(fileuri);//저장소에 파일을 넣음 + task로 처리하도록 함.(반환값이 task)

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//firebase에서 자주 쓰이는 callback listener
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                        Toast.makeText(getApplicationContext(), "성공적으로 이미지 업로드가 되었습니다.", Toast.LENGTH_LONG).show();

                        //db에 파일 이름 저장
                        GetService service = retrofit.create(GetService.class);
                        Call<DummyResponse> call = service.postBeforePicture(resId, "/"+firebasefileuri, DefinedMethod.getCurrentDate2());
                        call.enqueue(response);
                    }
                });
            }
        });

        transportPictureButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(alreadyAfter){
                    Toast.makeText(getApplicationContext(),"이미 이미지를 업로드 했습니다.", Toast.LENGTH_LONG).show();
                    return;
                }
                if(!after){
                    Toast.makeText(getApplicationContext(), "사진을 찍어주세요.", Toast.LENGTH_LONG).show();
                    return;
                }
                //photoUri는 content provider 경로이므로 file 경로로 재설정 해주어야 한다.
                //content provider경로에서 file 경로로 재설정 하는 것은 굉장히 어렵기 때문에 이전에 구했던
                //file경로인 externalFile경로를 이용하여 uri를 설정한다.
                //실제 file path도
                final Uri fileuri = Uri.fromFile(new File(imageFilePath2));//내장 데이터(앨범)의 uri를 가져옴
                //파이어 베이스에 이미지를 업로드 하면서 node서버에 어떤 이미지가 어떤 사용자의 것인지도 추강를 해야 한다.
                String firebasefileuri = "images/" + fileuri.getLastPathSegment();
                StorageReference riversRef = storageRef.child(firebasefileuri);//저장소에 파일명으로 저장함
                UploadTask uploadTask = riversRef.putFile(fileuri);//저장소에 파일을 넣음 + task로 처리하도록 함.(반환값이 task)

                // Register observers to listen for when the download is done or if it fails
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Toast.makeText(getApplicationContext(), "이미지 업로드 실패", Toast.LENGTH_LONG).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {//firebase에서 자주 쓰이는 callback listener
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        Toast.makeText(getApplicationContext(), "성공적으로 이미지 업로드가 되었습니다,", Toast.LENGTH_LONG).show();
                        //db에 파일 이름 저장
                        GetService service = retrofit.create(GetService.class);
                        Call<DummyResponse> call = service.postAfterPicture(resId, "/"+firebasefileuri, DefinedMethod.getCurrentDate2());
                        call.enqueue(response);
                    }
                });
            }
        });
    }

    //사진을 찍는 인텐트를 실행하고, 인텐트 환경 설정을 한다.
    private void sendTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {

                if(beforeOrAfter == 1){
                    photoUri1 = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    Log.d("photoUri", "" + photoUri1);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri1);
                }else if(beforeOrAfter == 2){
                    photoUri2 = FileProvider.getUriForFile(this, getPackageName(), photoFile);
                    Log.d("photoUri", "" + photoUri2);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri2);
                }

                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";

        //외부 저장 경로를 사용하지만 sdcard가 없어도 emulated가 되어서 가상의(심볼릭) 경로이고
        //나중에 provider를 통하여 사전에 지정한(files_paths.xml에 있음) 실제 기기에서 저장되는 경로에 파일을 지정해주어서
        //camera intent에서 좋은 화질의 사진을 실제 디렉토리에 저장 할 수 있도록 한다.
        //camera intent로 takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)을 추가하여 실제로 저장할 수 있도록 한다.
        //camera intent에서 카메라는 file path를 사용하지 않고 content provider path를 쓴다는 것을 유념하자.
        //(왜 그럴까?) 접근할 때는 어차피 external file path를 이용하기 때문에 나머지는 file path를 이용하여 처리하면 된다.

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //내부 저장 경로를 사용할 경우 화질의 문제가 생기기 때문에 외부 저장 경로를 활용한다.
        //또한 provider를 활용하지 못한다. (내부 저장 경로는 앱에 종속되기 때문에)
//        File storageDir = getFilesDir();
        File image = File.createTempFile(
                imageFileName,      /* prefix */
                ".png",         /* suffix */
                storageDir          /* directory */
        );

        if(beforeOrAfter == 1){
            imageFilePath1 = image.getAbsolutePath();
            Log.d("imageFilePath", "" + imageFilePath1);
        }else if(beforeOrAfter == 2){
            imageFilePath2 = image.getAbsolutePath();
            Log.d("imageFilePath", "" + imageFilePath2);
        }

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if(beforeOrAfter == 1){
                pictureImageView1.setImageURI(photoUri1);
                before = true;
            }else if(beforeOrAfter == 2){
                pictureImageView2.setImageURI(photoUri2);
                after = true;
            }
        }
    }

    Callback response = new Callback<DummyResponse>() {
        @Override
        public void onResponse(Call<DummyResponse> call, Response<DummyResponse> response) {
            if (response.isSuccessful()) {
                DummyResponse dummy = response.body();
            } else {
                Log.d("onResponse:", "Fail, " + String.valueOf(response.code()));
            }
        }

        @Override
        public void onFailure(Call<DummyResponse> call, Throwable t) {
            Log.d("response fail", "onFailure: ");

        }
    };

    //****-------------------------------------------------------------------------------------------------------------
    //****-------------------------------------------------------------------------------------------------------------
    //****-------------------------------------------------------------------------------------------------------------
    // 여기서부터는 퍼미션 관련 코드
    static final int PERMISSIONS_REQUEST_CODE = 1000;
    String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean hasPermissions(String[] permissions) {
        int result;

        //스트링 배열에 있는 퍼미션들의 허가 상태 여부 확인
        for (String perms : permissions) {
            result = ContextCompat.checkSelfPermission(this, perms);
            if (result == PackageManager.PERMISSION_DENIED) {
                //허가 안된 퍼미션 발견
                return false;
            }
        }
        //모든 퍼미션이 허가되었음
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermissionAccepted = grantResults[0]
                            == PackageManager.PERMISSION_GRANTED;
                    boolean diskPermissionAccepted = grantResults[1]
                            == PackageManager.PERMISSION_GRANTED;
                    if (!cameraPermissionAccepted || !diskPermissionAccepted)
                        showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
                    else {
                    }
                }
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                requestPermissions(PERMISSIONS, PERMISSIONS_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                finish();
            }
        });
        builder.create().show();
    }

}