package br.ufs.raulsvilar.agendahashing.hashing;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * Created by raulsvilar on 06/10/16.
 */

class Record implements Serializable{

    static final long serialVersionUID = 25L;
    static final int bufferSize = 100;
    private String name;
    private String number;

    /**
     * Cria um Record a partir de array de bytes
     * @param bytes Array de bytes utilizado para construir o Record
     */
    public Record(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.name = getStringFromByteBuffer(buffer);
        this.number = getStringFromByteBuffer(buffer);
    }

    /**
     * Cria um Record com dados preenchidos
     * @param name Nome do contato
     * @param number Número de telefone do contato
     */
    Record(String name, String number) {
        this.name = name;
        this.number = number;
    }

    /**
     * Insere uma String em um ByteBuffer.
     * @param buffer ByteBuffer onde a string deve ser adicionada
     * @param str String a ser adicionada
     */
    private void addStringInByteBuffer(ByteBuffer buffer, String str) {
        byte[] bytes;
        bytes = str.getBytes();
        buffer.putInt(bytes.length);
        for (byte aByte : bytes) buffer.put(aByte);
    }

    /**
     * Retorna a string que está gravada no ByteBuffer
     * @param buffer ByteBuffer que contém os dados gravados em disco
     * @return String armazenada
     */
    private String getStringFromByteBuffer(ByteBuffer buffer) {
        int strLength = buffer.getInt();
        byte [] bytes = new byte[strLength];
        buffer.get(bytes, 0, strLength);
        return new String(bytes);
    }

    public byte[] getBytes() {
        ByteBuffer bf = ByteBuffer.allocate(bufferSize);
        addStringInByteBuffer(bf, name);
        addStringInByteBuffer(bf, number);
        return bf.array();
    }

    public String getName() {
        return this.name;
    }

    public String getNumber() {
        return this.number;
    }
}
