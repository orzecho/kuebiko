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

    // pn describes precision where n i number of assigned tags taken in the account
    private double p1;
    private double p3;
    private double p5;
    private double pf; // n dependent on number of original tags

    // an describes accuracy where n i number of assigned tags taken in the account
    private double a1;
    private double a3;
    private double a5;
    private double af;

    // rn describes recall where n i number of assigned tags taken in the account
    private double r1;
    private double r3;
    private double r5;
    private double rf;

    // oneError describes if best assigned tag is correct
    private boolean oneError;
    private boolean twoError;
}
