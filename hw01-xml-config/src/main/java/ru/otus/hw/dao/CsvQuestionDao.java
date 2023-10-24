package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        String fileName = fileNameProvider.getTestFileName();
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (isNull(inputStream)) {
            throw new QuestionReadException("Test file resource not found", new FileNotFoundException());
        }
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream)) {
            List<QuestionDto> questionDtoList = new CsvToBeanBuilder<QuestionDto>(inputStreamReader)
                    .withSeparator('$')
                    .withSkipLines(1)
                    .withType(QuestionDto.class)
                    .build()
                    .parse();
            return questionDtoList.stream()
                    .map(QuestionDto::toDomainObject)
                    .toList();
        } catch (Exception e) {
            throw new QuestionReadException("Error when reading questions", e);
        }
    }
}