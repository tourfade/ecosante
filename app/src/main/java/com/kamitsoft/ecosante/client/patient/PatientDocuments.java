package com.kamitsoft.ecosante.client.patient;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kamitsoft.ecosante.BuildConfig;
import com.kamitsoft.ecosante.DiskCache;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.PatientBaseFragment;
import com.kamitsoft.ecosante.client.adapters.DocumentsListAdapter;
import com.kamitsoft.ecosante.client.patient.dialogs.DocEditorDialog;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.PatientInfo;
import com.kamitsoft.ecosante.model.UserInfo;
import com.kamitsoft.ecosante.model.viewmodels.DocumentsViewModel;

import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PatientDocuments extends PatientBaseFragment {
    private RecyclerView recyclerview;
    private DocumentsListAdapter docAdapter;
    private FloatingActionButton newDoc;
    private DiskCache cache;
    private FloatingActionButton newPdf;
    private FloatingActionButton newImg;
    private boolean isFABOpen;
    private DocumentsViewModel model;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.list_with_actions, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        model = ViewModelProviders.of(this).get(DocumentsViewModel.class);
        cache = new DiskCache(getContext());
        recyclerview =  view.findViewById(R.id.recycler_view);
        recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        docAdapter = new DocumentsListAdapter(getActivity());
        recyclerview.setAdapter(docAdapter);
        swr = view.findViewById(R.id.swiperefresh);
        swr.setOnRefreshListener(this::requestSync);
        model.getDocuments().observe(this, documentInfos -> {
            PatientInfo p = app.getCurrentPatient();
            if(p != null) {
                docAdapter.syncData(documentInfos.parallelStream()
                        .filter(d -> app.getCurrentPatient()!=null &&
                                !d.getDeleted() &&
                                d.getPatientUuid().equals(p.getUuid()))
                        .collect(Collectors.toList()));
            }

        });
        docAdapter.setItemClickListener(this::handleDocument);
        newDoc = view.findViewById(R.id.newItem);
        newPdf =  view.findViewById(R.id.newFiles);
        newImg = view.findViewById(R.id.newPicture);

        newDoc.setOnClickListener(view1 -> {
            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        });
        newPdf.setOnClickListener((View v)-> {
            app.newDocument();
            new DocEditorDialog(1, app.getCurrentDocument(), docAdapter).show(getFragmentManager(),"docdialog");

        });

        newImg.setOnClickListener((View v)-> {
            app.newDocument();
            new DocEditorDialog(0, app.getCurrentDocument(), docAdapter).show(getFragmentManager(),"docdialog");

        });

    }

    @Override
    protected Class<?> getEntity() {
        return DocumentInfo.class;
    }

    @Override
    public void onResume() {
        super.onResume();
        app.exitDocument();
    }

    private void showFABMenu(){
        isFABOpen=true;
        newImg.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        newPdf.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        newImg.animate().translationY(0);
        newPdf.animate().translationY(0);
    }


    public void handleDocument(int itemPosition, View v){
        DocumentInfo item = docAdapter.getItem(itemPosition);

        if(v.getId() == R.id.item_delete){
            item.setDeleted(true);
            model.update(item);
            return;
        }
        if(v.getId() == R.id.item_edit){
            app.setCurrentDoc(item);
            new DocEditorDialog(1, app.getCurrentDocument(), docAdapter).show(getFragmentManager(),"docdialog");
            return;
        }
        app.setCurrentDoc(item);


        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(Utils.getUri(getContext(), BuildConfig.DOCUMENT_BUCKET,item.getAttachment()), item.getMimeType());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try {
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.enter_from_right,R.anim.exit_to_left);
        }catch (Exception e){
            Toast.makeText(contextActivity,"Aucun programme installé ne peut ouvrir le fichier", Toast.LENGTH_LONG).show();
        }

    }


}
