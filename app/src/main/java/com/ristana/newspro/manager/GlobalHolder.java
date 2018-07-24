package com.ristana.newspro.manager;

/**
 * Created by hsn on 18/02/2018.
 */

public class GlobalHolder {

    private PickerManager pickerManager;

    private static GlobalHolder ourInstance = new GlobalHolder();

    public static GlobalHolder getInstance() {
        return ourInstance;
    }

    private GlobalHolder() {
    }


    public PickerManager getPickerManager() {
        return pickerManager;
    }

    public void setPickerManager(PickerManager pickerManager) {
        this.pickerManager = pickerManager;
    }
}