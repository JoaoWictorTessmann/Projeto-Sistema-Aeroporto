package sistema.aeroporto.util;

public class CpfUtils {

    // Remove caracteres que não sejam números
    public static String limpar(String cpf) {
        if (cpf == null) return null;
        return cpf.replaceAll("\\D", "");
    }

    // Valida CPF
    public static boolean validarCpf(String cpf) {
        cpf = limpar(cpf);

        // CPF deve ter 11 dígitos
        if (cpf == null || cpf.length() != 11) {
            return false;
        }

        // Rejeita números repetidos (00000000000, 11111111111, etc.)
        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        try {
            int soma = 0;
            int peso = 10;

            // Calcula primeiro dígito verificador
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int primeiroDV = 11 - (soma % 11);
            primeiroDV = (primeiroDV > 9) ? 0 : primeiroDV;

            // Calcula segundo dígito verificador
            soma = 0;
            peso = 11;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpf.charAt(i)) * peso--;
            }

            int segundoDV = 11 - (soma % 11);
            segundoDV = (segundoDV > 9) ? 0 : segundoDV;

            // Compara com os dígitos reais
            return cpf.charAt(9) == Character.forDigit(primeiroDV, 10)
                    && cpf.charAt(10) == Character.forDigit(segundoDV, 10);

        } catch (Exception e) {
            return false;
        }
    }
}
