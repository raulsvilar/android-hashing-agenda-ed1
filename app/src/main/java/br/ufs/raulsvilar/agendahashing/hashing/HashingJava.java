package br.ufs.raulsvilar.agendahashing.hashing;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * Created by raulsvilar on 05/10/16.
 */

public class HashingJava implements BaseHashing{

    private int insetedItems;
    private int deletedItems;
    private int colisions;
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

    public HashingJava(File file, long sizeOfFile, int insertedItems, int deletedItems, int colisions) throws IOException{
        this.deletedItems = deletedItems;
        this.insetedItems = insertedItems;
        this.colisions = colisions;
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
    public boolean add(String key, String value) {
        boolean isAdded = false;
        byte[] data = new byte[Record.bufferSize];
        Record record;
        long index = hash(key);
        long firstIndex = index;
        do {
            try {
                randomAccessFile.seek(index * Record.bufferSize);
                randomAccessFile.read(data);
                record = new Record(data);
                if (TextUtils.isEmpty(record.getName()) ||
                        (!TextUtils.isEmpty(record.getName()) && record.isDeleted())) {
                    if (record.isDeleted()) deletedItems--;
                    record = new Record(key, value);
                    randomAccessFile.seek(randomAccessFile.getFilePointer()-Record.bufferSize);
                    randomAccessFile.write(record.getBytes());
                    isAdded = true;
                    insetedItems++;
                    break;
                } else {
                    index = (index + 1) % maxRecordsInFile;
                    colisions++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (firstIndex != index);
        return isAdded;
    }

    @Override
    public Record getValue(String key) {
        long index = hash(key);
        long firstIndex = index;
        Record record = null;
        byte[] data = new byte[Record.bufferSize];
        do {
            try {
                randomAccessFile.seek(index * Record.bufferSize);
                randomAccessFile.read(data);
                record = new Record(data);
                if (!TextUtils.isEmpty(record.getName()) && record.getName().equals(key) && !record.isDeleted()) {
                    break;
                } else if (TextUtils.isEmpty(record.getName()) || record.isDeleted()) {
                    record = null;
                    break;
                } else index = (index + 1) % maxRecordsInFile;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (firstIndex != index);
        return record;
    }

    @Override
    public boolean delete(String key) {
        boolean deleted = false;
        Record record = getValue(key);
        try {
            if (record != null) {
                record.setDeleted(true);
                randomAccessFile.seek(randomAccessFile.getFilePointer()-Record.bufferSize);
                randomAccessFile.write(record.getBytes());
                deleted = true;
                deletedItems++;
                insetedItems--;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return deleted;
    }

    public int getInsetedItems() {
        return insetedItems;
    }

    public int getDeletedItems() {
        return deletedItems;
    }

    public int getColisions() {
        return colisions;
    }

    public long getMaxRecordsInFile() {
        return maxRecordsInFile;
    }
}
