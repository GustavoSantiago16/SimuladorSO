package simulador;

import java.util.ArrayList;
import java.util.List;

class GerenciadorArquivos {
    private final Disco disco;
    private final Diretorio raiz = new Diretorio("", null);
    private Diretorio atual = raiz;

    GerenciadorArquivos(Disco disco) { this.disco = disco; }
    String pwd() {
        if (atual == raiz) return "/";
        List<String> partes = new ArrayList<>();
        for (Diretorio d = atual; d != raiz; d = d.getPai()) partes.add(0, d.getNome());
        return "/" + String.join("/", partes);
    }
    void mkdir(String caminho) {
        Diretorio pai = resolverDiretorio(paiDo(caminho));
        String nome = nomeFinal(caminho);
        validarNome(nome);
        if (!pai.adicionar(new Diretorio(nome, pai))) throw new IllegalArgumentException("Já existe um item com esse nome.");
    }
    void create(String caminho, long tamanho) {
        if (tamanho < 0) throw new IllegalArgumentException("O tamanho não pode ser negativo.");
        Diretorio pai = resolverDiretorio(paiDo(caminho));
        String nome = nomeFinal(caminho); validarNome(nome);
        if (pai.getFilho(nome) != null) throw new IllegalArgumentException("Já existe um item com esse nome.");
        Arquivo arquivo = new Arquivo(nome, pai, tamanho);
        List<Integer> blocos = disco.alocar(arquivo, quantidadeBlocos(tamanho));
        arquivo.getINode().definirBlocos(blocos);
        pai.adicionar(arquivo);
    }
    List<NoSistema> listar(String caminho) { return List.copyOf(resolverDiretorio(caminho).getFilhos()); }
    Diretorio getRaiz() { return raiz; }
    void cd(String caminho) { atual = resolverDiretorio(caminho); }
    void rm(String caminho) {
        NoSistema no = resolverNo(caminho);
        if (no.isDiretorio()) throw new IllegalArgumentException("Use rmdir para remover diretórios.");
        disco.liberar((Arquivo) no); no.getPai().remover(no.getNome());
    }
    void rmdir(String caminho) {
        Diretorio diretorio = resolverDiretorio(caminho);
        if (diretorio == raiz) throw new IllegalArgumentException("A raiz não pode ser removida.");
        if (!diretorio.getFilhos().isEmpty()) throw new IllegalArgumentException("O diretório não está vazio.");
        diretorio.getPai().remover(diretorio.getNome());
        if (atual == diretorio) atual = diretorio.getPai();
    }
    void rename(String caminho, String novoNome) {
        NoSistema no = resolverNo(caminho); validarNome(novoNome);
        if (no.getPai().getFilho(novoNome) != null) throw new IllegalArgumentException("Já existe um item com esse nome.");
        no.getPai().renomearFilho(no.getNome(), novoNome, no);
    }
    Arquivo arquivo(String caminho) {
        NoSistema no = resolverNo(caminho);
        if (no.isDiretorio()) throw new IllegalArgumentException("O caminho indica um diretório.");
        return (Arquivo) no;
    }
    void update(String caminho, long tamanho) {
        if (tamanho < 0) throw new IllegalArgumentException("O tamanho não pode ser negativo.");
        Arquivo arquivo = arquivo(caminho);
        INode inode = arquivo.getINode();
        int necessarios = quantidadeBlocos(tamanho), atuais = inode.getBlocos().size();
        if (necessarios > atuais) {
            List<Integer> novos = disco.alocar(arquivo, necessarios - atuais);
            List<Integer> todos = new ArrayList<>(inode.getBlocos()); todos.addAll(novos); inode.definirBlocos(todos);
        } else if (necessarios < atuais) {
            List<Integer> todos = new ArrayList<>(inode.getBlocos());
            disco.liberarNumeros(todos.subList(necessarios, atuais));
            inode.definirBlocos(todos.subList(0, necessarios));
        }
        inode.setTamanhoBytes(tamanho);
    }
    private int quantidadeBlocos(long bytes) { return (int) ((bytes + Disco.TAMANHO_BLOCO - 1) / Disco.TAMANHO_BLOCO); }
    Diretorio resolverDiretorio(String caminho) {
        NoSistema no = resolverNo(caminho);
        if (!no.isDiretorio()) throw new IllegalArgumentException("O caminho não indica um diretório.");
        return (Diretorio) no;
    }
    private NoSistema resolverNo(String caminho) {
        if (caminho == null || caminho.isBlank() || caminho.equals(".")) return atual;
        Diretorio base = caminho.startsWith("/") ? raiz : atual;
        String[] partes = caminho.split("/");
        NoSistema no = base;
        for (int i = 0; i < partes.length; i++) {
            String parte = partes[i];
            if (parte.isEmpty() || parte.equals(".")) continue;
            if (parte.equals("..")) { if (!(no instanceof Diretorio d)) throw new IllegalArgumentException("Caminho inválido."); no = d.getPai() == null ? raiz : d.getPai(); continue; }
            if (!(no instanceof Diretorio d)) throw new IllegalArgumentException("Caminho inválido.");
            no = d.getFilho(parte);
            if (no == null) throw new IllegalArgumentException("Caminho não encontrado: " + caminho);
        }
        return no;
    }
    private String paiDo(String caminho) {
        int corte = caminho.lastIndexOf('/');
        return corte < 0 ? "." : (corte == 0 ? "/" : caminho.substring(0, corte));
    }
    private String nomeFinal(String caminho) {
        String limpo = caminho.endsWith("/") && caminho.length() > 1 ? caminho.substring(0, caminho.length() - 1) : caminho;
        int corte = limpo.lastIndexOf('/'); return corte < 0 ? limpo : limpo.substring(corte + 1);
    }
    private void validarNome(String nome) {
        if (nome.isBlank() || nome.equals(".") || nome.equals("..") || nome.contains("/")) throw new IllegalArgumentException("Nome inválido.");
    }
}
