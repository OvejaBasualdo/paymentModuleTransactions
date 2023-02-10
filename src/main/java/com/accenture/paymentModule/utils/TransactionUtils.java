package com.accenture.paymentModule.utils;

import com.accenture.paymentModule.dtos.TransactionDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public final class TransactionUtils {
    public static Boolean checkingEmptyData(TransactionDTO transactionDTO) throws Exception {
        if (transactionDTO.getFromAccount().isEmpty() || transactionDTO.getToAccount().isEmpty()
                || transactionDTO.getTransactionType() == null || transactionDTO.getPaymentType() == null
                || transactionDTO.getTransactionDate().toString().isEmpty()
                || transactionDTO.getAmount().toString().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public static Boolean verifyNumber(String number) {
        try {
            Double a = Double.parseDouble(number);
            System.out.printf(String.valueOf(a));
            System.out.printf("Integer " + a);
            return false;
        } catch (NumberFormatException e) {
            e.getMessage();
            return true;
        }
    }

    public static Boolean isAmountBiggerThanZero(TransactionDTO transactionDTO) throws Exception {
        int a = BigDecimal.ZERO.compareTo(transactionDTO.getAmount());
        if (a == -1) {
            return false;
        } else {
            return true;
        }
    }
    public static Boolean isAmountBiggerThan(TransactionDTO transactionDTO, BigDecimal bigDecimal) throws Exception {
        int a = bigDecimal.compareTo(transactionDTO.getAmount());
        if (a == -1) {
            return true;
        } else {
            return false;
        }
    }
    public static Boolean haveTwoDecimal(BigDecimal number) {

        BigDecimal bd = number.setScale(2, RoundingMode.HALF_UP);
        int a = number.subtract(bd).compareTo(BigDecimal.ZERO);
        try {
            if (a != 0) {
                return true;
            } else {
                return false;
            }
        } catch (NumberFormatException e) {
            e.getMessage();
            return false;
        }
    }

    public static Boolean dateBefore(LocalDateTime date) {
        if (date.toLocalDate().isBefore(LocalDate.now())) {
            return true;
        } else {
            return false;
        }
    }
    public static boolean checkFormatDate(LocalDate dateTime) {
        boolean isCorrect = true;
        try {
            SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");
            formatDate.setLenient(false);
            String day = Integer.toString(dateTime.getDayOfMonth());
            String month = Integer.toString(dateTime.getMonthValue());
            String year = Integer.toString(dateTime.getYear());
            formatDate.parse( day + "/" + month + "/" + year );
            isCorrect = false;
        } catch (ParseException e) {
            isCorrect = true;
        }
        return isCorrect;
    }

}
