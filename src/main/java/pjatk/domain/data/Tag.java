package pjatk.domain.data;

import lombok.*;

/**
 * Tag holds some meta information about piece of information.
 * It can be f.g. category of article or hashtag collected from tweet.
 * @author Michał Dąbrowski
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class Tag {
    private Long id;
    private String content;
}
