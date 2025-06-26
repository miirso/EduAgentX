package cumt.miirso.eduagentx.dto.resp;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
public class StudentPaperStatsRespDTO {
    private Long studentId;
    private Integer paperId;
    private Integer totalScore;
    private Integer correctCount;
    private Integer wrongCount;
    private BigDecimal accuracyRate;
    private Integer timeSpent;
    private String status;
    private Timestamp startTime;
    private Timestamp submitTime;
}
