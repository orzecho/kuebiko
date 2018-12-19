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
}
