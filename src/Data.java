import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class Data implements Serializable {

    private int dia, mes, ano;
    private boolean erro;

    /**
     * Construtor Data
     *
     * @param dia   argumento que recebe um inteiro que corresponde ao dia
     * @param mes   argumento que recebe um inteiro que corresponde ao mes
     * @param ano_a argumento que recebe um inteiro que corresponde ao ano
     */
    public Data(int dia, int mes, int ano_a) {
        if (!verificacaoAno(ano_a) || verificacaoDia(dia, mes, ano_a) || verificacaoMes(mes)) {
            erro = true;
        } else {

            ano = ano_a;
            this.mes = mes;
            this.dia = dia;
            if (this.mes <= (Calendar.getInstance().get(Calendar.MONTH) + 1)) {
                if (this.mes < (Calendar.getInstance().get(Calendar.MONTH) + 1))
                    erro = true;
                if (this.mes == (Calendar.getInstance().get(Calendar.MONTH) + 1) && this.dia <= Calendar.getInstance().get(Calendar.DAY_OF_MONTH)) {
                    erro = true;
                }
            } else {
                erro = false;
            }
        }
    }

    /**
     * Construtor para a criaçao de um objeto Data
     *
     * @param dia   argumento que recebe um inteiro que corresponde ao dia
     * @param mes   argumento que recebe um inteiro que corresponde ao mes
     * @param ano_a argumento que recebe um inteiro que corresponde ao ano
     * @param anoR  arguemto que serve de referencia /comparaçao para a validaçao da data
     */
    public Data(int dia, int mes, int ano_a, int anoR) {
        if (!verificacaoAno(ano_a, anoR) || verificacaoDia(dia, mes, ano_a) || verificacaoMes(mes)) {
            erro = true;
        } else {
            ano = ano_a;
            this.mes = mes;
            this.dia = dia;
            erro = false;
        }
    }


    private boolean verificacaoAno(int ano) {
        return ano >= Calendar.getInstance().get(Calendar.YEAR) || ano >= (Calendar.getInstance().get(Calendar.YEAR) + 1);

    }

    private boolean verificacaoAno(int ano, int anoR) {
        return ano <= Calendar.getInstance().get(Calendar.YEAR) || ano >= anoR;

    }


    private boolean verificacaoDia(int dia, int mes, int ano) {
        if (mes == 2 && ano % 4 != 0) {
            return dia < 1 || dia > 28;
        } else if (mes == 2 /*&& ano % 4 == 0*/) {
            return dia < 1 || dia > 29;
        } else {
            if (mes < 7 && mes % 2 == 0) {
                return dia < 1 || dia > 30;
            } else if (mes > 7 && mes % 2 != 0) {
                return dia < 1 || dia > 30;
            } else {
                return dia < 1 || dia > 31;
            }
        }
    }

    private boolean verificacaoMes(int mes) {
        return mes < 1 || mes > 12;
    }

    /**
     * toString
     * Metodo que cria e fornece uma string com os atributos do objeto desta classe
     *
     * @return String com os atributos do objeto
     */
    @Override
    public String toString() {
        if (!erro) {
            return dia + "/" + mes + "/" + ano;
        } else {
            return  Calendar.getInstance().get(Calendar.DAY_OF_MONTH) + "/" + Calendar.getInstance().get(Calendar.MONTH) + "/" + (Calendar.getInstance().get(Calendar.YEAR) + 1);
        }

    }


//SET

    /**
     * Metodo que atrbui o valor dado como arguemento  ao ano
     *
     * @param ano Valor que vai ser atribuido
     */
    public void setAno(int ano) {
        this.ano = ano;
    }

    /**
     * Metodo que atrbui o valor dado como arguemento  ao mes
     *
     * @param mes Valor que vai ser atribuido
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * Metodo que atrbui valor dado como arguemento ao  erro
     *
     * @param erro Valor que vai ser atribuido
     */

    public void setErro(boolean erro) {
        this.erro = erro;
    }

    /**
     * Metodo que atrbui valor dado como arguemento ao dia
     *
     * @param dia Valor que vai ser atribuido
     */
    public void setDia(int dia) {
        this.dia = dia;
    }


//GETT

    /**
     * Metodo que devolve um valor  o ano
     *
     * @return Ano da data
     */
    public int getAno() {
        return ano;
    }

    /**
     * Metodo que devolve um valor  o dia
     *
     * @return Dia da data
     */
    public int getDia() {
        return dia;
    }

    /**
     * Metodo que devolve um valor  o mes
     *
     * @return Mes da data
     */
    public int getMes() {
        return mes;
    }

    /**
     * Metodo que devolve  um valor  o erro (data valida ou nao)
     *
     * @return erro
     */
    public boolean getERRO() {
        return erro;
    }
}
