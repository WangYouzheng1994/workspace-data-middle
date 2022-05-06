package com.yqwl.datamiddle.realtime;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/4/15 13:43
 * @Version: V1.0
 */
// @Slf4j
public class DecodePW {
    public static void main(String[] args) {
        System.out.println(decode("UetD6~=!)"));
    }
    public static String decode(String ts_passwd){
        int li_max,li_passwd_len,k,j,i;
        char[] ls_char = new char[95];
        String ls_chars,ls_new="";
        char[] ls_new_passwd;
        ls_chars="`1234567890-=~!@#$%^&*()_+"+
                "qwertyuiop[]\\QWERTYUIOP{}|"+
                "asdfghjkl;ASDFGHJKL:"+
                "zxcvbnm,./ZXCVBNM<>? '\"";
        ls_char=ls_chars.toCharArray();
//        ls_char[94]='\'';
//        ls_char[95]='"';
        li_max=95;
        ls_new_passwd = new char[ts_passwd.length()];
        li_passwd_len=ts_passwd.length();
        for(i=0;i<li_passwd_len;i++) {
            ls_new_passwd[i] = ts_passwd.charAt(i);//mid(ts_passwd,i,1)
            for (j = 0; j < ls_chars.length(); j++) {
                if (ls_new_passwd[i] == ls_char[j]) {
                    break;
                }
            }
            k = j  - i - 1;
//            log.info("k------>{}  li_max---->{}",k,li_max);
            if(k > li_max) {
                k = k - li_max;
            }
//            log.info("k==={} ",k);
            ls_new_passwd[i] = ls_char[k];
//            log.info("char=={}",ls_char[k-1]);

            ls_new = ls_new + ls_new_passwd[i];

        }
        // log.info("解密后密码 = {}",ls_new);
        return ls_new;
    }
}
