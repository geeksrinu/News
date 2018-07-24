package com.ristana.newspro.manager;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by hsn on 18/02/2018.
 */

public class CameraPickerManager extends PickerManager {

    public CameraPickerManager(Activity activity) {
        super(activity);
    }

    protected void sendToExternalApp()
    {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        mProcessingPhotoUri =  getImageFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mProcessingPhotoUri);
        activity.startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
    }
}