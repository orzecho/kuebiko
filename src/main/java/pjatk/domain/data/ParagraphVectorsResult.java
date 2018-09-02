package pjatk.domain.data;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class ParagraphVectorsResult {
    @Id
    @GeneratedValue
    private Long id;
    private String experimentName;
    @ManyToOne
    private DataBlock dataBlock;
    @OneToMany(mappedBy = "result")
    private List<Score> scores;
}
