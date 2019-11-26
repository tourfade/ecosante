package com.kamitsoft.ecosante.client.patient.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.kamitsoft.ecosante.EcoSanteApp;
import com.kamitsoft.ecosante.ImagePickerActivity;
import com.kamitsoft.ecosante.R;
import com.kamitsoft.ecosante.Utils;
import com.kamitsoft.ecosante.client.adapters.DocumentsListAdapter;
import com.kamitsoft.ecosante.model.DocumentInfo;
import com.kamitsoft.ecosante.model.viewmodels.DocumentsViewModel;

import java.sql.Timestamp;
import java.util.Calendar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProviders;

public class DocEditorDialog extends DialogFragment {
    private final int filchooser;
    private  DocumentsListAdapter docsAdapter;
    private  DocumentInfo doc;
    private  EditText title, date;
    private  ImageView docPreview;
    private  EcoSanteApp app;
    private  final Calendar calendar = Calendar.getInstance();
    private  String attachment;
    ImagePickerActivity picker;
    private DocumentsViewModel docsModel;

    public DocEditorDialog(int filchooser, DocumentInfo doc, DocumentsListAdapter adapter){
        this.filchooser= filchooser;
        this.doc = doc;
        this.docsAdapter = adapter;

    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        app = (EcoSanteApp) getActivity().getApplication();
        if(getActivity() instanceof ImagePickerActivity) {
            picker = (ImagePickerActivity) getActivity();
        }
        docsModel = ViewModelProviders.of(this).get(DocumentsViewModel.class);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(R.string.newDoc);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(R.layout.dialog_doc_view);
        alertDialogBuilder.setIcon(R.drawable.docs);
        alertDialogBuilder.setPositiveButton("Ajouter",(dialog, which)->{
            doc.setDocName(title.getText().toString());
            doc.setAttachment(attachment);
            doc.setDate(new Timestamp(calendar.getTimeInMillis()));
            doc.setMimeType(picker.getMimeType());
            docsModel.insert(doc);
        });
        alertDialogBuilder  .setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setOnShowListener(dialog -> {
            initVars((AlertDialog)dialog);
            initValues();
            initListeners();
        });
        alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return alertDialog;
    }

    private void initValues() {
        title.setText(Utils.niceFormat(doc.getDocName()));
        if(doc.getMimeType() != null) {
            docPreview.setImageResource(Utils.getPicture(doc.getMimeType()));
        }
        calendar.setTimeInMillis(doc.getDate()!=null?doc.getDate().getTime(): System.currentTimeMillis());
        date.setText(Utils.format(calendar.getTime()));
    }

    void initVars(AlertDialog d) {

        title = d.findViewById(R.id.docname);
        docPreview = d.findViewById(R.id.docImg);
        date = d.findViewById(R.id.docdate);

    }
    void initListeners() {


        picker.setSquare(true);
        picker.setPlaceholder(R.drawable.file);
        picker.setSelectionFinishedListener((avatar)-> attachment = avatar);

        docPreview.setOnClickListener(filchooser == 1?picker::pickFile:picker::pick);


        date.setOnClickListener(v->{

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (vie, year, month, dayOfMonth) -> {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                ((EditText)v).setText(Utils.niceFormat(Utils.format(calendar.getTime())));

            },  calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH) );

            datePickerDialog.show();
        });


    }



}
