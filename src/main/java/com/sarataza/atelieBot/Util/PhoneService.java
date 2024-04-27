package com.sarataza.atelieBot.Util;

import com.sarataza.atelieBot.Exception.PhoneFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneService {
    public String formatPhone(String phone){
        if (phone.contains("[a-zA-Z]") || phone.contains("[а-яА-Я]")){
            throw new PhoneFormatException();
        }
        if (phone.length() == 11){
            if (phone.startsWith("8")){
                StringBuilder sb = new StringBuilder();
                StringBuilder phone1 = new StringBuilder(phone);
                phone1.deleteCharAt(1);
                sb.append("+7");
                sb.append(phone);
               return sb.toString();
            } else throw new PhoneFormatException();
        }
        else if (phone.length() == 10){
            if (phone.startsWith("9")){
                StringBuilder sb = new StringBuilder();
                sb.append("+7");
                sb.append(phone);
                return sb.toString();
            } else throw new PhoneFormatException();
        }
        else if (phone.length() == 12){
            if (phone.startsWith("+7")){
                return phone;
            } else throw new PhoneFormatException();
        }
        throw new PhoneFormatException();
    }
}
