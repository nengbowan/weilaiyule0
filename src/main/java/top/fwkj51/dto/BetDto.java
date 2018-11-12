package top.fwkj51.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BetDto {

    private String lottery;

    private String issue;

    private String method;

    private String content;

    private String model;

    private Integer multiple;

    private String code;

    private boolean compress;
}
