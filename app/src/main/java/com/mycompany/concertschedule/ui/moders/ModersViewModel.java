package com.mycompany.concertschedule.ui.moders;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ModersViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ModersViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is moders db fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}