package com.ngra.trafficcontroller.views.fragments.login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.FragmentLoginBinding;
import com.ngra.trafficcontroller.viewmodels.fragment.login.ViewModel_FragmentLogin;
import com.ngra.trafficcontroller.views.dialogs.DialogMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import pl.droidsonroids.gif.GifImageView;

import static com.ngra.trafficcontroller.utility.StaticFunctions.TextChangeForChangeBack;

public class FragmentLogin extends Fragment {

    private Context context;
    private ViewModel_FragmentLogin viewModel;
    private View view;
    private PublishSubject<String> ActivityObservables;
    private String PhoneNumber;

    @BindView(R.id.editPhoneNumber)
    EditText editPhoneNumber;

    @BindView(R.id.editPassword)
    EditText editPassword;

    @BindView(R.id.imgShowPassword)
    ImageView imgShowPassword;

    @BindView(R.id.btnLogin)
    Button btnLogin;

    @BindView(R.id.gifWatting)
    GifImageView gifWatting;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModel_FragmentLogin(context);
        FragmentLoginBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_login, container, false
        );
        binding.setLogin(viewModel);
        view = binding.getRoot();
        ButterKnife.bind(this, view);
        return view;
    }//_____________________________________________________________________________________________ Start onCreateView


    public FragmentLogin(Context context, PublishSubject<String> ActivityObservables) {//___________ Start FragmentLogin
        this.context = context;
        this.ActivityObservables = ActivityObservables;
    }//_____________________________________________________________________________________________ End FragmentLogin


    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        SetClick();
        SetTextWatcher();
        ObserverObservable();
    }//_____________________________________________________________________________________________ End onStart


    private void SetClick() {//_____________________________________________________________________ Start SetClick

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckEmpty()) {
                    ShowProgress();
                    viewModel.SendNumber(PhoneNumber);
                }
            }
        });


    }//_____________________________________________________________________________________________ End SetClick


    private void ObserverObservable() {//___________________________________________________________ Start ObserverObservable
        viewModel
                .Observables
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableObserver<String>() {
                    @Override
                    public void onNext(String s) {
                        getActivity()
                                .runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DismissProgress();
                                        switch (s) {
                                            case "done":
                                                ActivityObservables.onNext(PhoneNumber);
                                                break;
                                            case "failed":
                                                ShowMessage(
                                                        viewModel.getMessageResult(),
                                                        getResources().getColor(R.color.ML_White),
                                                        getResources().getDrawable(R.drawable.ic_warning_red)
                                                );
                                                break;
                                            case "onFailure":
                                                ShowMessage(
                                                        getResources().getString(R.string.onFailure),
                                                        getResources().getColor(R.color.ML_White),
                                                        getResources().getDrawable(R.drawable.ic_warning_red)
                                                );
                                                break;
                                        }
                                    }
                                });
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }//_____________________________________________________________________________________________ End ObserverObservable


    private Boolean CheckEmpty() {//________________________________________________________________ Start CheckEmpty

        boolean phone = false;
        PhoneNumber = editPhoneNumber.getText().toString();


        if (editPhoneNumber.getText().length() != 11) {
            editPhoneNumber.setBackgroundResource(R.drawable.edit_empty_background);
            editPhoneNumber.setError(getResources().getString(R.string.EmptyPhoneNumber));
            editPhoneNumber.requestFocus();
            phone = false;
        } else {
            String ZeroNine = editPhoneNumber.getText().subSequence(0, 2).toString();
            if (ZeroNine.equalsIgnoreCase("09"))
                phone = true;
            else {
                editPhoneNumber.setBackgroundResource(R.drawable.edit_empty_background);
                editPhoneNumber.setError(getResources().getString(R.string.EmptyPhoneNumber));
                editPhoneNumber.requestFocus();
                phone = false;
            }
        }

        if (phone)
            return true;
        else
            return false;

    }//_____________________________________________________________________________________________ End CheckEmpty


    private void ShowProgress() {//_________________________________________________________________ Start ShowProgress
        btnLogin.setText("");
        gifWatting.setVisibility(View.VISIBLE);
    }//_____________________________________________________________________________________________ End ShowProgress



    private void DismissProgress() {//______________________________________________________________ Start DismissProgress
        btnLogin.setText(getResources().getString(R.string.Login));
        gifWatting.setVisibility(View.INVISIBLE);
    }//_____________________________________________________________________________________________ End DismissProgress


    private void SetTextWatcher() {//_______________________________________________________________ Start SetTextWatcher
        editPhoneNumber.addTextChangedListener(TextChangeForChangeBack(editPhoneNumber));
    }//_____________________________________________________________________________________________ End SetTextWatcher


    private void ShowMessage(String message, int color, Drawable icon) {//__________________________ Start ShowMessage

        DialogMessage dialogMessage = new DialogMessage(context, message, color, icon);
        dialogMessage.setCancelable(false);
        dialogMessage.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);

    }//_____________________________________________________________________________________________ End ShowMessage


}
