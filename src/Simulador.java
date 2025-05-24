import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.awt.Color;

/**
 * Um simulador simples de predador-presa, baseado em um campo retangular contendo 
 * coelhos e raposas.
 * 
 * @author David J. Barnes e Michael Kölling
 *  Traduzido por Julio César Alves
 * @version 2025.05.24
 */
public class Simulador
{
    // Constantes que representam informações de configuração para a simulação.
    // A largura padrão da grade.
    private static final int LARGURA_PADRAO = 120;
    // O comprimento padrão da grade.
    private static final int COMPRIMENTO_PADRAO = 80;
    // A probabilidade de uma raposa ser criada em qualquer posição da grade.
    private static final double PROBABILIDADE_CRIACAO_RAPOSA = 0.02;
    // A probabilidade de um coelho ser criado em qualquer posição.
    private static final double PROBABILIDADE_CRIACAO_COELHO = 0.08;    

    // Listas de animais no campo.
    private List<Coelho> coelhos;
    private List<Raposa> raposas;
    // O estado atual do campo.
    private Campo campo;
    // O passo atual da simulação.
    private int passo;
    // Uma visão gráfica da simulação.
    private VisaoSimulador visao;
    
    /**
     * Constrói um campo de simulação com tamanho padrão.
     */
    public Simulador()
    {
        this(COMPRIMENTO_PADRAO, LARGURA_PADRAO);
    }
    
    /**
     * Cria um campo de simulação com o tamanho fornecido.
     * @param comprimento O comprimento do campo. Deve ser maior que zero.
     * @param largura A largura do campo. Deve ser maior que zero.
     */
    public Simulador(int comprimento, int largura)
    {
        if(largura <= 0 || comprimento <= 0) {
            System.out.println("As dimensões devem ser >= zero.");
            System.out.println("Usando valores padrões.");
            comprimento = COMPRIMENTO_PADRAO;
            largura = LARGURA_PADRAO;
        }
        
        coelhos = new ArrayList<>();
        raposas = new ArrayList<>();
        campo = new Campo(comprimento, largura);

        // Cria uma visão do estado de cada localização no campo.
        visao = new VisaoSimulador(comprimento, largura, this);
        visao.definirCor(Coelho.class, Color.ORANGE);
        visao.definirCor(Raposa.class, Color.BLUE);
        
        // Configura um ponto de partida válido.
        reiniciar();
    }
    
    /**
     * Executa a simulação a partir de seu estado atual por um período razoavelmente longo 
     * (4000 passos).
     */
    public void executarSimulacaoLonga()
    {
        // altere o parâmetro de atraso se quiser executar mais lentamente
        simular(4000, 0);
    }
    
    /**
     * Executa a simulação pelo número fornecido de passos.
     * Para a simulação antes do número fornecido de passos se ela se tornar inviável.
     * @param numPassos O número de passos a executar.
     */
    public void simular(int numPassos, int atraso)
    {
        for(int passo = 1; passo <= numPassos && visao.ehViavel(campo); passo++) {
            simularUmPasso();
            if (atraso > 0) {
                pausar(atraso);   
            }
        }
        visao.reabilitarOpcoes();
    }
    
    /**
     * Executa a simulação a partir de seu estado atual por um único passo. 
     * Itera por todo o campo atualizando o estado de cada raposa e coelho.
     */
    public void simularUmPasso()
    {
        passo++;

        // Fornece espaço para coelhos recém-nascidos.
        List<Coelho> novosCoelhos = new ArrayList<>();        
        // Permite que todos os coelhos ajam.
        for(Iterator<Coelho> it = coelhos.iterator(); it.hasNext(); ) {
            Coelho coelho = it.next();
            coelho.correr(novosCoelhos);
            if(!coelho.estaVivo()) {
                it.remove();
            }
        }
        
        // Fornece espaço para raposas recém-nascidas.
        List<Raposa> novasRaposas = new ArrayList<>();        
        // Permite que todas as raposas ajam.
        for(Iterator<Raposa> it = raposas.iterator(); it.hasNext(); ) {
            Raposa raposa = it.next();
            raposa.cacar(novasRaposas);
            if(!raposa.estaViva()) {
                it.remove();
            }
        }
        
        // Adiciona as raposas e coelhos recém-nascidos às listas principais.
        coelhos.addAll(novosCoelhos);
        raposas.addAll(novasRaposas);

        visao.mostrarStatus(passo, campo);
    }
        
    /**
     * Reinicia a simulação para uma posição inicial.
     */
    public void reiniciar()
    {
        passo = 0;
        coelhos.clear();
        raposas.clear();
        povoar();
        
        // Mostra o estado inicial na visão.
        visao.mostrarStatus(passo, campo);
        visao.reabilitarOpcoes();
    }
    
    /**
     * Povoa aleatoriamente o campo com raposas e coelhos.
     */
    private void povoar()
    {
        Random rand = Randomizador.obterRandom();
        campo.limpar();
        for(int linha = 0; linha < campo.obterComprimento(); linha++) {
            for(int coluna = 0; coluna < campo.obterLargura(); coluna++) {
                if(rand.nextDouble() <= PROBABILIDADE_CRIACAO_RAPOSA) {
                    Localizacao localizacao = new Localizacao(linha, coluna);
                    Raposa raposa = new Raposa(true, campo, localizacao);
                    raposas.add(raposa);
                }
                else if(rand.nextDouble() <= PROBABILIDADE_CRIACAO_COELHO) {
                    Localizacao localizacao = new Localizacao(linha, coluna);
                    Coelho coelho = new Coelho(true, campo, localizacao);
                    coelhos.add(coelho);
                }
                // caso contrário, deixa a localização vazia.
            }
        }
    }
    
    /**
     * Pausa por um tempo fornecido.
     * @param milissegundos O tempo para pausar, em milissegundos.
     */
    private void pausar(int milissegundos)
    {
        try {
            Thread.sleep(milissegundos);
        }
        catch (InterruptedException ie) {
            // acorda
        }
    }
}