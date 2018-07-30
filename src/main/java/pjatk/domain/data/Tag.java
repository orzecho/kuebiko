package pjatk.domain.data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.NaturalId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
}
