package com.pbt.raadrivers.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.pbt.raadrivers.R;

import java.io.File;
import java.net.URI;

public class PDFViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_view);

        PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
//        URI pdfuri = URI.create("");

        String pdfPath = getIntent().getExtras().getString("file");
        pdfView.fromUri(Uri.fromFile(new File(pdfPath))).load();
    }
}
