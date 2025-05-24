import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * Um modelo simples de uma raposa.
 * Raposas envelhecem, se movem, comem coelhos e morrem.
 * 
 * @author David J. Barnes e Michael Kölling
 *  Traduzido por Julio César Alves
 * @version 2025.05.24
 */
public class Raposa
{
    // Características compartilhadas por todas as raposas (atributos estáticos, da classe).
    
    // A idade em que uma raposa pode começar a procriar.
    private static final int IDADE_REPRODUCAO = 15;
    // A idade máxima que uma raposa pode atingir.
    private static final int IDADE_MAXIMA = 150;
    // A probabilidade de uma raposa se reproduzir.
    private static final double PROBABILIDADE_REPRODUCAO = 0.08;
    // O número máximo de filhotes que podem nascer de cada vez.
    private static final int TAMANHO_MAXIMO_NINHADA = 2;
    // O valor nutricional de um único coelho. Na prática, este é o
    // número de passos que uma raposa pode dar antes de precisar comer novamente.
    private static final int VALOR_COMIDA_COELHO = 9;
    // Um gerador de números aleatórios compartilhado para controlar a reprodução.
    private static final Random rand = Randomizador.getRandom();
    
    // Características individuais (atributos comuns, de instância).

    // A idade da raposa.
    private int idade;
    // Indica se a raposa está viva ou não.
    private boolean viva;
    // A localização da raposa.
    private Localizacao localizacao;
    // O campo ocupado.
    private Campo campo;
    // O nível de comida da raposa, que aumenta ao comer coelhos.
    private int nivelComida;

    /**
     * Cria uma raposa. Uma raposa pode ser criada como recém-nascida (idade zero
     * e sem fome) ou com idade e nível de fome aleatórios.
     * 
     * @param idadeAleatoria Se verdadeiro, a raposa terá idade e nível de fome aleatórios.
     * @param campo O campo atualmente ocupado.
     * @param localizacao A localização dentro do campo.
     */
    public Raposa(boolean idadeAleatoria, Campo campo, Localizacao localizacao)
    {
        idade = 0;
        viva = true;
        this.campo = campo;
        setLocalizacao(localizacao);
        if(idadeAleatoria) {
            idade = rand.nextInt(IDADE_MAXIMA);
            nivelComida = rand.nextInt(VALOR_COMIDA_COELHO);
        }
        else {
            // deixa a idade como 0
            nivelComida = VALOR_COMIDA_COELHO;
        }
    }
    
    /**
     * Isto é o que a raposa faz na maior parte do tempo: ela caça coelhos.
     * Durante o processo, ela pode se reproduzir, morrer de fome
     * ou morrer de velhice.
     * @param novasRaposas Uma lista para retornar as raposas recém-nascidas.
     */
    public void cacar(List<Raposa> novasRaposas)
    {
        incrementarIdade();
        incrementarFome();
        if(viva) {
            reproduzir(novasRaposas);            
            // Move-se em direção a uma fonte de comida, se encontrada.
            Localizacao novaLocalizacao = buscarComida();
            if(novaLocalizacao == null) { 
                // Nenhuma comida encontrada - tenta se mover para uma localização livre.
                novaLocalizacao = campo.localizacaoVizinhaLivre(localizacao);
            }
            // Verifica se foi possível se mover.
            if(novaLocalizacao != null) {
                setLocalizacao(novaLocalizacao);
            }
            else {
                // Superlotação.
                morrer();
            }
        }
    }

    /**
     * Verifica se a raposa está viva ou não.
     * @return Verdadeiro se a raposa ainda estiver viva.
     */
    public boolean estaViva()
    {
        return viva;
    }

    /**
     * Retorna a localização da raposa.
     * @return A localização da raposa.
     */
    public Localizacao getLocalizacao()
    {
        return localizacao;
    }
    
    /**
     * Coloca a raposa na nova localização no campo fornecido.
     * @param novaLocalizacao A nova localização da raposa.
     */
    private void setLocalizacao(Localizacao novaLocalizacao)
    {
        if(localizacao != null) {
            campo.limpar(localizacao);
        }
        localizacao = novaLocalizacao;
        campo.colocar(this, novaLocalizacao);
    }
    
    /**
     * Aumenta a idade. Isso pode resultar na morte da raposa.
     */
    private void incrementarIdade()
    {
        idade++;
        if(idade > IDADE_MAXIMA) {
            morrer();
        }
    }
    
    /**
     * Faz com que esta raposa fique mais faminta. Isso pode resultar na morte da raposa.
     */
    private void incrementarFome()
    {
        nivelComida--;
        if(nivelComida <= 0) {
            morrer();
        }
    }
    
    /**
     * Procura por coelhos adjacentes à localização atual.
     * Apenas o primeiro coelho vivo é comido.
     * @return Onde a comida foi encontrada, ou null se não foi.
     */
    private Localizacao buscarComida()
    {
        List<Localizacao> vizinhas = campo.localizacoesVizinhas(localizacao);
        Iterator<Localizacao> it = vizinhas.iterator();
        while(it.hasNext()) {
            Localizacao onde = it.next();
            Object animal = campo.getObjetoEm(onde);
            if(animal instanceof Coelho) {
                Coelho coelho = (Coelho) animal;
                if(coelho.estaVivo()) { 
                    coelho.morrer();
                    nivelComida = VALOR_COMIDA_COELHO;
                    return onde;
                }
            }
        }
        return null;
    }
    
    /**
     * Verifica se esta raposa deve dar à luz neste passo.
     * Novos nascimentos serão feitos em locais vizinhos livres.
     * @param novasRaposas Uma lista para retornar as raposas recém-nascidas.
     */
    private void reproduzir(List<Raposa> novasRaposas)
    {
        // Novas raposas nascem em locais vizinhos.
        // Obtém uma lista de locais vizinhos livres.
        List<Localizacao> locaisLivres = campo.localizacoesVizinhasLivres(localizacao);
        int nascimentos = procriar();
        for(int n = 0; n < nascimentos && locaisLivres.size() > 0; n++) {
            Localizacao local = locaisLivres.remove(0);
            Raposa filhote = new Raposa(false, campo, local);
            novasRaposas.add(filhote);
        }
    }
        
    /**
     * Gera um número representando o número de nascimentos,
     * se puder procriar.
     * @return O número de nascimentos (pode ser zero).
     */
    private int procriar()
    {
        int nascimentos = 0;
        if(podeProcriar() && rand.nextDouble() <= PROBABILIDADE_REPRODUCAO) {
            nascimentos = rand.nextInt(TAMANHO_MAXIMO_NINHADA) + 1;
        }
        return nascimentos;
    }

    /**
     * Uma raposa pode procriar se tiver atingido a idade de reprodução.
     */
    private boolean podeProcriar()
    {
        return idade >= IDADE_REPRODUCAO;
    }

    /**
     * Indica que a raposa não está mais viva.
     * Ela é removida do campo.
     */
    private void morrer()
    {
        viva = false;
        if(localizacao != null) {
            campo.limpar(localizacao);
            localizacao = null;
            campo = null;
        }
    }
}