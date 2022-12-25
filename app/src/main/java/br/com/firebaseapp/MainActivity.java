package br.com.firebaseapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.UUID;

import br.com.firebaseapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonSelecionarImagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selecionarImagem();
            }
        });

        binding.buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImagem();
            }
        });


    }

    private void uploadImagem() {
        //Definir nome de maneira aleatoria
        String nomeArquivo = gerarNomedoArquivo();

        //Define barra de progresso
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Fazendo upload.....");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference("images/"+nomeArquivo);
        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        binding.imageFoto.setImageURI(null);
                        Toast.makeText(MainActivity.this,
                                "Sucesso ao fazer upload",
                                Toast.LENGTH_LONG).show();

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        binding.imageFoto.setImageURI(null);
                        Toast.makeText(MainActivity.this,
                                "Falaha ao fazer upload "+e.getMessage(),
                                Toast.LENGTH_LONG).show();

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                });

    }

    private String gerarNomedoArquivo() {
        return UUID.randomUUID().toString();
    }

    private void selecionarImagem() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityIfNeeded(intent, 100);
    }

    private Boolean respotaServico(int requestCode, int resultCode, @Nullable Intent data){
        return requestCode == 100 && data != null && data.getData() != null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (respotaServico(requestCode, resultCode, data)){
            imageUri = data.getData();
            binding.imageFoto.setImageURI(imageUri);
        }
    }
}