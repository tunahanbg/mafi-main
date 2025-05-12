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
import com.mafi.app.ui.viewmodel.LoginViewModel;

public class LoginFragment extends Fragment {

    private LoginViewModel viewModel;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View elemanlarını bağla
        editTextEmail = view.findViewById(R.id.edit_text_email);
        editTextPassword = view.findViewById(R.id.edit_text_password);
        buttonLogin = view.findViewById(R.id.button_login);
        textViewRegister = view.findViewById(R.id.text_register);

        // ViewModel'i bağla
        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        // Gözlemcileri ayarla
        viewModel.getLoginSuccess().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                // Giriş başarılı, ana sayfaya geç
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new HomeFragment())
                            .commit();

                    // Bottom Navigation'ı göster
                    View bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
                    if (bottomNavigation != null) {
                        bottomNavigation.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Login butonuna tıklama işlemi
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            viewModel.login(email, password);
        });

        //- Kayıt ol butonuna tıklama işlemi
        textViewRegister.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new RegisterFragment())
                        .commit();
            }
        });
    }
}