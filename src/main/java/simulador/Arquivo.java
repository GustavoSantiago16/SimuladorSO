package simulador;

class Arquivo extends NoSistema {
    private final INode inode;

    Arquivo(String nome, Diretorio pai, long tamanhoBytes) {
        super(nome, pai);
        inode = new INode(tamanhoBytes);
    }
    @Override boolean isDiretorio() { return false; }
    INode getINode() { return inode; }
}
