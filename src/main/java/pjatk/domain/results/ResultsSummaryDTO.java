package pjatk.domain.results;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ResultsSummaryDTO {
    private String experimentName;
    private Long howManyBlocksHadAtLeastOneMatch;
    private Long howManyMatches;
    private Long howManyTags;
    private Long howManyDataBlocks;
    private List<ResultDTO> results;

    private double p1;
    private double p3;
    private double p5;
    private double pf;

    private double a1;
    private double a3;
    private double a5;
    private double af;

    private double r1;
    private double r3;
    private double r5;
    private double rf;

    private double oneError;
    private double twoError;
}
