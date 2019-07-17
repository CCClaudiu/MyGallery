package com.claudiu.mygallery;

import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 100;
    private static final String PHOTO_DIRECTORY="imageDir";
    private GridView gridView;
    private ImageGridAdapter gridAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = data.getData();

                String pathfromgallery = getPath(this, imageUri);
                int pos = pathfromgallery.lastIndexOf("/");
                String fileName = pathfromgallery.substring(pos + 1, pathfromgallery.length());
                String fileNameWithoutExt = fileName.substring(0, fileName.lastIndexOf('.'));
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                byte[] binaryFromImage = getBytesFromBitmap(selectedImage);
                if(saveToInternalStorage(binaryFromImage, fileNameWithoutExt)) {
                    File finalFile = new File(pathfromgallery);
                    deleteFileFromMediaStore(this, finalFile);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        gridAdapter = new ImageGridAdapter(this);
        bindData();
        gridView.setAdapter(gridAdapter);
    }
    private void initUI(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent();
                intent1.setType("image/*");
                intent1.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent1, PICK_IMAGE_REQUEST);
            }
        });
        gridView = findViewById(R.id.gridView);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                ImageInfo info = (ImageInfo) gridAdapter.getItem(arg2);
                if (info == null) {
                    return;
                }

                Intent it = new Intent();
                List<ImageInfo> infos = gridAdapter.getData();
                String[] pathStrings = new String[infos.size()];
                for (int i = 0; i < infos.size(); i++) {
                    pathStrings[i] = infos.get(i).getPathString();
                }
                it.putExtra(ImagePreviewActivity.IMAGE_PATH, pathStrings);
                it.putExtra(ImagePreviewActivity.IMAGE_ID, arg2);
                it.setClass(MainActivity.this, ImagePreviewActivity.class);
                startActivity(it);

            }
        });
    }
    public List<ImageInfo> getAllPrivateImages() {
        List<String> allFilesList = new ArrayList<String>();

        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir(PHOTO_DIRECTORY, Context.MODE_PRIVATE);
        getAllFiles(directory.getAbsolutePath(), allFilesList);
        List<ImageInfo> resultsImageInfos = new ArrayList<ImageInfo>();
        for (int i = 0; i < allFilesList.size(); i++) {
            ImageInfo pic = new ImageInfo();
            pic.setPathString(allFilesList.get(i));
            pic.setFileName("");
            int pos = pic.getPathString().lastIndexOf("/");
            pic.setFileName(pic.getPathString().substring(pos + 1, pic.getPathString().length()));
            resultsImageInfos.add(pic);
        }
        return resultsImageInfos;
    }

    private void getAllFiles(String path, List<String> pathList) {

        File root = new File(path);

        File files[] = root.listFiles();
        if (files != null)
            for (File f : files) {
                if (f.isDirectory()) {
                    getAllFiles(f.getPath(), pathList);
                } else {
                    pathList.add(f.getPath());
                }
            }
    }
    private void bindData() {
        List<ImageInfo> images = getAllPrivateImages();
        gridAdapter.setData(images);
        if(images==null || images.size()==0)
        {
            showDialog();
        }
    }
    public static String getPath(Context context, Uri uri) {
        String result = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(proj[0]);
                result = cursor.getString(column_index);
            }
            cursor.close();
        }
        if (result == null) {
            result = "Not found";
        }
        return result;
    }

    public byte[] getBytesFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();
    }

    private boolean saveToInternalStorage(byte[] bitmapImage, String fileName) {
        FileOutputStream fos = null;
        try {
            ContextWrapper cw = new ContextWrapper(getApplicationContext());
            File directory = cw.getDir(PHOTO_DIRECTORY, Context.MODE_PRIVATE);
            File mypath = new File(directory, fileName);

            fos = new FileOutputStream(mypath);
            //bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.write(bitmapImage);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void deleteFileFromMediaStore(Context ctx, final File file) {
        ContentResolver contentResolver = ctx.getContentResolver();
        String canonicalPath;
        try {
            canonicalPath = file.getCanonicalPath();
        } catch (IOException e) {
            canonicalPath = file.getAbsolutePath();
        }
        final Uri uri = MediaStore.Files.getContentUri("external");
        final int result = contentResolver.delete(uri,
                MediaStore.Files.FileColumns.DATA + "=?", new String[]{canonicalPath});
        if (result == 0) {
            final String absolutePath = file.getAbsolutePath();
            if (!absolutePath.equals(canonicalPath)) {
                contentResolver.delete(uri, MediaStore.Files.FileColumns.DATA + "=?", new String[]{absolutePath});
            }
        }
    }
    private void showDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setTitle("Empty gallery");
        dialog.setMessage("No private image found. To add an image, click the button in the lower right corner.\n Would you like to add a picture now?");
        dialog.setCancelable(true);
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent1 = new Intent();
                        intent1.setType("image/*");
                        intent1.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent1, PICK_IMAGE_REQUEST);
                        dialog.cancel();
                    }
                });
        dialog.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert = dialog.create();
        alert.show();
    }
}
