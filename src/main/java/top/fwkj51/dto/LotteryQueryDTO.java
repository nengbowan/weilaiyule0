package top.fwkj51.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LotteryQueryDTO {
    private float lotteryBalance;

    private Integer baccaratBalance;

    private Integer msgCount;

    private GameOpenCode gameOpenCode;

    private boolean hasNewNotice;

}
