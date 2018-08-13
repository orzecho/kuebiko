package pjatk.domain.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Class holds information about one consistent piece of data f.g. one article or one tweet.
 * @author Michał Dąbrowski
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Entity
public class DataBlock {
    @Id
    @GeneratedValue
    private Long id;
    private DataSource origin;
    private LocalDate date;
    @Lob
    private String content;
    @Column(unique = true)
    private String contentHash;
    @ManyToMany
    private List<Tag> tags;
    @Setter
    private Boolean word2VecUnprocessed = true;
    @Setter
    private Double cosineSimilarityToBadWords;
    @Setter
    private Double cosineSimilarityToGoodWords;

    public void createContentHash() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert messageDigest != null;
        messageDigest.update(this.content.getBytes());
        this.contentHash = new String(messageDigest.digest());
    }
}
