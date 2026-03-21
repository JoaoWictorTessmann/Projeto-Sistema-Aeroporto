package sistema.aeroporto.util;

public class CpfUtils {

    public static String limpar(String cpf) {
        if (cpf == null)
            return null;
        return cpf.replaceAll("\\D", "");
    }

    public static boolean validarCpf(String cpf) {
        cpf = limpar(cpf);

        // CPF deve ter 11 dígitos
        if (cpf == null || cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;

            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int primeiroDV = 11 - (soma % 11);
            primeiroDV = (primeiroDV > 9) ? 0 : primeiroDV;

            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int segundoDV = 11 - (soma % 11);
            segundoDV = (segundoDV > 9) ? 0 : segundoDV;

            return cpf.charAt(9) == Character.forDigit(primeiroDV, 10)
                    && cpf.charAt(10) == Character.forDigit(segundoDV, 10);

        } catch (Exception e) {
            return false;
        }
    }

    public static String formatar(String cpf) {
        if (cpf == null)
            return "";
        String numeros = cpf.replaceAll("\\D", "");
        return numeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }
}
