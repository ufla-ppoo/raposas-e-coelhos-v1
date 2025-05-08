import java.util.Random;

/**
 * Fornece controle sobre a randomização da simulação. Usando o randomizador 
 * compartilhado com uma semente fixa, execuções repetidas terão exatamente o mesmo 
 * comportamento (o que ajuda nos testes). Defina 'usarCompartilhado' como falso 
 * para obter um comportamento aleatório diferente a cada vez.
 * 
 * @author David J. Barnes e Michael Kölling
 *  Traduzido por Julio César Alves
 * @version 2025.05.08
 */
public class Randomizador
{
    // A semente padrão para controle da randomização.
    private static final int SEMENTE = 1111;
    // Um objeto Random compartilhado, se necessário.
    private static final Random rand = new Random(SEMENTE);
    // Determina se um gerador aleatório compartilhado deve ser fornecido.
    private static final boolean usarCompartilhado = true;

    /**
     * Construtor para objetos da classe Randomizador.
     */
    public Randomizador()
    {
    }

    /**
     * Fornece um gerador aleatório.
     * @return Um objeto Random.
     */
    public static Random getRandom()
    {
        if(usarCompartilhado) {
            return rand;
        }
        else {
            return new Random();
        }
    }
    
    /**
     * Reseta a randomização.
     * Isso não terá efeito se a randomização não for feita através de um gerador Random compartilhado.
     */
    public static void resetar()
    {
        if(usarCompartilhado) {
            rand.setSeed(SEMENTE);
        }
    }
}