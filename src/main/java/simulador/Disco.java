package simulador;

import java.util.ArrayList;
import java.util.List;

class Disco {
    static final int TAMANHO_BLOCO = 4 * 1024;
    private final List<Bloco> blocos = new ArrayList<>();

    Disco(int quantidade) {
        for (int i = 0; i < quantidade; i++) blocos.add(new Bloco(i));
        blocos.get(0).reservarRaiz();
    }
    int getQuantidade() { return blocos.size(); }
    int getQuantidadeLivre() { return (int) blocos.stream().filter(b -> !b.isOcupado()).count(); }
    int getQuantidadeOcupada() { return getQuantidade() - getQuantidadeLivre(); }
    List<Integer> alocar(Arquivo arquivo, int quantidade) {
        List<Bloco> livres = blocos.stream().filter(b -> !b.isOcupado()).limit(quantidade).toList();
        if (livres.size() < quantidade) throw new IllegalStateException("Espaço insuficiente no disco.");
        List<Integer> alocados = new ArrayList<>();
        for (Bloco bloco : livres) { bloco.ocupar(arquivo); alocados.add(bloco.getNumero()); }
        return alocados;
    }
    void liberar(Arquivo arquivo) {
        liberarNumeros(arquivo.getINode().getBlocos());
        arquivo.getINode().definirBlocos(List.of());
    }
    void liberarNumeros(List<Integer> numeros) { for (int numero : numeros) blocos.get(numero).liberar(); }
    List<Bloco> getBlocos() { return List.copyOf(blocos); }
}
