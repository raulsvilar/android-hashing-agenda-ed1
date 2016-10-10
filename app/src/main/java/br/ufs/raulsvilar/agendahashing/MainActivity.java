package br.ufs.raulsvilar.agendahashing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

import br.ufs.raulsvilar.agendahashing.hashing.HashingJava;
import br.ufs.raulsvilar.agendahashing.hashing.Record;

public class MainActivity extends AppCompatActivity {

    private static final int PICKFILE_REQUEST_CODE = 945;
    HashingJava hashingJava;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(getString(R.string.config_preferences), 0);
        if (preferences.getBoolean(getString(R.string.config_first_open), false)) {
            try {
                hashingJava = new HashingJava(new File(getExternalFilesDir(null),
                        getString(R.string.data_file)), 1048576);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                int inserted_items = preferences.getInt(getString(R.string.config_inserted_items), 0);
                int deleted_items = preferences.getInt(getString(R.string.config_deleted_items), 0);
                int colisions = preferences.getInt(getString(R.string.colisions), 0);
                hashingJava = new HashingJava(new File(getExternalFilesDir(null),
                        getString(R.string.data_file)), 1048576,
                        inserted_items,deleted_items,colisions);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        updateStatistics();
    }

    public void pesquisar(View view) {
        TextInputEditText editText = (TextInputEditText) findViewById(R.id.edit_pesquisa);
        Record record = hashingJava.getValue(editText.getText().toString());
        if (record == null) {
            criarDialog("Contato não encontrado", "O contato não existe ou nome não foi digitado corretamente", false);
        } else {
            criarDialog("Contato", String.format("Nome: %s\nNúmero: %s", record.getName(), record.getNumber()), true);
            editText.getText().clear();
        }
    }

    public void adicionar(View view) {
        TextInputEditText editTextNome = (TextInputEditText) findViewById(R.id.edit_adicionar_nome);
        TextInputEditText editTextNumero = (TextInputEditText) findViewById(R.id.edit_adicionar_numero);
        if (hashingJava.add(editTextNome.getText().toString(), editTextNumero.getText().toString())) {
            Toast.makeText(this, "Contato adicionado", Toast.LENGTH_SHORT).show();
            editTextNome.getText().clear();
            editTextNumero.getText().clear();
        }
        else Toast.makeText(this,"Capacidade máxima atingida", Toast.LENGTH_SHORT).show();
        updateStatistics();
    }

    private void criarDialog(String title, String message, boolean withDelete) {
        final TextInputEditText editText = (TextInputEditText) findViewById(R.id.edit_pesquisa);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK",null);
        if (withDelete)
            dialog.setNeutralButton("Deletar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (hashingJava.delete(editText.getText().toString()))
                        Toast.makeText(dialog.getContext(),"Contato deletado",
                                Toast.LENGTH_SHORT).show();
                    else Toast.makeText(dialog.getContext(),"Contato não deletado",
                            Toast.LENGTH_SHORT).show();
                    updateStatistics();
                }
            });
        dialog.show();
    }

    void updateStatistics() {
        preferences.edit().putInt(getString(R.string.config_colisions), hashingJava.getColisions())
                .putInt(getString(R.string.config_deleted_items), hashingJava.getDeletedItems())
                .putInt(getString(R.string.config_inserted_items), hashingJava.getInsetedItems())
                .apply();

        TextView colisions = (TextView) findViewById(R.id.colisions_textView);
        TextView inserted_items = (TextView) findViewById(R.id.inserted_items_textView);
        TextView deleted_items = (TextView) findViewById(R.id.deleted_items_textView);
        TextView max_items = (TextView) findViewById(R.id.max_items_textView);

        colisions.setText(String.format(Locale.getDefault(),"%s %d", getString(R.string.colisions),
                hashingJava.getColisions()));
        inserted_items.setText(String.format(Locale.getDefault(),"%s %d", getString(R.string.inserted_items),
                hashingJava.getInsetedItems()));
        deleted_items.setText(String.format(Locale.getDefault(),"%s %d", getString(R.string.deleted_items),
                hashingJava.getDeletedItems()));
        max_items.setText(String.format(Locale.getDefault(),"%s %d", getString(R.string.max_contacts),
                hashingJava.getMaxRecordsInFile()));
    }

    public void carregarContatos(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PICKFILE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    final AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                            .setTitle("Aviso")
                            .setMessage("O procedimento irá apagar todos os contatos já adicionados!")
                            .setNegativeButton("Cancelar",null);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int which) {
                            try {
                                hashingJava = new HashingJava(new File(getExternalFilesDir(null),
                                        getString(R.string.data_file_mock)), 118481300);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            new AsyncLoadContact(dialog.getContext(),new File(data.getData().getPath())).execute();
                        }
                    }).show();
                }
                break;
        }
    }

    class AsyncLoadContact extends AsyncTask<Void, Integer, Void> {

        private Context mContext;
        private ProgressDialog progress;
        private File file;

        AsyncLoadContact(Context context, File file) {
            this.mContext = context;
            this.file = file;
        }
        @Override
        protected void onPreExecute() {
    //        progress = ProgressDialog.show(mContext, "Carregando", "A tabela hash está sendo" +
    //                " alimentada por um arquivo de "+file.getTotalSpace()+ " bytes");
     //       progress.setMax(1184713);
            progress = new ProgressDialog(mContext);
            progress.setTitle("Carregando");
            progress.setMessage("A tabela hash está sendo" +
                    " alimentada por um arquivo de "+file.getTotalSpace()+ " bytes");
            progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progress.setMax(1184713);
            progress.setCancelable(false);
            progress.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progress.setProgress(values[0]);
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                boolean arquivoCheio = false;
                int count = 0;
                String line;
                String[] entrada;
                while ((line = br.readLine()) != null && !arquivoCheio) {
                    entrada = line.split(";");
                    arquivoCheio = !hashingJava.add(entrada[0], entrada[1]);
                    count++;
                    publishProgress(count);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            progress.dismiss();
            updateStatistics();
        }
    }
}
