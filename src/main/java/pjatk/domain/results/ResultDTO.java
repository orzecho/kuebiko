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
public class ResultDTO {
    private List<String> originalTags;
    private List<AssignedTagDTO> assignedTags;
    private String content;
    private boolean anyMatch;
    private int howManyMatch;
}
