package simulador;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

class Diretorio extends NoSistema {
    private final Map<String, NoSistema> filhos = new LinkedHashMap<>();

    Diretorio(String nome, Diretorio pai) { super(nome, pai); }
    @Override boolean isDiretorio() { return true; }
    Collection<NoSistema> getFilhos() { return filhos.values(); }
    NoSistema getFilho(String nome) { return filhos.get(nome); }
    boolean adicionar(NoSistema no) {
        if (filhos.containsKey(no.getNome())) return false;
        filhos.put(no.getNome(), no);
        return true;
    }
    NoSistema remover(String nome) { return filhos.remove(nome); }
    void renomearFilho(String antigo, String novo, NoSistema no) {
        filhos.remove(antigo);
        no.setNome(novo);
        filhos.put(novo, no);
    }
}
