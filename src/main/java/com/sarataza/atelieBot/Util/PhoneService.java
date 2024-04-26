package com.sarataza.atelieBot.Util;

import com.sarataza.atelieBot.Exception.PhoneFormatException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhoneService {
    public String formatPhone(String phone){
        if (phone.length() == 11){
            if (phone.startsWith("8")){
                StringBuilder sb = new StringBuilder(phone);
                sb.setCharAt(1, (char) 7);
               return sb.toString();
            } else throw new PhoneFormatException();
        }
        else if (phone.length() == 10){
            if (phone.startsWith("9")){
                StringBuilder sb = new StringBuilder();
                sb.append(7);
                sb.append(phone);
                return sb.toString();
            } else throw new PhoneFormatException();
        }
        else if (phone.length() == 12){
            if (phone.startsWith("+7")){
                StringBuilder sb = new StringBuilder(phone);
                sb.deleteCharAt(1);
                return sb.toString();
            } else throw new PhoneFormatException();
        }
        throw new PhoneFormatException();
    }
}
