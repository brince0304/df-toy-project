package com.dfoff.demo.Util;

import com.dfoff.demo.Domain.ForCharacter.CharacterBuffEquipmentJsonDto;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SearchPageUtil {

    public static List<String> getBuffPercent(CharacterBuffEquipmentJsonDto dto){
        List<String> list = new ArrayList<>();
        if(dto.getSkill()!=null){
            if(dto.getSkill().getBuff()!=null){
                list.add(dto.getSkill().getBuff().getSkillInfo().getName());
                String desc = dto.getSkill().getBuff().getSkillInfo().getOption().getDesc();
                List<String> val = dto.getSkill().getBuff().getSkillInfo().getOption().getValues();
                StringTokenizer st = new StringTokenizer(desc,"\n");
                int i=0;
                while(st.hasMoreTokens()){
                    String token = st.nextToken();

                    if(token.contains("스킬") || token.contains("데미지")||token.contains("무기")){
                        list.add(dto.getSkill().getBuff().getSkillInfo().getOption().getLevel()+"");
                        list.add(val.get(i)+"%");
                        break;
                    }
                    i++;
                }

            }

        } return list;
    }



        public static long dPlus(LocalDateTime dayBefore) {
            return ChronoUnit.DAYS.between(dayBefore, LocalDateTime.now());
        }

        public static long dMinus(LocalDateTime dayAfter) {
            return ChronoUnit.DAYS.between(dayAfter, LocalDateTime.now());
        }

        public static String timesAgo(LocalDateTime dayBefore) {
            long gap = ChronoUnit.MINUTES.between(dayBefore, LocalDateTime.now());
            String word;
            if (gap == 0) {
                word = "방금 전";
            } else if (gap < 60) {
                word = gap + "분 전";
            } else if (gap < 60 * 24) {
                word = (gap / 60) + "시간 전";
            } else if (gap < 60 * 24 * 10) {
                word = (gap / 60 / 24) + "일 전";
            } else {
                word = dayBefore.format(DateTimeFormatter.ofPattern("MM월 dd일"));
            }
            return word;
        }

        public static String customForm(LocalDateTime date) {
            return date.format(DateTimeFormatter.ofPattern("MM월 dd일"));
        }
}