package com.yashika.applock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * This fragment is used to set Pin for AppLock
 *
 * @author yashika.
 */
public class SetPinFragment extends AppLockFragment {

    private static final int PIN_LENGTH = 6;
    @BindView(R.id.status_subtitle_text_view)
    TextView statusSubtitleTextView;
    @BindView(R.id.pin_edit_text)
    EditText pinEditText;
    private StringBuilder inputPin;
    private String oldPin;
    private boolean pinReEnterCase = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inputPin = new StringBuilder();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_pin, container, false);
        ButterKnife.bind(this, view);
        statusSubtitleTextView.setText(getResources().getString(R.string.enter_pin));
        pinEditText.addTextChangedListener(new PinTextWatcher());
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(getAppLockActivity() != null && getAppLockActivity().getSupportActionBar() != null) {
            getAppLockActivity().getSupportActionBar().hide();
        }
    }

    @OnClick({ R.id.delete_image_view, R.id.one_button, R.id.two_button,
            R.id.three_button, R.id.four_button, R.id.five_button, R.id.six_button,
            R.id.seven_button, R.id.eight_button, R.id.nine_button, R.id.zero_button })
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.delete_image_view:
                deletePin();
                break;
            case R.id.one_button:
                appendText(getString(R.string.one_text));
                break;
            case R.id.two_button:
                appendText(getString(R.string.two_text));
                break;
            case R.id.three_button:
                appendText(getString(R.string.three_text));
                break;
            case R.id.four_button:
                appendText(getString(R.string.four_text));
                break;
            case R.id.five_button:
                appendText(getString(R.string.five_text));
                break;
            case R.id.six_button:
                appendText(getString(R.string.six_text));
                break;
            case R.id.seven_button:
                appendText(getString(R.string.seven_text));
                break;
            case R.id.eight_button:
                appendText(getString(R.string.eight_text));
                break;
            case R.id.nine_button:
                appendText(getString(R.string.nine_text));
                break;
            case R.id.zero_button:
                appendText(getString(R.string.zero_text));
                break;
        }
    }

    private void deletePin() {
        if(!TextUtils.isEmpty(inputPin)) {
            inputPin.deleteCharAt(inputPin.length() - 1);
            String pinEditTextString = pinEditText.getText().toString();
            if(!TextUtils.isEmpty(pinEditTextString)) {
                pinEditTextString = pinEditTextString.substring(0, pinEditTextString.length() - 1);
                pinEditText.setText(pinEditTextString);
            }
        }
        pinEditText.setSelection(pinEditText.getText().length());
    }

    private void appendText(String input) {
        if(inputPin.length() < PIN_LENGTH) {
            inputPin.append(input);
            pinEditText.setText(inputPin);
            pinEditText.setSelection(pinEditText.getText().length());
        }
    }

    private void validatePin() {
        if(!TextUtils.isEmpty(oldPin)) {
            //this is the case of pin creation.
            //match the 2 pins and do the needful.
            if(oldPin.equals(inputPin.toString())) {
                SessionPreferences.INSTANCE.setPin(pinEditText.getText().toString());
                // TODO: 06-02-2017 switch to next fragment
            } else {
                statusSubtitleTextView.setText(getResources().getString(R.string.pin_mismatch));
                pinEditText.setText("");
                inputPin.delete(0, inputPin.length());
                oldPin = "";
                pinReEnterCase = true;
            }
        }
    }

    /**
     * This method is used to re-enter pin by user.
     */
    private void reEnterPin() {
        pinReEnterCase = false;
        statusSubtitleTextView.setText(getResources().getString(R.string.reenter_pin));
        pinEditText.setText("");
        inputPin.delete(0, inputPin.length());
    }

    public class PinTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence s, int i, int i1, int i2) {
            if(s.length() == PIN_LENGTH) {
                //re-enter pin.
                if(pinReEnterCase) {
                    oldPin = inputPin.toString();
                    reEnterPin();
                } else {
                    validatePin();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}
