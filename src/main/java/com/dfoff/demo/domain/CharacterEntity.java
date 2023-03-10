package com.dfoff.demo.domain;

import com.dfoff.demo.domain.jsondtos.CharacterAbilityDto;
import com.dfoff.demo.domain.jsondtos.CharacterDto;
import com.dfoff.demo.utils.CharactersUtil;
import com.dfoff.demo.utils.RestTemplateUtil;
import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.dfoff.demo.utils.CharactersUtil.timesAgo;


@Entity
@Getter
@ToString
@NoArgsConstructor (access = AccessLevel.PROTECTED)
@AllArgsConstructor (access = AccessLevel.PRIVATE)
@Builder
public class CharacterEntity extends AuditingFields {
    @Id
    private String characterId;
    @Column(nullable = false)
    @Setter
    private String serverId;
    @Setter
    private String characterName;
    @Column(nullable = false)
    @Setter
    private Integer level;
    @Column(nullable = false)
    @Setter
    private String jobId;
    @Column(nullable = false)
    @Setter
    private String jobGrowId;
    @Column(nullable = false)
    @Setter
    private String jobName;
    @Column(nullable = false)
    @Setter
    private String jobGrowName;
    @Setter
    @Builder.Default
    private Integer adventureFame = 0;

    @Setter
    private String adventureName;

    @Setter
    @Builder.Default
    private Integer buffPower = 0;

    @Setter
    @Builder.Default
    private Integer damageIncrease = 0;

    @Setter
    private String guildId;

    @Setter
    private String guildName;

    @OneToMany(fetch = FetchType.LAZY)
    @ToString.Exclude
    @Builder.Default
    private final Set<UserAccountCharacterMapper> userAccount = new LinkedHashSet<>();


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CharacterEntity that)) return false;
        return characterId.equals(that.characterId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(characterId);
    }

    /**
     * A DTO for the {@link CharacterEntity} entity
     */
    @Data
    @Builder
    public static class CharacterEntityDto implements Serializable {
        private final LocalDateTime createdAt;
        private final String createdBy;
        private final LocalDateTime modifiedAt;
        private final String modifiedBy;
        private final String characterId;
        private final String characterName;

        private final String serverId;

        private final Integer level;

        private final String jobId;

        private final String jobGrowId;

        private final String jobName;

        private final String jobGrowName;

        private final Integer adventureFame;
        private final String adventureName;

        private final String guildId;

        private final String guildName;

        private final Integer buffPower;

        private final Integer damageIncrease;


        public static CharacterEntity toEntity(CharacterEntityDto dto) {
            return CharacterEntity.builder()
                    .characterId(dto.getCharacterId())
                    .characterName(dto.getCharacterName())
                    .serverId(dto.getServerId())
                    .level(dto.getLevel())
                    .jobId(dto.getJobId())
                    .jobGrowId(dto.getJobGrowId())
                    .jobName(dto.getJobName())
                    .jobGrowName(dto.getJobGrowName())
                    .adventureFame(dto.getAdventureFame())
                    .adventureName(dto.getAdventureName())
                    .damageIncrease(dto.getDamageIncrease())
                    .buffPower(dto.getBuffPower())
                    .guildId(dto.getGuildId())
                    .guildName(dto.getGuildName())
                    .build();
        }

        public static CharacterEntityDto from(CharacterDto dto) {
            return CharacterEntityDto.builder()
                    .characterId(dto.getCharacterId())
                    .characterName(dto.getCharacterName())
                    .serverId(dto.getServerId())
                    .level(dto.getLevel())
                    .jobId(dto.getJobId())
                    .jobGrowId(dto.getJobGrowId())
                    .jobName(dto.getJobName())
                    .jobGrowName(dto.getJobGrowName())
                    .build();
        }


        public static CharacterEntityDto from(CharacterEntityResponse response) {
            return CharacterEntityDto.builder()
                    .characterId(response.getCharacterId())
                    .characterName(response.getCharacterName())
                    .serverId(response.getServerId())
                    .level(response.getLevel())
                    .jobId(response.getJobId())
                    .jobGrowId(response.getJobGrowId())
                    .jobName(response.getJobName())
                    .jobGrowName(response.getJobGrowName())
                    .adventureFame(response.getAdventureFame())
                    .adventureName(response.getAdventureName())
                    .guildId(response.getGuildId())
                    .guildName(response.getGuildName())
                    .buffPower(response.getBuffPower())
                    .damageIncrease(response.getDamageIncrease())
                    .build();
        }

        public static CharacterEntity toEntity(CharacterDto dto) {
            return CharacterEntity.builder()
                    .characterId(dto.getCharacterId())
                    .characterName(dto.getCharacterName())
                    .level(dto.getLevel())
                    .jobId(dto.getJobId())
                    .jobGrowId(dto.getJobGrowId())
                    .jobName(dto.getJobName())
                    .jobGrowName(dto.getJobGrowName())
                    .serverId(dto.getServerId())
                    .build();
        }

        public static String getServerName(String serverId) {
            if (serverId.equals("bakal")) {
                return "??????";
            } else if (serverId.equals("cain")) {
                return "??????";
            } else if (serverId.equals("diregie")) {
                return "????????????";
            } else if (serverId.equals("hilder")) {
                return "??????";
            } else if (serverId.equals("prey")) {
                return "?????????";
            } else if (serverId.equals("siroco")) {
                return "?????????";
            } else if (serverId.equals("casillas")) {
                return "????????????";
            } else if (serverId.equals("anton")) {
                return "??????";
            } else {
                return serverId;
            }
        }

        public static CharacterEntityDto from(CharacterEntity entity) {
            return CharacterEntityDto.builder()
                    .characterId(entity.getCharacterId())
                    .characterName(entity.getCharacterName())
                    .serverId(entity.getServerId())
                    .level(entity.getLevel())
                    .jobId(entity.getJobId())
                    .jobGrowId(entity.getJobGrowId())
                    .jobName(entity.getJobName())
                    .jobGrowName(entity.getJobGrowName())
                    .adventureFame(entity.getAdventureFame())
                    .adventureName(entity.getAdventureName())
                    .guildId(entity.getGuildId())
                    .guildName(entity.getGuildName())
                    .buffPower(entity.getBuffPower())
                    .damageIncrease(entity.getDamageIncrease())
                    .build();
        }


    }

    @Getter
    @Data
    @Builder
    public static class CharacterEntityResponse implements Serializable {
        private final String createdAt;
        private final String modifiedAt;
        private final String characterId;
        private final String characterName;

        private final String serverId;

        private final String serverName;

        private final Integer level;

        private final String jobId;

        private final String jobGrowId;

        private final String jobName;

        private final String jobGrowName;

        private final Integer adventureFame;
        private final String adventureName;

        private final String guildId;

        private final String guildName;

        private final Integer buffPower;

        private final Integer damageIncrease;

        private final List<CharacterAbilityDto.Status__1> status;


        public static CharacterEntityResponse from(CharacterEntityDto dto) {
            return CharacterEntityResponse.builder()
                    .characterId(dto.getCharacterId())
                    .characterName(dto.getCharacterName())
                    .serverId(dto.getServerId())
                    .level(dto.getLevel())
                    .jobId(dto.getJobId())
                    .jobGrowId(dto.getJobGrowId())
                    .jobName(dto.getJobName())
                    .jobGrowName(dto.getJobGrowName())
                    .adventureFame(dto.getAdventureFame())
                    .adventureName(dto.getAdventureName())
                    .serverName(getServerName(dto.getServerId()))
                    .guildId(dto.getGuildId())
                    .guildName(dto.getGuildName())
                    .modifiedAt(dto.getModifiedAt() != null ? timesAgo(dto.getModifiedAt()) : "?????? ???")
                    .createdAt(dto.getCreatedAt() != null ? timesAgo(dto.getCreatedAt()) : "?????? ???")
                    .buffPower(dto.getBuffPower())
                    .damageIncrease(dto.getDamageIncrease())
                    .build();
        }

        public static CharacterEntityResponse from(CharacterEntity dto) {
            return CharacterEntityResponse.builder()
                    .characterId(dto.getCharacterId())
                    .characterName(dto.getCharacterName())
                    .serverId(dto.getServerId())
                    .level(dto.getLevel())
                    .jobId(dto.getJobId())
                    .jobGrowId(dto.getJobGrowId())
                    .jobName(dto.getJobName())
                    .jobGrowName(dto.getJobGrowName())
                    .adventureFame(dto.getAdventureFame())
                    .adventureName(dto.getAdventureName())
                    .serverName(getServerName(dto.getServerId()))
                    .guildId(dto.getGuildId())
                    .guildName(dto.getGuildName())
                    .modifiedAt(dto.getModifiedAt() != null ? timesAgo(dto.getModifiedAt()) : "?????? ???")
                    .build();
        }


        public static CharacterEntityResponse from(CharacterAbilityDto dto, String serverId) {
            return CharacterEntityResponse.builder()
                    .characterId(dto.getCharacterId())
                    .characterName(dto.getCharacterName())
                    .serverId(serverId)
                    .level(dto.getLevel())
                    .jobId(dto.getJobId())
                    .jobGrowId(dto.getJobGrowId())
                    .jobName(dto.getJobName())
                    .jobGrowName(dto.getJobGrowName())
                    .adventureFame(dto.getAdventureFame())
                    .adventureName(dto.getAdventureName())
                    .serverName(getServerName(serverId))
                    .guildId(dto.getGuildId())
                    .guildName(dto.getGuildName())
                    .status(dto.getStatus())
                    .build();
        }

        public static String getServerName(String serverId) {
            if (serverId.equals("bakal")) {
                return "??????";
            } else if (serverId.equals("cain")) {
                return "??????";
            } else if (serverId.equals("diregie")) {
                return "????????????";
            } else if (serverId.equals("hilder")) {
                return "??????";
            } else if (serverId.equals("prey")) {
                return "?????????";
            } else if (serverId.equals("siroco")) {
                return "?????????";
            } else if (serverId.equals("casillas")) {
                return "????????????";
            } else if (serverId.equals("anton")) {
                return "??????";
            } else {
                return serverId;
            }
        }

        public static Set<CharacterEntityResponse> from(Set<CharacterEntityDto> dtos) {
            return dtos.stream().map(CharacterEntityResponse::from).collect(Collectors.toSet());
        }

    }


    @Data
    @Builder
    public static class CharacterEntityMainPageResponse implements Serializable {
        private final String modifiedAt;
        private final String characterId;
        private final String characterName;

        private final String serverId;

        private final String serverName;


        private final String jobName;

        private final String jobGrowName;

        private final Integer adventureFame;
        private final String adventureName;

        private final String imgStyleClassName;

        private final Integer damageIncrease;

        private final Integer buffPower;
        private final String characterImgUrl;


        public static CharacterEntityMainPageResponse from(CharacterEntity entity) {
            return CharacterEntityMainPageResponse.builder()
                    .characterId(entity.getCharacterId())
                    .characterName(entity.getCharacterName())
                    .serverId(entity.getServerId())
                    .jobName(entity.getJobName())
                    .jobGrowName(entity.getJobGrowName())
                    .adventureFame(entity.getAdventureFame())
                    .adventureName(entity.getAdventureName())
                    .serverName(CharacterEntityDto.getServerName(entity.getServerId()))
                    .modifiedAt(entity.getModifiedAt() != null ? timesAgo(entity.getModifiedAt()) : "?????? ???")
                    .imgStyleClassName(CharactersUtil.getStyleClassName(entity.getJobName()))
                    .damageIncrease(entity.getDamageIncrease())
                    .buffPower(entity.getBuffPower())
                    .characterImgUrl(RestTemplateUtil.getCharacterImgUri(entity.getServerId(), entity.getCharacterId(), "1"))
                    .build();
        }
    }

    @Data
    @Builder
    public static class CharacterEntityRankingResponse implements Serializable {
        @Setter
        private  Long rank;

        private final String modifiedAt;
        private final String characterId;
        private final String characterName;

        private final String serverId;

        private final String serverName;


        private final String jobName;

        private final String jobGrowName;

        private final Integer adventureFame;
        private final String adventureName;

        private final String imgStyleClassName;

        private final Integer damageIncrease;

        private final Integer buffPower;
        private final String characterImgUrl;


        public static CharacterEntityRankingResponse from(CharacterEntity entity) {
            return CharacterEntityRankingResponse.builder()
                    .characterId(entity.getCharacterId())
                    .characterName(entity.getCharacterName())
                    .serverId(entity.getServerId())
                    .jobName(entity.getJobName())
                    .jobGrowName(entity.getJobGrowName())
                    .adventureFame(entity.getAdventureFame())
                    .adventureName(entity.getAdventureName())
                    .serverName(CharacterEntityDto.getServerName(entity.getServerId()))
                    .modifiedAt(entity.getModifiedAt() != null ? timesAgo(entity.getModifiedAt()) : "?????? ???")
                    .buffPower(entity.getBuffPower())
                    .imgStyleClassName(CharactersUtil.getStyleClassName(entity.getJobName()))
                    .damageIncrease(entity.getDamageIncrease())
                    .characterImgUrl(RestTemplateUtil.getCharacterImgUri(entity.getServerId(), entity.getCharacterId(), "1")).build();

        }
    }
}
