package br.ufs.raulsvilar.agendahashing.hashing;

import android.content.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;

import br.ufs.raulsvilar.agendahashing.R;

/**
 * Created by raulsvilar on 05/10/16.
 */

public class HashingJava implements BaseHashing{

    private Context mContext;
    private long maxRecordsInFile;
    RandomAccessFile randomAccessFile;

    public HashingJava(Context context) throws FileNotFoundException {
        mContext = context;
        randomAccessFile = new RandomAccessFile(
                new File(mContext.getExternalFilesDir(null),
                        mContext.getResources().getString(R.string.data_file)), "rw");
    }

    @Override
    public long hash(String s) {
        long h = 0;
        for (int i = 0; i < s.length(); i++)
            h = h * (65599) + s.charAt(i);
        return h % maxRecordsInFile;
    }

    @Override
    public void add(String key, String value) {
        //TODO: Adicionar a resolução de colisão
        long index = hash(key);
        try {
            randomAccessFile.seek(index * Record.bufferSize);
            Record record = new Record(key, value);
            randomAccessFile.write(record.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Record getValue(String key) {
        long index = hash(key);
        Record record;
        boolean canStop = false;
        byte[] data = new byte[Record.bufferSize];
        do {
            try {
                randomAccessFile.seek(index * Record.bufferSize);
                randomAccessFile.read(data);
                record = new Record(data);
                if (record.getName() == null) {
                    canStop = true;
                } else if (record.getName().equals(key)) {
                    canStop = true;
                    //TODO: Implementar o endereçamento aberto linear
                } else {

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (canStop);
        return null;
    }

    @Override
    public void delete(String key) {

    }

    /**
     * Define a quantidade de registros possíveis de serem armazenados baseado no tamanho do arquivo
     * @param size Tamanho do arquivo em bytes
     */
    public void setSizeOfFile(long size) {
        this.maxRecordsInFile = size / Record.bufferSize;
    }
}
