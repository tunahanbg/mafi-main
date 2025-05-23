// Generated by view binder compiler. Do not edit!
package com.mafi.app.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.mafi.app.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentRegisterBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final Button buttonRegister;

  @NonNull
  public final TextInputEditText editTextConfirmPassword;

  @NonNull
  public final TextInputEditText editTextEmail;

  @NonNull
  public final TextInputEditText editTextPassword;

  @NonNull
  public final TextInputEditText editTextUsername;

  @NonNull
  public final ImageView imageLogo;

  @NonNull
  public final TextInputLayout textInputConfirmPassword;

  @NonNull
  public final TextInputLayout textInputEmail;

  @NonNull
  public final TextInputLayout textInputPassword;

  @NonNull
  public final TextInputLayout textInputUsername;

  @NonNull
  public final TextView textLogin;

  @NonNull
  public final TextView textWelcome;

  private FragmentRegisterBinding(@NonNull ConstraintLayout rootView,
      @NonNull Button buttonRegister, @NonNull TextInputEditText editTextConfirmPassword,
      @NonNull TextInputEditText editTextEmail, @NonNull TextInputEditText editTextPassword,
      @NonNull TextInputEditText editTextUsername, @NonNull ImageView imageLogo,
      @NonNull TextInputLayout textInputConfirmPassword, @NonNull TextInputLayout textInputEmail,
      @NonNull TextInputLayout textInputPassword, @NonNull TextInputLayout textInputUsername,
      @NonNull TextView textLogin, @NonNull TextView textWelcome) {
    this.rootView = rootView;
    this.buttonRegister = buttonRegister;
    this.editTextConfirmPassword = editTextConfirmPassword;
    this.editTextEmail = editTextEmail;
    this.editTextPassword = editTextPassword;
    this.editTextUsername = editTextUsername;
    this.imageLogo = imageLogo;
    this.textInputConfirmPassword = textInputConfirmPassword;
    this.textInputEmail = textInputEmail;
    this.textInputPassword = textInputPassword;
    this.textInputUsername = textInputUsername;
    this.textLogin = textLogin;
    this.textWelcome = textWelcome;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentRegisterBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentRegisterBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_register, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentRegisterBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button_register;
      Button buttonRegister = ViewBindings.findChildViewById(rootView, id);
      if (buttonRegister == null) {
        break missingId;
      }

      id = R.id.edit_text_confirm_password;
      TextInputEditText editTextConfirmPassword = ViewBindings.findChildViewById(rootView, id);
      if (editTextConfirmPassword == null) {
        break missingId;
      }

      id = R.id.edit_text_email;
      TextInputEditText editTextEmail = ViewBindings.findChildViewById(rootView, id);
      if (editTextEmail == null) {
        break missingId;
      }

      id = R.id.edit_text_password;
      TextInputEditText editTextPassword = ViewBindings.findChildViewById(rootView, id);
      if (editTextPassword == null) {
        break missingId;
      }

      id = R.id.edit_text_username;
      TextInputEditText editTextUsername = ViewBindings.findChildViewById(rootView, id);
      if (editTextUsername == null) {
        break missingId;
      }

      id = R.id.image_logo;
      ImageView imageLogo = ViewBindings.findChildViewById(rootView, id);
      if (imageLogo == null) {
        break missingId;
      }

      id = R.id.text_input_confirm_password;
      TextInputLayout textInputConfirmPassword = ViewBindings.findChildViewById(rootView, id);
      if (textInputConfirmPassword == null) {
        break missingId;
      }

      id = R.id.text_input_email;
      TextInputLayout textInputEmail = ViewBindings.findChildViewById(rootView, id);
      if (textInputEmail == null) {
        break missingId;
      }

      id = R.id.text_input_password;
      TextInputLayout textInputPassword = ViewBindings.findChildViewById(rootView, id);
      if (textInputPassword == null) {
        break missingId;
      }

      id = R.id.text_input_username;
      TextInputLayout textInputUsername = ViewBindings.findChildViewById(rootView, id);
      if (textInputUsername == null) {
        break missingId;
      }

      id = R.id.text_login;
      TextView textLogin = ViewBindings.findChildViewById(rootView, id);
      if (textLogin == null) {
        break missingId;
      }

      id = R.id.text_welcome;
      TextView textWelcome = ViewBindings.findChildViewById(rootView, id);
      if (textWelcome == null) {
        break missingId;
      }

      return new FragmentRegisterBinding((ConstraintLayout) rootView, buttonRegister,
          editTextConfirmPassword, editTextEmail, editTextPassword, editTextUsername, imageLogo,
          textInputConfirmPassword, textInputEmail, textInputPassword, textInputUsername, textLogin,
          textWelcome);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
