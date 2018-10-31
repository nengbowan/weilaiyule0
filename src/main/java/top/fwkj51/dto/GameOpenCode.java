package top.fwkj51.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameOpenCode {
    private Long id;

    private String lottery;

    private String issue;

    private String code;

    private String code1;

    private String code2;

    private String openTime;

    private Integer clearStatus;

    private String clearTime;

}
