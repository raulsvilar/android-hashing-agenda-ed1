package br.ufs.raulsvilar.agendahashing.hashing;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import br.ufs.raulsvilar.agendahashing.R;

/**
 * Created by raulsvilar on 05/10/16.
 */

public class HashingJava implements BaseHashing{

    private long maxRecordsInFile;
    private RandomAccessFile randomAccessFile;

    /**
     * Controla o acesso, inserção e remoção de registros em um arquivo por meio de uma tabela hash.
     * @param file arquivo onde os registros serão armazenados, caso o arquivo não exista ele será
     *             criado.
     * @param sizeOfFile Caso o arquivo não exista o mesmo será criado com o tamanho aqui fornecido.
     * @throws IOException
     */
    public HashingJava(File file, long sizeOfFile) throws IOException {
        maxRecordsInFile = sizeOfFile/Record.bufferSize;
        randomAccessFile = new RandomAccessFile(file, "rwd");
        randomAccessFile.setLength(sizeOfFile);
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
        boolean canStop = false;
        byte[] data = new byte[Record.bufferSize];
        Record record;
        long index = hash(key);
        do {
            try {
                randomAccessFile.seek(index * Record.bufferSize);
                randomAccessFile.read(data);
                record = new Record(data);
                if (record.getName() == null ||
                        (record.getName() != null && record.isDeleted())) {
                    record = new Record(key, value);
                    randomAccessFile.write(record.getBytes());
                    canStop = true;
                } else index = (index + 1) % maxRecordsInFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (canStop);
    }

    @Override
    public Record getValue(String key) {
        long index = hash(key);
        Record record = null;
        boolean canStop = false;
        byte[] data = new byte[Record.bufferSize];
        do {
            try {
                randomAccessFile.seek(index * Record.bufferSize);
                randomAccessFile.read(data);
                record = new Record(data);
                if (record.getName() != null && record.getName().equals(key)) {
                    canStop = true;
                } else if (record.getName() == null) {
                    record = null;
                    canStop = true;
                } else index = (index + 1) % maxRecordsInFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (canStop);
        return record;
    }

    @Override
    public boolean delete(String key) {
        boolean deleted = false;
        Record record = getValue(key);
        try {
            if (record != null) {
                record.setDeleted(true);
                randomAccessFile.seek(hash(key) * Record.bufferSize);
                randomAccessFile.write(record.getBytes());
                deleted = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deleted;
    }
}
