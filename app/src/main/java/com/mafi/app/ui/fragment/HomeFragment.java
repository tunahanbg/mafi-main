package com.mafi.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mafi.app.R;
import com.mafi.app.data.model.Content;
import com.mafi.app.ui.adapter.ContentAdapter;
import com.mafi.app.ui.viewmodel.HomeViewModel;

import java.util.ArrayList;

public class HomeFragment extends Fragment implements ContentAdapter.OnItemClickListener {
    private static final String TAG = "HomeFragment";

    private HomeViewModel viewModel;
    private RecyclerView recyclerViewContent;
    private ContentAdapter contentAdapter;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddContent;
    private TextView emptyTextView; // Boş durum için TextView

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated başladı");

        // View elemanlarını bağla
        recyclerViewContent = view.findViewById(R.id.recycler_view_content);
        progressBar = view.findViewById(R.id.progress_bar);
        fabAddContent = view.findViewById(R.id.fab_add_content);


        emptyTextView = view.findViewById(R.id.text_empty);

        // RecyclerView'ı ayarla
        recyclerViewContent.setLayoutManager(new LinearLayoutManager(getContext()));
        contentAdapter = new ContentAdapter(getContext(), new ArrayList<>());
        contentAdapter.setOnItemClickListener(this);
        recyclerViewContent.setAdapter(contentAdapter);

        // ViewModel'i bağla
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Gözlemcileri ayarla
        viewModel.getContentList().observe(getViewLifecycleOwner(), contents -> {
            Log.d(TAG, "ContentList değişti: " + (contents != null ? contents.size() : 0) + " adet içerik");

            if (contents == null) {
                contents = new ArrayList<>();
            }

            contentAdapter.updateContentList(contents);

            // Boş durum kontrolü - yalnızca içerik yoksa mesaj göster
            if (contents.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
            } else {
                emptyTextView.setVisibility(View.GONE);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Log.e(TAG, "Hata: " + errorMessage);
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // İçerik ekleme butonu işlemi
        fabAddContent.setOnClickListener(v -> {
            Log.d(TAG, "İçerik ekle butonuna tıklandı");
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new ContentManagerFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        // İçerikleri yükle - artık bu, kullanıcının kendi içeriklerini getirir
        Log.d(TAG, "İçerikler yükleniyor...");
        viewModel.loadUserContents();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Sayfaya geri dönüldüğünde içerikleri yenile
        Log.d(TAG, "onResume - İçerikler yenileniyor");
        viewModel.loadUserContents();
    }

    @Override
    public void onItemClick(Content content) {
        Log.d(TAG, "İçeriğe tıklandı: " + content.getTitle() + ", ID: " + content.getId());

        // TextEditorFragment'a geçiş
        if (getActivity() != null) {
            TextEditorFragment fragment = TextEditorFragment.newInstance(content.getId());
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onItemLongClick(Content content, View view) {
        // İçerik işlemleri (silme, düzenleme vb.)
        Toast.makeText(getContext(), "İçerik İşlemleri: " + content.getTitle(), Toast.LENGTH_SHORT).show();
    }
}