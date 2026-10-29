import java.util.Objects;

/** Demonstra o padrão Chain of Responsibility no encaminhamento de solicitações. */
public class Main {
    enum Categoria { SENHA, TECNICA, FINANCEIRA, OUTRA }

    static final class Solicitacao {
        private final int protocolo;
        private final Categoria categoria;
        private final String descricao;
        private String responsavel;
        private boolean atendida;

        Solicitacao(int protocolo, Categoria categoria, String descricao) {
            this.protocolo = protocolo;
            this.categoria = Objects.requireNonNull(categoria);
            this.descricao = Objects.requireNonNull(descricao);
        }
        public int getProtocolo() { return protocolo; }
        public Categoria getCategoria() { return categoria; }
        public String getDescricao() { return descricao; }
        public String getResponsavel() { return responsavel; }
        public boolean isAtendida() { return atendida; }
        private void concluir(String responsavel) { this.responsavel = responsavel; this.atendida = true; }
    }

    abstract static class Atendente {
        private Atendente proximo;
        public Atendente definirProximo(Atendente proximo) { this.proximo = proximo; return proximo; }
        public final boolean atender(Solicitacao solicitacao) {
            Objects.requireNonNull(solicitacao);
            if (podeAtender(solicitacao)) {
                processar(solicitacao);
                return true;
            }
            return proximo != null && proximo.atender(solicitacao);
        }
        protected abstract boolean podeAtender(Solicitacao solicitacao);
        protected abstract void processar(Solicitacao solicitacao);
    }

    static final class SuporteSenha extends Atendente {
        protected boolean podeAtender(Solicitacao s) { return s.getCategoria() == Categoria.SENHA; }
        protected void processar(Solicitacao s) { s.concluir("Suporte de acesso"); }
    }
    static final class SuporteTecnico extends Atendente {
        protected boolean podeAtender(Solicitacao s) { return s.getCategoria() == Categoria.TECNICA; }
        protected void processar(Solicitacao s) { s.concluir("Suporte técnico"); }
    }
    static final class SuporteFinanceiro extends Atendente {
        protected boolean podeAtender(Solicitacao s) { return s.getCategoria() == Categoria.FINANCEIRA; }
        protected void processar(Solicitacao s) { s.concluir("Equipe financeira"); }
    }
    static final class SuporteGeral extends Atendente {
        protected boolean podeAtender(Solicitacao s) { return s.getCategoria() == Categoria.OUTRA; }
        protected void processar(Solicitacao s) { s.concluir("Atendimento geral"); }
    }

    static Atendente criarFluxoPadrao() {
        Atendente acesso = new SuporteSenha();
        acesso.definirProximo(new SuporteTecnico())
              .definirProximo(new SuporteFinanceiro())
              .definirProximo(new SuporteGeral());
        return acesso;
    }

    public static void main(String[] args) {
        Atendente fluxo = criarFluxoPadrao();
        Solicitacao[] solicitacoes = {
            new Solicitacao(101, Categoria.SENHA, "Redefinir senha"),
            new Solicitacao(102, Categoria.TECNICA, "Aplicativo não inicia"),
            new Solicitacao(103, Categoria.FINANCEIRA, "Dúvida sobre cobrança"),
            new Solicitacao(104, Categoria.OUTRA, "Informação geral")
        };
        for (Solicitacao s : solicitacoes) {
            boolean atendida = fluxo.atender(s);
            String resultado = atendida ? s.getResponsavel() : "Sem responsável";
            System.out.println("Protocolo " + s.getProtocolo() + " — " + s.getDescricao() + " → " + resultado);
        }
    }
}
