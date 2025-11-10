package com.cosmosboard.fmh.dto.validator;

import com.cosmosboard.fmh.dto.annotation.TC;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class TcValidator implements ConstraintValidator<TC, String> {
    @Override
    public void initialize(TC constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String strNumber, ConstraintValidatorContext context) {
        if (strNumber.length()!= 11 || strNumber.charAt(0)== '0'){
            return false;
        }
        int oddSum=0, evenSum=0, controlDigit= 0;
        for (int i=0; i<=8; i++){
            if (i%2==0){
                oddSum+=Character.getNumericValue(strNumber.charAt(i));

            }else {
                evenSum+=Character.getNumericValue(strNumber.charAt(i));
            }
        }
        controlDigit = (oddSum*7-evenSum)%10;
        if (Character.getNumericValue(strNumber.charAt(9))!=controlDigit){
            return false;
        }
        if (Character.getNumericValue(strNumber.charAt(10))!=(controlDigit+evenSum+oddSum)%10){
            return false;
        }
        return true;
    }
}
