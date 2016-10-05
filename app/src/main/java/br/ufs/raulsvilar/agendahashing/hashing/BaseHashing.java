package br.ufs.raulsvilar.agendahashing.hashing;

/**
 * Created by raulsvilar on 05/10/16.
 */

interface BaseHashing {

    /**
     * Converte um String em um hash númerico
     * @param s String para ser convertidada em hash
     * @return Hash
     */
    String hash(String s);

    /**
     * Adiciona uma entrada na tabela hash
     * @param name Nome do contato a ser armazenado, que é a chave na tabela
     * @param number Número do contato a ser armazenado, que é o valor na tabela
     */
    void add(String name, String number);

    /**
     * Remove uma entrada da tabela
     * @param name Nome do contato que a ser removido
     */
    void delete(String name);
}
