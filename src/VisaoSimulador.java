import java.awt.*;
import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Uma visão gráfica da grade de simulação.
 * A visão exibe um retângulo colorido para cada localização,
 * representando seu conteúdo. Usa uma cor de fundo padrão.
 * As cores para cada tipo de espécie podem ser definidas usando o
 * método definirCor.
 * 
 * @author David J. Barnes e Michael Kölling
 * @version 2016.02.29
 */
public class VisaoSimulador extends JFrame
{
    // Cores usadas para localizações vazias.
    private static final Color COR_VAZIA = Color.white;

    // Cor usada para objetos que não têm cor definida.
    private static final Color COR_DESCONHECIDA = Color.gray;

    private final String PREFIXO_PASSO = "Passo: ";
    private final String PREFIXO_POPULACAO = "População: ";
    private JLabel rotuloPasso, populacao;
    private VisaoCampo visaoCampo;
    
    // Um mapa para armazenar cores para participantes na simulação.
    private Map<Class<?>, Color> cores;
    // Um objeto de estatísticas que calcula e armazena informações da simulação.
    private EstatisticasCampo estatisticas;

    /**
     * Cria uma visão com a largura e altura fornecidas.
     * @param altura A altura da simulação.
     * @param largura A largura da simulação.
     */
    public VisaoSimulador(int altura, int largura)
    {
        estatisticas = new EstatisticasCampo();
        cores = new LinkedHashMap<>();

        setTitle("Simulação de Raposas e Coelhos");
        rotuloPasso = new JLabel(PREFIXO_PASSO, JLabel.CENTER);
        populacao = new JLabel(PREFIXO_POPULACAO, JLabel.CENTER);
        
        setLocation(100, 50);
        
        visaoCampo = new VisaoCampo(altura, largura);

        Container conteudo = getContentPane();
        conteudo.add(rotuloPasso, BorderLayout.NORTH);
        conteudo.add(visaoCampo, BorderLayout.CENTER);
        conteudo.add(populacao, BorderLayout.SOUTH);
        pack();
        setVisible(true);
    }
    
    /**
     * Define uma cor a ser usada para uma classe específica de animal.
     * @param classeAnimal A classe do animal.
     * @param cor A cor a ser usada para a classe fornecida.
     */
    public void definirCor(Class<?> classeAnimal, Color cor)
    {
        cores.put(classeAnimal, cor);
    }

    /**
     * @return A cor a ser usada para uma classe específica de animal.
     */
    private Color getCor(Class<?> classeAnimal)
    {
        Color cor = cores.get(classeAnimal);
        if(cor == null) {
            // nenhuma cor definida para esta classe
            return COR_DESCONHECIDA;
        }
        else {
            return cor;
        }
    }

    /**
     * Mostra o estado atual do campo.
     * @param passo Qual iteração do passo está sendo exibida.
     * @param campo O campo cujo estado será exibido.
     */
    public void mostrarStatus(int passo, Campo campo)
    {
        if(!isVisible()) {
            setVisible(true);
        }
            
        rotuloPasso.setText(PREFIXO_PASSO + passo);
        estatisticas.redefinir();
        
        visaoCampo.prepararPintura();

        for(int linha = 0; linha < campo.getComprimento(); linha++) {
            for(int coluna = 0; coluna < campo.getLargura(); coluna++) {
                Object animal = campo.getObjetoEm(linha, coluna);
                if(animal != null) {
                    estatisticas.incrementarContagem(animal.getClass());
                    visaoCampo.desenharMarca(coluna, linha, getCor(animal.getClass()));
                }
                else {
                    visaoCampo.desenharMarca(coluna, linha, COR_VAZIA);
                }
            }
        }
        estatisticas.finalizarContagem();

        populacao.setText(PREFIXO_POPULACAO + estatisticas.getDetalhesPopulacao(campo));
        visaoCampo.repaint();
    }

    /**
     * Determina se a simulação deve continuar a ser executada.
     * @return true Se houver mais de uma espécie viva.
     */
    public boolean ehViavel(Campo campo)
    {
        return estatisticas.ehViavel(campo);
    }
    
    /**
     * Fornece uma visão gráfica de um campo retangular. Esta é 
     * uma classe aninhada (uma classe definida dentro de outra classe) que
     * define um componente personalizado para a interface do usuário. Este
     * componente exibe o campo.
     * Isso é algo mais avançado em GUI - você pode ignorar isso 
     * para o seu projeto, se preferir.
     */
    private class VisaoCampo extends JPanel
    {
        private final int FATOR_ESCALA_GRADE = 6;

        private int larguraGrade, alturaGrade;
        private int escalaX, escalaY;
        Dimension tamanho;
        private Graphics g;
        private Image imagemCampo;

        /**
         * Cria um novo componente VisaoCampo.
         */
        public VisaoCampo(int altura, int largura)
        {
            alturaGrade = altura;
            larguraGrade = largura;
            tamanho = new Dimension(0, 0);
        }

        /**
         * Informa ao gerenciador de GUI o tamanho desejado.
         */
        public Dimension getPreferredSize()
        {
            return new Dimension(larguraGrade * FATOR_ESCALA_GRADE,
                                 alturaGrade * FATOR_ESCALA_GRADE);
        }

        /**
         * Prepara para uma nova rodada de pintura. Como o componente
         * pode ser redimensionado, calcula novamente o fator de escala.
         */
        public void prepararPintura()
        {
            if(!tamanho.equals(getSize())) {  // se o tamanho mudou...
                tamanho = getSize();
                imagemCampo = visaoCampo.createImage(tamanho.width, tamanho.height);
                g = imagemCampo.getGraphics();

                escalaX = tamanho.width / larguraGrade;
                if(escalaX < 1) {
                    escalaX = FATOR_ESCALA_GRADE;
                }
                escalaY = tamanho.height / alturaGrade;
                if(escalaY < 1) {
                    escalaY = FATOR_ESCALA_GRADE;
                }
            }
        }
        
        /**
         * Pinta uma localização na grade deste campo com uma cor específica.
         */
        public void desenharMarca(int x, int y, Color cor)
        {
            g.setColor(cor);
            g.fillRect(x * escalaX, y * escalaY, escalaX-1, escalaY-1);
        }

        /**
         * O componente VisaoCampo precisa ser redesenhado. Copia a
         * imagem interna para a tela.
         */
        public void paintComponent(Graphics g)
        {
            if(imagemCampo != null) {
                Dimension tamanhoAtual = getSize();
                if(tamanho.equals(tamanhoAtual)) {
                    g.drawImage(imagemCampo, 0, 0, null);
                }
                else {
                    // Redimensiona a imagem anterior.
                    g.drawImage(imagemCampo, 0, 0, tamanhoAtual.width, tamanhoAtual.height, null);
                }
            }
        }
    }
}