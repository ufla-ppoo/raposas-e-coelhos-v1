import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Representa uma grade retangular de posições no campo.
 * Cada posição pode armazenar um único animal.
 * 
 * @author David J. Barnes e Michael Kölling
 *  Traduzido por Julio César Alves
 * @version 2025.05.24
 */
public class Campo
{
    // Um gerador de números aleatórios para fornecer localizações aleatórias.
    private static final Random rand = Randomizador.obterRandom();
    
    // O comprimento e a largura do campo.
    private int comprimento, largura;
    // Armazenamento para os animais.
    private Object[][] campo;

    /**
     * Representa um campo com as dimensões fornecidas.
     * @param comprimento O comprimento do campo.
     * @param largura A largura do campo.
     */
    public Campo(int comprimento, int largura)
    {
        this.comprimento = comprimento;
        this.largura = largura;
        campo = new Object[comprimento][largura];
    }
    
    /**
     * Esvazia o campo.
     */
    public void limpar()
    {
        for(int linha = 0; linha < comprimento; linha++) {
            for(int coluna = 0; coluna < largura; coluna++) {
                campo[linha][coluna] = null;
            }
        }
    }
    
    /**
     * Limpa a localização fornecida.
     * @param localizacao A localização a ser limpa.
     */
    public void limpar(Localizacao localizacao)
    {
        campo[localizacao.obterLinha()][localizacao.obterColuna()] = null;
    }
    
    /**
     * Coloca um animal na localização fornecida.
     * Se já houver um animal na localização, ele será substituído.
     * @param animal O animal a ser colocado.
     * @param linha Coordenada da linha da localização.
     * @param coluna Coordenada da coluna da localização.
     */
    public void colocar(Object animal, int linha, int coluna)
    {
        colocar(animal, new Localizacao(linha, coluna));
    }
    
    /**
     * Coloca um animal na localização fornecida.
     * Se já houver um animal na localização, ele será substituído.
     * @param animal O animal a ser colocado.
     * @param localizacao Onde colocar o animal.
     */
    public void colocar(Object animal, Localizacao localizacao)
    {
        campo[localizacao.obterLinha()][localizacao.obterColuna()] = animal;
    }
    
    /**
     * Retorna o animal da localização fornecida, se houver.
     * @param localizacao Onde no campo.
     * @return O animal na localização fornecida, ou null se não houver nenhum.
     */
    public Object obterObjetoEm(Localizacao localizacao)
    {
        return obterObjetoEm(localizacao.obterLinha(), localizacao.obterColuna());
    }
    
    /**
     * Retorna o animal na localização fornecida, se houver.
     * @param linha A linha desejada.
     * @param coluna A coluna desejada.
     * @return O animal na localização fornecida, ou null se não houver nenhum.
     */
    public Object obterObjetoEm(int linha, int coluna)
    {
        return campo[linha][coluna];
    }
    
    /**
     * Gera uma localização aleatória que seja vizinha à
     * localização fornecida, ou a mesma localização.
     * A localização retornada estará dentro dos limites válidos
     * do campo.
     * @param localizacao A localização a partir da qual gerar uma vizinhança.
     * @return Uma localização válida dentro da área da grade.
     */
    public Localizacao localizacaoVizinhaAleatoria(Localizacao localizacao)
    {
        List<Localizacao> vizinhos = localizacoesVizinhas(localizacao);
        return vizinhos.get(0);
    }
    
    /**
     * Obtém uma lista embaralhada das localizações vizinhas livres.
     * @param localizacao Obter localizações vizinhas a esta.
     * @return Uma lista de localizações vizinhas livres.
     */
    public List<Localizacao> localizacoesVizinhasLivres(Localizacao localizacao)
    {
        List<Localizacao> livres = new LinkedList<>();
        List<Localizacao> vizinhas = localizacoesVizinhas(localizacao);
        for(Localizacao proxima : vizinhas) {
            if(obterObjetoEm(proxima) == null) {
                livres.add(proxima);
            }
        }
        return livres;
    }
    
    /**
     * Tenta encontrar uma localização livre que seja vizinha à
     * localização fornecida. Se não houver nenhuma, retornar null.
     * A localização retornada estará dentro dos limites válidos
     * do campo.
     * @param localizacao A localização a partir da qual gerar uma vizinhança.
     * @return Uma localização válida dentro da área da grade.
     */
    public Localizacao localizacaoVizinhaLivre(Localizacao localizacao)
    {
        // As localizações livres disponíveis.
        List<Localizacao> livres = localizacoesVizinhasLivres(localizacao);
        if(livres.size() > 0) {
            return livres.get(0);
        }
        else {
            return null;
        }
    }

    /**
     * Retorna uma lista embaralhada de localizações vizinhas à fornecida.
     * A lista não incluirá a própria localização.
     * Todas as localizações estarão dentro da grade.
     * @param localizacao A localização a partir da qual gerar vizinhanças.
     * @return Uma lista de localizações vizinhas à fornecida.
     */
    public List<Localizacao> localizacoesVizinhas(Localizacao localizacao)
    {
        assert localizacao != null : "Localização nula passada para localizacoesVizinhas";
        // A lista de localizações a ser retornada.
        List<Localizacao> localizacoes = new LinkedList<>();
        if(localizacao != null) {
            int linha = localizacao.obterLinha();
            int coluna = localizacao.obterColuna();
            for(int deslocLinha = -1; deslocLinha <= 1; deslocLinha++) {
                int proxLinha = linha + deslocLinha;
                if(proxLinha >= 0 && proxLinha < comprimento) {
                    for(int deslocColuna = -1; deslocColuna <= 1; deslocColuna++) {
                        int proxColuna = coluna + deslocColuna;
                        // Excluir localizações inválidas e a localização original.
                        if(proxColuna >= 0 && proxColuna < largura && (deslocLinha != 0 || deslocColuna != 0)) {
                            localizacoes.add(new Localizacao(proxLinha, proxColuna));
                        }
                    }
                }
            }
            
            // Embaralha a lista. Vários outros métodos dependem da lista
            // estar em uma ordem aleatória.
            Collections.shuffle(localizacoes, rand);
        }
        return localizacoes;
    }

    /**
     * Retorna o comprimento do campo.
     * @return O comprimento do campo.
     */
    public int obterComprimento()
    {
        return comprimento;
    }
    
    /**
     * Retorna a largura do campo.
     * @return A largura do campo.
     */
    public int obterLargura()
    {
        return largura;
    }
}