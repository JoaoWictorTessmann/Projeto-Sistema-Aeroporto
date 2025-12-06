package sistema.aeroporto.util;

public class CnpjUtils {
    public static boolean validarCnpj(String cnpj) {
        cnpj = cnpj.replaceAll("\\D", "");

        if (cnpj.length() != 14) return false;
        if (cnpj.chars().distinct().count() == 1) return false;

        int[] peso1 = {5,4,3,2,9,8,7,6,5,4,3,2};
        int[] peso2 = {6,5,4,3,2,9,8,7,6,5,4,3,2};

        try {
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso1[i];
            }
            int mod = soma % 11;
            int digito1 = (mod < 2) ? 0 : 11 - mod;

            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso2[i];
            }
            mod = soma % 11;
            int digito2 = (mod < 2) ? 0 : 11 - mod;

            return digito1 == Character.getNumericValue(cnpj.charAt(12)) &&
                   digito2 == Character.getNumericValue(cnpj.charAt(13));
        } catch (Exception e) {
            return false;
        }
    }
}
