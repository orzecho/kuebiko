package pjatk.api;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import pjatk.domain.data.ParagraphVectorsResult;
import pjatk.domain.data.Score;
import pjatk.domain.data.Tag;
import pjatk.domain.results.AssignedTagDTO;
import pjatk.domain.results.ResultDTO;
import pjatk.domain.results.ResultsSummaryDTO;
import pjatk.persist.ParagraphVectorsResultRepository;

@RequestMapping("/results")
@RestController
@RequiredArgsConstructor
@Slf4j
public class ResultsController {
    private final ParagraphVectorsResultRepository paragraphVectorsResultRepository;

    @GetMapping("/{experimentName}")
    public ResultsSummaryDTO getResultsSummary(@PathVariable String experimentName) {
        List<ParagraphVectorsResult> results = paragraphVectorsResultRepository.findByExperimentName(experimentName);
        Long howManyTags = results.stream().map(e -> e.getDataBlock().getTags())
                .flatMap(List::stream)
                .map(Tag::getId)
                .distinct().count();
        List<ResultDTO> resultsDTO = results.stream().map(result -> {
            List<AssignedTagDTO> top5AssignedTags = result.getScores()
                    .stream()
                    .sorted(Comparator.comparingDouble(Score::getValue)).limit(5l)
                    .map(e -> AssignedTagDTO.builder().name(e.getLabel()).score(e.getValue()).build())
                    .collect(Collectors.toList());
            List<AssignedTagDTO> assignedTags = result.getScores()
                    .stream()
                    .sorted(Comparator.comparingDouble(Score::getValue))
                    .map(e -> AssignedTagDTO.builder().name(e.getLabel()).score(e.getValue()).build())
                    .collect(Collectors.toList());
            List<String> originalTags = result.getDataBlock().getTags().stream().map(Tag::getContent)
                    .collect(Collectors.toList());
            Long howManyMatch = originalTags.stream().filter(originalTag -> top5AssignedTags.stream()
                    .anyMatch(t -> t.getName().equals(originalTag))).count();
            return ResultDTO.builder()
                    .content(result.getDataBlock().getContent())
                    .originalTags(originalTags)
                    .assignedTags(assignedTags)
                    .howManyMatch(howManyMatch.intValue())
                    .anyMatch(howManyMatch > 0)
                    .build();
            }).collect(Collectors.toList());
        val summary = ResultsSummaryDTO.builder()
                .experimentName(experimentName)
                .howManyBlocksHadAtLeastOneMatch(resultsDTO.stream().filter(ResultDTO::isAnyMatch).count())
                .howManyDataBlocks(resultsDTO.stream().count())
                .howManyMatches((resultsDTO.stream()
                        .mapToLong(resultDTO -> Long.valueOf(resultDTO.getHowManyMatch()))).sum())
                .howManyTags(howManyTags)
                .results(resultsDTO)
                .build();

        try {
            writeSummaryTable(summary);
            writeSummaryDetailsTable(summary);
        } catch (IOException exception) {
            log.error(exception.getMessage());
        }
        return summary;
    }

    private void writeSummaryTable(ResultsSummaryDTO summary) throws IOException {
        FileWriter fileWriter = new FileWriter(summary.getExperimentName() + "-" + LocalDateTime.now().toString());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Experiment name&Processed articles&Processed tags&No matching tags&"
                + "No articles with matching tags\\\\%n");
        printWriter.printf("%s&%s&%s&%s&%s\\\\%n",
                summary.getExperimentName(), summary.getHowManyDataBlocks().toString(),
                summary.getHowManyTags().toString(),
                summary.getHowManyMatches().toString(),
                summary.getHowManyBlocksHadAtLeastOneMatch().toString());
        printWriter.close();
    }

    private void writeSummaryDetailsTable(ResultsSummaryDTO summary) throws IOException {
        FileWriter fileWriter = new FileWriter(summary.getExperimentName() + "-details-" + LocalDateTime.now()
                .toString());
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.printf("Original tags&Assigned tags&NoM&AnyM\\\\%n");
        summary.getResults().forEach(r ->
            printWriter.printf("%s&%s&%s&%s\\\\%n",
                    String.join(", ", r.getOriginalTags().stream()
                            .map(e -> e.replaceAll("#", "")).collect(Collectors.toList())),
                    String.join(", ", r.getAssignedTags().stream()
                            .limit(5)
                            .map(e -> String.format("%s(%.2f)", e.getName().replaceAll("#",""),
                                    e.getScore()))
                            .collect(Collectors.toList())),
                    r.getHowManyMatch(),
                    r.isAnyMatch()));
        printWriter.close();
    }
}
