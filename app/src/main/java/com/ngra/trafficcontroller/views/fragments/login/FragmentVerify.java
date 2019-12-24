package com.ngra.trafficcontroller.views.fragments.login;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.ngra.trafficcontroller.R;
import com.ngra.trafficcontroller.databinding.FragmentVerifyBinding;
import com.ngra.trafficcontroller.viewmodels.fragment.login.ViewModel_FragmentVerify;
import com.ngra.trafficcontroller.views.dialogs.DialogMessage;
import com.ngra.trafficcontroller.views.dialogs.DialogProgress;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

public class FragmentVerify extends Fragment {

    private Context context;
    private ViewModel_FragmentVerify viewModel;
    private View view;
    private DialogProgress progress;
    private boolean ReTryGetSMSClick = false;
    private PublishSubject<String> ActivityObservables;
    private String PhoneNumber;

    @BindView(R.id.VerifyCode1)
    EditText VerifyCode1;

    @BindView(R.id.VerifyCode2)
    EditText VerifyCode2;

    @BindView(R.id.VerifyCode3)
    EditText VerifyCode3;

    @BindView(R.id.VerifyCode4)
    EditText VerifyCode4;

    @BindView(R.id.VerifyCode5)
    EditText VerifyCode5;

    @BindView(R.id.VerifyCode6)
    EditText VerifyCode6;

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.TimeElapsed)
    TextView TimeElapsed;

    @BindView(R.id.message)
    TextView message;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewModel = new ViewModel_FragmentVerify(context);
        FragmentVerifyBinding binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_verify, container, false
        );
        binding.setVerify(viewModel);
        view = binding.getRoot();
        ButterKnife.bind(this, view);
        return view;
    }//_____________________________________________________________________________________________ Start onCreateView


    public FragmentVerify(Context c, PublishSubject<String> o, String P) {//________________________ Start FragmentVerify
        this.context = c;
        this.ActivityObservables = o;
        this.PhoneNumber = P;
    }//_____________________________________________________________________________________________ Start FragmentVerify


    @Override
    public void onStart() {//_______________________________________________________________________ Start onStart
        super.onStart();
        VerifyCode1.requestFocus();
        ObserverObservable();
        SetBackVerifyCode();
        SetTextChangeListener();
        ReTryGetSMS();
        SetClick();
        StartTimer(60);
    }//_____________________________________________________________________________________________ Start onStart


    private void SetClick() {//_____________________________________________________________________ Start SetClick

        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ReTryGetSMSClick){
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
                                            case "verifydone":
                                                ActivityObservables.onNext("finishok");
                                                break;
                                            case "senddone":
                                                StartTimer(60);
                                                break;
                                            case "failed":
                                                VerifyCode1.setText("");
                                                VerifyCode2.setText("");
                                                VerifyCode3.setText("");
                                                VerifyCode4.setText("");
                                                VerifyCode5.setText("");
                                                VerifyCode6.setText("");
                                                VerifyCode1.requestFocus();
                                                SetBackVerifyCode();
                                                ShowMessage(
                                                        viewModel.getMessageResult(),
                                                        getResources().getColor(R.color.ML_White),
                                                        getResources().getDrawable(R.drawable.ic_warning_red)
                                                );
                                                break;
                                            case "onFailure":
                                                VerifyCode1.setText("");
                                                VerifyCode2.setText("");
                                                VerifyCode3.setText("");
                                                VerifyCode4.setText("");
                                                VerifyCode5.setText("");
                                                VerifyCode6.setText("");
                                                VerifyCode1.requestFocus();
                                                SetBackVerifyCode();
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


    private void StartTimer(int Elapse) {//___________________________________________________________________ Start StartTimer

        ReTryGetSMSClick = false;
        TimeElapsed.setVisibility(View.VISIBLE);
        message.setText(getResources().getString(R.string.ElapsedTimeGetSMS));

        Elapse = Elapse * 10;
        progressBar.setMax(Elapse * 2);
        progressBar.setProgress(Elapse);
        TimeElapsed.setVisibility(View.VISIBLE);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progressBar.getProgress() - 1);
                int mili = progressBar.getProgress() + 10;
                int seconds = (int) (mili / 10) % 60;
                int minutes = (int) ((mili / (10 * 60)) % 60);
                TimeElapsed.setText(String.format("%02d", minutes) + " : " + String.format("%02d", seconds));

                if (progressBar.getProgress() > 0)
                    handler.postDelayed(this, 100);
                else
                    ReTryGetSMS();
            }
        }, 100);
    }//_____________________________________________________________________________________________ End StartTimer


    private void SetBackVerifyCode() {//____________________________________________________________ Start SetBackVerifyCode

        Boolean c1 = SetBackVerifyCodeView(VerifyCode1);
        Boolean c2 = SetBackVerifyCodeView(VerifyCode2);
        Boolean c3 = SetBackVerifyCodeView(VerifyCode3);
        Boolean c4 = SetBackVerifyCodeView(VerifyCode4);
        Boolean c5 = SetBackVerifyCodeView(VerifyCode5);
        Boolean c6 = SetBackVerifyCodeView(VerifyCode6);

        if (c1 && c2 && c3 && c4 && c5 && c6) {
            String code = VerifyCode1.getText().toString() +
                    VerifyCode2.getText().toString() +
                    VerifyCode3.getText().toString() +
                    VerifyCode4.getText().toString() +
                    VerifyCode5.getText().toString() +
                    VerifyCode6.getText().toString();

            ShowProgressDialog();
            progressBar.setProgress(0);
            viewModel.VerifyNumber(PhoneNumber, code);

        }

    }//_____________________________________________________________________________________________ End SetBackVerifyCode


    private Boolean SetBackVerifyCodeView(EditText editText) {//____________________________________ Satart SetBackVerifyCodeView

        Boolean ret = false;
        if (editText.getText().length() == 0)
            if (editText.isFocused())
                editText.setBackground(getResources().getDrawable(R.drawable.edit_verify_code_index));
            else
                editText.setBackground(getResources().getDrawable(R.drawable.edit_verify_code_empty));
        else {
            editText.setBackground(getResources().getDrawable(R.drawable.edit_code_verify_full));
            ret = true;
        }
        return ret;

    }//_____________________________________________________________________________________________ End SetBackVerifyCodeView


    private void SetTextChangeListener() {//________________________________________________________ Start SetTextChangeListener

        VerifyCode1.addTextChangedListener(TextChange(VerifyCode2));
        VerifyCode2.addTextChangedListener(TextChange(VerifyCode3));
        VerifyCode3.addTextChangedListener(TextChange(VerifyCode4));
        VerifyCode4.addTextChangedListener(TextChange(VerifyCode5));
        VerifyCode5.addTextChangedListener(TextChange(VerifyCode6));
        VerifyCode6.addTextChangedListener(TextChange(VerifyCode6));

        VerifyCode1.setOnKeyListener(SetKeyBackSpace(VerifyCode1));
        VerifyCode2.setOnKeyListener(SetKeyBackSpace(VerifyCode1));
        VerifyCode3.setOnKeyListener(SetKeyBackSpace(VerifyCode2));
        VerifyCode4.setOnKeyListener(SetKeyBackSpace(VerifyCode3));
        VerifyCode5.setOnKeyListener(SetKeyBackSpace(VerifyCode4));
        VerifyCode6.setOnKeyListener(SetKeyBackSpace(VerifyCode5));


    }//_____________________________________________________________________________________________ End SetTextChangeListener


//    private View.OnFocusChangeListener SetFocuseChange(EditText editText) {//______________________________________________ Satart TextChange
//        return new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus) {
//                    editText.setText("");
//                    SetBackVerifyCode();
//                }
//            }
//        };
//    }//_____________________________________________________________________________________________ End SetTextChangeListener


    private TextWatcher TextChange(EditText eNext) {//______________________________________________ Satart TextChange

        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0)
                    eNext.requestFocus();
                SetBackVerifyCode();
            }
        };

    }//_____________________________________________________________________________________________ End TextChange


    private View.OnKeyListener SetKeyBackSpace(EditText view) {//____________________________________ Start SetKeyBackSpace
        return new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                EditText edit = (EditText) v;
                if (keyCode == 67) {
                    if (event.getAction() != KeyEvent.ACTION_DOWN)
                        return true;
                    if (edit.getText().length() == 0) {
                        view.requestFocus();
                        SetBackVerifyCode();
                        return true;
                    } else
                        return false;
                }
                return false;
            }
        };
    }//_____________________________________________________________________________________________ End SetKeyBackSpace


    private void ReTryGetSMS() {//__________________________________________________________________ Start ReTryGetSMS
        TimeElapsed.setVisibility(View.GONE);
        ReTryGetSMSClick = true;
        message.setText(getResources().getString(R.string.ReTryGetSMS));
    }//_____________________________________________________________________________________________ End ReTryGetSMS


    private void ShowProgressDialog() {//___________________________________________________________ Start ShowProgressDialog
        progress = new DialogProgress(context, null);
        progress.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);
    }//_____________________________________________________________________________________________ End ShowProgressDialog


    private void ShowMessage(String message, int color, Drawable icon) {//__________________________ Start ShowMessage

        DialogMessage dialogMessage = new DialogMessage(context, message, color, icon);
        dialogMessage.setCancelable(false);
        dialogMessage.show(getFragmentManager(), NotificationCompat.CATEGORY_PROGRESS);

    }//_____________________________________________________________________________________________ End ShowMessage



    private void DismissProgress() {//______________________________________________________________ Start DismissProgress
        if(progress!= null)
            progress.dismiss();
    }//_____________________________________________________________________________________________ End DismissProgress


}
