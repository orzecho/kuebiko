package pjatk.domain.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Score {
    @Id
    @GeneratedValue
    private Long id;
    private String label;
    private Double value;
    @Setter
    @ManyToOne
    private ParagraphVectorsResult result;
}
