package com.libnanman.liftingplanner;

import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class Utils {

    public static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = { MediaStore.Images.Media.DATA };
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

//    public static Uri convertVideoFileToContentUri(Context context, File file) throws Exception {
//
//        //Uri localImageUri = Uri.fromFile(localImageFile); // Not suitable as it's not a content Uri
//
//        ContentResolver cr = context.getContentResolver();
//        String imagePath = file.getAbsolutePath();
//        String imageName = null;
//        String imageDescription = null;
////        String uriString = MediaStore.Images.Media.insertImage(cr, imagePath, imageName, imageDescription);
////        String uriString = MediaStore.Video.Media.getContentUri(MediaStore.Video.Media.INTERNAL_CONTENT_URI);
//        Uri uri = MediaStore.Video.Media.getContentUri("internal");
//        return Uri.parse(uriString);
//    }
}
