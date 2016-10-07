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
    long hash(String s);

    /**
     * Adiciona uma entrada na tabela hash
     * @param key Chave do valor a ser armazenado
     * @param value Valor a ser armazenado
     */
    void add(String key, String value);

    /**
     * Recupera um registro da tabela por meio da chave
     * @param key Chave do item a ser recuperado
     * @return Objeto com os dados armazenados
     */
    Record getValue(String key);

    /**
     * Remove uma entrada da tabela
     * @param key Chave do registro a ser removido
     */
    void delete(String key);
}
