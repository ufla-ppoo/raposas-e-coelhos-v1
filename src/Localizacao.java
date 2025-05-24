/**
 * Representa uma localização em uma grade retangular.
 * 
 * @author David J. Barnes e Michael Kölling
 *  Traduzido por Julio César Alves
 * @version 2025.05.08
 */
public class Localizacao
{
    // Posições de linha e coluna.
    private int linha;
    private int coluna;

    /**
     * Representa uma linha e uma coluna.
     * @param linha A linha.
     * @param coluna A coluna.
     */
    public Localizacao(int linha, int coluna)
    {
        this.linha = linha;
        this.coluna = coluna;
    }
    
    /**
     * Implementa igualdade de conteúdo.
     */
    public boolean equals(Object obj)
    {
        if(obj instanceof Localizacao) {
            Localizacao outra = (Localizacao) obj;
            return linha == outra.obterLinha() && coluna == outra.obterColuna();
        }
        else {
            return false;
        }
    }
    
    /**
     * Retorna uma string no formato linha,coluna.
     * @return Uma representação em string da localização.
     */
    public String toString()
    {
        return linha + "," + coluna;
    }
    
    /**
     * Usa os 16 bits superiores para o valor da linha e os inferiores para
     * a coluna. Exceto para grades muito grandes, isso deve fornecer um
     * código hash único para cada par (linha, coluna).
     * @return Um código hash para a localização.
     */
    public int hashCode()
    {
        return (linha << 16) + coluna;
    }
    
    /**
     * @return A linha.
     */
    public int obterLinha()
    {
        return linha;
    }
    
    /**
     * @return A coluna.
     */
    public int obterColuna()
    {
        return coluna;
    }
}