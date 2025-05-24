/**
 * Fornece um contador para um participante na simulação.
 * Isso inclui uma string identificadora e uma contagem de quantos
 * participantes desse tipo atualmente existem na simulação.
 * 
 * @author David J. Barnes e Michael Kölling
 *  Traduzido por Julio César Alves
 * @version 2025.05.08
 */
public class Contador
{
    // Um nome para este tipo de participante da simulação.
    private String nome;
    // Quantos deste tipo existem na simulação.
    private int contagem;

    /**
     * Fornece um nome para um dos tipos da simulação.
     * @param nome Um nome, por exemplo, "Raposa".
     */
    public Contador(String nome)
    {
        this.nome = nome;
        contagem = 0;
    }
    
    /**
     * @return A descrição curta deste tipo.
     */
    public String obterNome()
    {
        return nome;
    }

    /**
     * @return A contagem atual para este tipo.
     */
    public int obterContagem()
    {
        return contagem;
    }

    /**
     * Incrementa a contagem atual em um.
     */
    public void incrementar()
    {
        contagem++;
    }
    
    /**
     * Redefine a contagem atual para zero.
     */
    public void reiniciar()
    {
        contagem = 0;
    }
}