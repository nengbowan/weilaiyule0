package top.fwkj51.dto;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommonDto<T> {
    private Integer error;

    private String code;

    private String message;

    private T data;

    private Integer type;

    private Integer id;
}
