/** Verificações do encadeamento e do encaminhamento das solicitações. */
public final class ChainResponsibilityTest {
    private static int verificacoes;
    private static void verificar(boolean condicao, String mensagem) {
        if (!condicao) throw new AssertionError(mensagem);
        verificacoes++;
    }
    private static void encaminha(Main.Categoria categoria, String responsavel) {
        Main.Solicitacao s = new Main.Solicitacao(1, categoria, "teste");
        boolean resultado = Main.criarFluxoPadrao().atender(s);
        verificar(resultado, "Solicitação deveria ser atendida: " + categoria);
        verificar(s.isAtendida(), "Solicitação deve estar marcada como atendida");
        verificar(responsavel.equals(s.getResponsavel()), "Responsável incorreto para " + categoria);
    }
    public static void main(String[] args) {
        encaminha(Main.Categoria.SENHA, "Suporte de acesso");
        encaminha(Main.Categoria.TECNICA, "Suporte técnico");
        encaminha(Main.Categoria.FINANCEIRA, "Equipe financeira");
        encaminha(Main.Categoria.OUTRA, "Atendimento geral");

        Main.Atendente primeiro = new Main.SuporteSenha();
        Main.Atendente ultimo = new Main.SuporteGeral();
        primeiro.definirProximo(ultimo);
        Main.Solicitacao geral = new Main.Solicitacao(2, Main.Categoria.OUTRA, "geral");
        verificar(primeiro.atender(geral), "Solicitação deve chegar ao próximo atendente");
        verificar("Atendimento geral".equals(geral.getResponsavel()), "Último atendente deve processar a solicitação");

        Main.Atendente semCorrespondencia = new Main.SuporteSenha();
        Main.Solicitacao tecnica = new Main.Solicitacao(3, Main.Categoria.TECNICA, "técnica");
        verificar(!semCorrespondencia.atender(tecnica), "Sem próximo atendente, deve retornar não atendida");
        verificar(!tecnica.isAtendida(), "Solicitação sem correspondência permanece pendente");
        verificar(tecnica.getResponsavel() == null, "Solicitação não atendida não recebe responsável");

        Main.Solicitacao independente = new Main.Solicitacao(4, Main.Categoria.SENHA, "acesso");
        Main.criarFluxoPadrao().atender(independente);
        verificar("Suporte de acesso".equals(independente.getResponsavel()), "Cada chamada encaminha sua própria solicitação");
        System.out.println(verificacoes + " verificações aprovadas.");
    }
}
