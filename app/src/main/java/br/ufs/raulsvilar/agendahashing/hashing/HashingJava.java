package br.ufs.raulsvilar.agendahashing.hashing;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Created by raulsvilar on 05/10/16.
 */

public class HashingJava implements BaseHashing{

    List<List<Byte>> hashTable;

    long actualSize;
    int tableSize;

    @Override
    public String hash(String s) {
        try {
            MessageDigest.getInstance("MD5").digest(s.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void add(String name, String number) {

    }

    @Override
    public void delete(String name) {

    }
}
