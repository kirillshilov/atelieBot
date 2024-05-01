package com.sarataza.atelieBot.Util;

import com.sarataza.atelieBot.Exception.PhoneFormatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhoneService {
    public String formatPhone(String phone){
        if (phone.contains("[a-zA-Z]") || phone.contains("[а-яА-Я]")){
            throw new PhoneFormatException();
        }
        if (phone.length() == 11){
            StringBuilder sb = new StringBuilder();
            if (phone.startsWith("8")){
                StringBuilder phone1 = new StringBuilder(phone);
                phone1.deleteCharAt(0);
                sb.append("+7");
                sb.append(phone1);
               return sb.toString();
            } else if (phone.startsWith("79")) {
                sb.append("+");
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
            if (phone.startsWith("+79")){
                return phone;
            } else throw new PhoneFormatException();
        }
        throw new PhoneFormatException();
    }
}
