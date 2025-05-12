package com.mafi.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.mafi.app.R;
import com.mafi.app.ui.viewmodel.RegisterViewModel;

public class RegisterFragment extends Fragment {

    private RegisterViewModel viewModel;
    private EditText editTextUsername;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private Button buttonRegister;
    private TextView textLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View elemanlarını bağla
        editTextUsername = view.findViewById(R.id.edit_text_username);
        editTextEmail = view.findViewById(R.id.edit_text_email);
        editTextPassword = view.findViewById(R.id.edit_text_password);
        editTextConfirmPassword = view.findViewById(R.id.edit_text_confirm_password);
        buttonRegister = view.findViewById(R.id.button_register);
        textLogin = view.findViewById(R.id.text_login);

        // ViewModel'i bağla
        viewModel = new ViewModelProvider(this).get(RegisterViewModel.class);

        // Gözlemcileri ayarla
        viewModel.getRegisterSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Kayıt başarılı! Giriş yapabilirsiniz.", Toast.LENGTH_SHORT).show();

                // Giriş sayfasına dön
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new LoginFragment())
                            .commit();
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Kayıt butonuna tıklama işlemi
        buttonRegister.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString();
            String confirmPassword = editTextConfirmPassword.getText().toString();

            viewModel.register(username, email, password, confirmPassword);
        });

        // Giriş sayfasına geçiş
        textLogin.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();
            }
        });
    }
}