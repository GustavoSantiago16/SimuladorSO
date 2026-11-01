package simulador;

import java.util.List;

class SistemaArquivos {
    private final Disco disco = new Disco(64);
    private final GerenciadorArquivos gerenciador = new GerenciadorArquivos(disco);

    GerenciadorArquivos getGerenciador() { return gerenciador; }
    Disco getDisco() { return disco; }
    long getFragmentacaoInterna() {
        long total = fragmentacao(gerenciador.getRaiz());
        return total;
    }
    private long fragmentacao(Diretorio diretorio) {
        long total = 0;
        for (NoSistema no : diretorio.getFilhos()) {
            if (no instanceof Arquivo arquivo) {
                long reservado = (long) arquivo.getINode().getBlocos().size() * Disco.TAMANHO_BLOCO;
                total += reservado - arquivo.getINode().getTamanhoBytes();
            } else total += fragmentacao((Diretorio) no);
        }
        return total;
    }
    String formatarTamanho(long bytes) {
        if (bytes == 0) return "0B";
        if (bytes % (1024L * 1024) == 0 && bytes >= 1024L * 1024) return (bytes / (1024L * 1024)) + "MB";
        if (bytes % 1024 == 0) return (bytes / 1024) + "KB";
        return bytes + "B";
    }
    List<Bloco> blocos() { return disco.getBlocos(); }
}
