package simulador;

abstract class NoSistema {
    private String nome;
    private Diretorio pai;

    NoSistema(String nome, Diretorio pai) {
        this.nome = nome;
        this.pai = pai;
    }

    String getNome() { return nome; }
    Diretorio getPai() { return pai; }
    void setNome(String nome) { this.nome = nome; }
    void setPai(Diretorio pai) { this.pai = pai; }
    abstract boolean isDiretorio();
}
