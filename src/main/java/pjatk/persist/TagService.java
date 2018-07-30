package pjatk.persist;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pjatk.domain.data.Tag;

@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository tagRepository;

    public Tag findOrCreateTag(String content) {
        return tagRepository.findByContent(content).orElseGet(() -> createTag(content));
    }

    private Tag createTag(String content) {
        Tag tag = Tag.builder()
                .content(content)
                .build();
        return tagRepository.save(tag);
    }
}
