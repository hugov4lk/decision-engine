package ee.test.loan.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PersonalCodeValidator implements ConstraintValidator<ValidPersonalCode, String> {

    @Override
    public boolean isValid(String personalCode, ConstraintValidatorContext context) {
        if (personalCode == null || personalCode.length() != 11) {
            return false;
        }

        try {
            int[] multipliers1 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 1};
            int[] multipliers2 = {3, 4, 5, 6, 7, 8, 9, 1, 2, 3};
            int controlDigit = Character.getNumericValue(personalCode.charAt(10));
            int sum = 0;

            for (int i = 0; i < 10; i++) {
                sum += Character.getNumericValue(personalCode.charAt(i)) * multipliers1[i];
            }

            int mod = sum % 11;
            if (mod == 10) {
                sum = 0;
                for (int i = 0; i < 10; i++) {
                    sum += Character.getNumericValue(personalCode.charAt(i)) * multipliers2[i];
                }
                mod = sum % 11;
                if (mod == 10) {
                    mod = 0;
                }
            }

            return mod == controlDigit;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}