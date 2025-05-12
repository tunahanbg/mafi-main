package com.mafi.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mafi.app.R;
import com.mafi.app.data.model.Content;
import com.mafi.app.data.repository.ContentRepository;
import com.mafi.app.ui.adapter.ContentAdapter;
import com.mafi.app.ui.viewmodel.ProfileViewModel;

import java.util.ArrayList;

public class ProfileFragment extends Fragment implements ContentAdapter.OnItemClickListener {

    private ProfileViewModel viewModel;
    private ImageView imageViewProfile;
    private TextView textViewUsername;
    private TextView textViewEmail;
    private RecyclerView recyclerViewUserContent;
    private ContentAdapter contentAdapter;
    private Button buttonLogout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // View elemanlarını bağla
        imageViewProfile = view.findViewById(R.id.image_view_profile);
        textViewUsername = view.findViewById(R.id.text_view_username);
        textViewEmail = view.findViewById(R.id.text_view_email);
        recyclerViewUserContent = view.findViewById(R.id.recycler_view_user_content);
        buttonLogout = view.findViewById(R.id.button_logout);

        // RecyclerView'ı ayarla
        recyclerViewUserContent.setLayoutManager(new LinearLayoutManager(getContext()));
        contentAdapter = new ContentAdapter(getContext(), new ArrayList<>());
        contentAdapter.setOnItemClickListener(this);
        recyclerViewUserContent.setAdapter(contentAdapter);

        // ViewModel'i bağla
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Gözlemcileri ayarla
        viewModel.getUsername().observe(getViewLifecycleOwner(), username -> {
            textViewUsername.setText(username);
        });

        viewModel.getEmail().observe(getViewLifecycleOwner(), email -> {
            textViewEmail.setText(email);
        });

        viewModel.getProfilePicture().observe(getViewLifecycleOwner(), profilePicture -> {
            // Gerçek uygulamada burada Glide veya Picasso kullanılarak profil resmi yüklenebilir
            // Örnek:
            // Glide.with(this).load(profilePicture).placeholder(R.drawable.ic_profile).into(imageViewProfile);

            // Şimdilik varsayılan ikon gösterelim
            imageViewProfile.setImageResource(R.drawable.ic_profile);
        });

        viewModel.getUserContentList().observe(getViewLifecycleOwner(), contents -> {
            contentAdapter.updateContentList(contents);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Çıkış butonuna tıklama işlemi
        buttonLogout.setOnClickListener(v -> {
            viewModel.logout();

            // Giriş ekranına yönlendir
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new LoginFragment())
                        .commit();

                // Bottom Navigation'ı gizle
                View bottomNavigation = getActivity().findViewById(R.id.bottom_navigation);
                if (bottomNavigation != null) {
                    bottomNavigation.setVisibility(View.GONE);
                }
            }
        });

        // Kullanıcı içeriklerini yükle
        viewModel.loadUserContents();
    }

    @Override
    public void onItemClick(Content content) {
        // İçerik detayını göster
        Toast.makeText(getContext(), "İçerik: " + content.getTitle(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(Content content, View view) {
        // İçerik işlemleri için popup menü göster
        PopupMenu popup = new PopupMenu(requireContext(), view);
        popup.getMenuInflater().inflate(R.menu.content_item_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_delete) {
                showDeleteConfirmationDialog(content);
                return true;
            } else if (item.getItemId() == R.id.action_edit) {
                // Düzenleme ekranına git
                TextEditorFragment fragment = TextEditorFragment.newInstance(content.getId());
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, fragment)
                            .addToBackStack(null)
                            .commit();
                }
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void showDeleteConfirmationDialog(Content content) {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("İçeriği Sil")
                .setMessage("\"" + content.getTitle() + "\" başlıklı içeriği silmek istediğinizden emin misiniz?")
                .setPositiveButton("Sil", (dialog, which) -> {
                    // İçeriği sil - ProfileViewModel'in deleteContent metodunu çağırıyoruz
                    deleteContent(content.getId());
                })
                .setNegativeButton("İptal", null)
                .show();
    }

    private void deleteContent(int contentId) {
        ContentRepository contentRepository = new ContentRepository(requireContext());
        int result = contentRepository.deleteContent(contentId);

        if (result > 0) {
            Toast.makeText(requireContext(), "İçerik başarıyla silindi", Toast.LENGTH_SHORT).show();
            // İçerik listesini yenile
            viewModel.loadUserContents();
        } else {
            Toast.makeText(requireContext(), "İçerik silinirken bir hata oluştu", Toast.LENGTH_SHORT).show();
        }
    }
}