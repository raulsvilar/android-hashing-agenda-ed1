package br.ufs.raulsvilar.agendahashing;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import br.ufs.raulsvilar.agendahashing.hashing.HashingJava;
import br.ufs.raulsvilar.agendahashing.hashing.Record;

public class MainActivity extends AppCompatActivity {

    HashingJava hashingJava;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            hashingJava = new HashingJava(new File(getExternalFilesDir(null),getResources()
                    .getString(R.string.data_file)), 1048576);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void pesquisar(View view) {
        TextInputEditText editText = (TextInputEditText) findViewById(R.id.edit_pesquisa);
        Record record = hashingJava.getValue(editText.getText().toString());
        if (record == null) {
            criarDialog("Contato não encontrado", "O contato não existe ou nome não foi digitado corretamente", false);
        } else {
            criarDialog("Contato", String.format("Nome: %s\nNúmero: %s", record.getName(), record.getNumber()), true);
        }
    }

    public void adicionar(View view) {
        TextInputEditText editTextNome = (TextInputEditText) findViewById(R.id.edit_adicionar_nome);
        TextInputEditText editTextNumero = (TextInputEditText) findViewById(R.id.edit_adicionar_numero);
        if (hashingJava.add(editTextNome.getText().toString(), editTextNumero.getText().toString()))
            Toast.makeText(getApplicationContext(),"Contato adicionado", Toast.LENGTH_SHORT).show();
        else Toast.makeText(getApplicationContext(),"Capacidade máxima atingida", Toast.LENGTH_SHORT).show();
    }

    private void criarDialog(String title, String message, boolean withDelete) {
        final TextInputEditText editText = (TextInputEditText) findViewById(R.id.edit_pesquisa);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",null);
        if (withDelete)
            dialog.setNeutralButton("Deletar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (hashingJava.delete(editText.getText().toString()))
                        Toast.makeText(getApplicationContext(),"Contato deletado", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getApplicationContext(),"Contato não deletado", Toast.LENGTH_SHORT).show();
                }
            });
        dialog.show();
    }
}
