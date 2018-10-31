package top.fwkj51.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDTO {
    private Integer error;

    private String code;

    private String message;

    private LotteryQueryDTO data;

    private Integer type;

    private Integer id;
}
