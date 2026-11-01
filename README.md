# Simulador de Sistema Operacional — Etapa 1

Simulador de sistema de arquivos em Java 21, executado pelo terminal e sem dependências externas.

## Executar

No PowerShell, na pasta do projeto:

```powershell
javac --release 21 -d out src/main/java/simulador/*.java
java -cp out simulador.Main
```

Digite `help` para consultar os comandos e `exit` para encerrar.

## Comandos

`mkdir caminho`, `create caminho tamanho`, `ls [caminho]`, `tree [caminho]`, `cd caminho`, `pwd`, `rm arquivo`, `rmdir diretorio-vazio`, `rename caminho novo-nome`, `cat arquivo`, `update arquivo tamanho` e `fs_status`.

Tamanhos aceitos: bytes, `KB` e `MB`; por exemplo, `create /home/teste.txt 10KB`
