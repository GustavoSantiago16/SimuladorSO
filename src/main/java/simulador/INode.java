package simulador;

import java.util.ArrayList;
import java.util.List;

class INode {
    private long tamanhoBytes;
    private final List<Integer> blocos = new ArrayList<>();

    INode(long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }
    long getTamanhoBytes() { return tamanhoBytes; }
    void setTamanhoBytes(long tamanhoBytes) { this.tamanhoBytes = tamanhoBytes; }
    List<Integer> getBlocos() { return List.copyOf(blocos); }
    void definirBlocos(List<Integer> novos) { blocos.clear(); blocos.addAll(novos); }
}
