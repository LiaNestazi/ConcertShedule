package com.mycompany.concertschedule.ui.change_pass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChangePassViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public ChangePassViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is change password fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}