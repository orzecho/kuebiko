package pjatk.domain.data;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import org.hibernate.annotations.NaturalId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Tag holds some meta information about piece of information.
 * It can be f.g. category of article or hashtag collected from tweet.
 * @author Michał Dąbrowski
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class Tag {
    @Id
    @GeneratedValue
    private Long id;

    @NaturalId
    private String content;

    @Setter
    private String similarWords;

    @Setter
    private boolean paragraphVectorsProcessed = false;

    @ManyToMany
    @JoinTable(name = "DATABLOCK_TAG",
               joinColumns = { @JoinColumn(name = "TAG_ID") },
               inverseJoinColumns = { @JoinColumn(name = "DATABLOCK_ID") })
    private List<DataBlock> dataBlocks;
}
