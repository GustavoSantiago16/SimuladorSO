package simulador;

import java.util.List;
import java.util.Scanner;

class Terminal {
    private final SistemaArquivos sistema;
    private final GerenciadorArquivos arquivos;
    private final Scanner scanner = new Scanner(System.in);

    Terminal(SistemaArquivos sistema) { this.sistema = sistema; arquivos = sistema.getGerenciador(); }
    void executar() {
        System.out.println("Simulador de Sistema de Arquivos. Digite 'help' para comandos ou 'exit' para sair.");
        while (true) {
            System.out.print(arquivos.pwd() + "> ");
            if (!scanner.hasNextLine()) break;
            String linha = scanner.nextLine().trim();
            if (linha.isEmpty()) continue;
            if (linha.equals("exit") || linha.equals("quit")) break;
            try { executarComando(linha); }
            catch (RuntimeException erro) { System.out.println("Erro: " + erro.getMessage()); }
        }
    }
    private void executarComando(String linha) {
        String[] args = linha.split("\\s+");
        String comando = args[0];
        switch (comando) {
            case "help" -> ajuda();
            case "mkdir" -> { exigir(args, 2); arquivos.mkdir(args[1]); }
            case "create" -> { exigir(args, 3); arquivos.create(args[1], parseTamanho(args[2])); }
            case "ls" -> listar(args.length > 1 ? args[1] : ".");
            case "tree" -> arvore(args.length > 1 ? args[1] : "/");
            case "cd" -> { exigir(args, 2); arquivos.cd(args[1]); }
            case "pwd" -> System.out.println(arquivos.pwd());
            case "rm" -> { exigir(args, 2); arquivos.rm(args[1]); }
            case "rmdir" -> { exigir(args, 2); arquivos.rmdir(args[1]); }
            case "rename" -> { exigir(args, 3); arquivos.rename(args[1], args[2]); }
            case "cat" -> cat(args);
            case "update" -> { exigir(args, 3); arquivos.update(args[1], parseTamanho(args[2])); }
            case "fs_status" -> status();
            default -> throw new IllegalArgumentException("Comando desconhecido. Digite 'help'.");
        }
    }
    private void listar(String caminho) {
        for (NoSistema no : arquivos.listar(caminho))
            System.out.println(no.isDiretorio() ? no.getNome() + "/" : no.getNome() + " (" + sistema.formatarTamanho(((Arquivo) no).getINode().getTamanhoBytes()) + ")");
    }
    private void arvore(String caminho) {
        Diretorio raiz = caminho.equals("/") ? arquivos.getRaiz() : diretorio(caminho);
        System.out.println(caminho.equals("/") ? "/" : raiz.getNome());
        imprimirArvore(raiz, "");
    }
    private Diretorio diretorio(String caminho) {
        return arquivos.resolverDiretorio(caminho);
    }
    private void imprimirArvore(Diretorio diretorio, String prefixo) {
        List<NoSistema> filhos = List.copyOf(diretorio.getFilhos());
        for (int i = 0; i < filhos.size(); i++) {
            NoSistema no = filhos.get(i); boolean ultimo = i == filhos.size() - 1;
            String ramo = ultimo ? "└── " : "├── ";
            if (no instanceof Diretorio d) {
                System.out.println(prefixo + ramo + d.getNome());
                imprimirArvore(d, prefixo + (ultimo ? "    " : "│   "));
            } else {
                Arquivo a = (Arquivo) no;
                System.out.println(prefixo + ramo + a.getNome() + " (" + sistema.formatarTamanho(a.getINode().getTamanhoBytes()) + ")");
            }
        }
    }
    private void cat(String[] args) {
        exigir(args, 2); Arquivo arquivo = arquivos.arquivo(args[1]);
        System.out.println("[Conteúdo simulado de " + arquivo.getNome() + "; tamanho " + sistema.formatarTamanho(arquivo.getINode().getTamanhoBytes()) + "]");
    }
    private void status() {
        Disco disco = sistema.getDisco();
        System.out.println("Tamanho do bloco: " + Disco.TAMANHO_BLOCO + " bytes (4 KB)");
        System.out.println("Quantidade total: " + disco.getQuantidade());
        System.out.println("Quantidade livre: " + disco.getQuantidadeLivre());
        System.out.println("Quantidade ocupada: " + disco.getQuantidadeOcupada());
        System.out.println("\nMapa do disco:");
        for (Bloco bloco : sistema.blocos()) {
            String dono = bloco.getProprietario() == null ? (bloco.getNumero() == 0 ? "ROOT" : "LIVRE") : bloco.getProprietario().getNome();
            System.out.printf("%02d %s%n", bloco.getNumero(), dono);
        }
        System.out.println("\nFragmentação interna: " + sistema.getFragmentacaoInterna() + " bytes");
        System.out.println("Fragmentação externa: 0 bytes");
        System.out.println("Necessidade de compactação: NÃO");
    }
    private long parseTamanho(String texto) {
        String valor = texto.toUpperCase();
        long multiplicador = 1;
        if (valor.endsWith("KB")) { multiplicador = 1024; valor = valor.substring(0, valor.length() - 2); }
        else if (valor.endsWith("MB")) { multiplicador = 1024L * 1024; valor = valor.substring(0, valor.length() - 2); }
        else if (valor.endsWith("B")) valor = valor.substring(0, valor.length() - 1);
        try { return Math.multiplyExact(Long.parseLong(valor), multiplicador); }
        catch (NumberFormatException | ArithmeticException erro) { throw new IllegalArgumentException("Tamanho inválido: " + texto); }
    }
    private void exigir(String[] args, int quantidade) {
        if (args.length != quantidade) throw new IllegalArgumentException("Quantidade de argumentos inválida. Digite 'help'.");
    }
    private void ajuda() {
        System.out.println("mkdir caminho | create caminho tamanho | ls [caminho] | tree [caminho] | cd caminho | pwd");
        System.out.println("rm arquivo | rmdir diretorio-vazio | rename caminho novo-nome | cat arquivo | update arquivo tamanho | fs_status | exit");
        System.out.println("Tamanhos aceitos: bytes (B), KB ou MB.");
    }
}
