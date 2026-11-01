package simulador;

class Bloco {
    private final int numero;
    private Arquivo proprietario;
    private boolean ocupado;

    Bloco(int numero) { this.numero = numero; }
    int getNumero() { return numero; }
    boolean isOcupado() { return ocupado; }
    Arquivo getProprietario() { return proprietario; }
    void ocupar(Arquivo arquivo) { proprietario = arquivo; ocupado = true; }
    void liberar() { proprietario = null; ocupado = false; }
    void reservarRaiz() { ocupado = true; }
}
