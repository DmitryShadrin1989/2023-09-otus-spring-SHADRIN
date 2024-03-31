package ru.otus.hw.data;

import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommentTestData {

    private static final List<Book> BOOKS = BookTestData.getBooks();

    public static Map<String, List<Comment>> getMapBooksComments() {
        Map<String, List<Comment>> mapBooksComments = new HashMap<>();
        Book book1 = BOOKS.get(0);
        mapBooksComments.put(book1.getId(), List.of(new Comment("id_1", "comment_1", book1),
                new Comment("id_2", "comment_2", book1),
                new Comment("id_3", "comment_3", book1)));
        Book book2 = BOOKS.get(1);
        mapBooksComments.put(book2.getId(), List.of(new Comment("id_4", "comment_4", book2),
                new Comment("id_5", "comment_5", book2),
                new Comment("id_6", "comment_6", book2)));
        Book book3 = BOOKS.get(2);
        mapBooksComments.put(book3.getId(), List.of(new Comment("id_7", "comment_7", book3),
                new Comment("id_8", "comment_8", book3),
                new Comment("id_9", "comment_9", book3)));
        return mapBooksComments;
    }

    public static List<Comment> getComments() {
        return getMapBooksComments().values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public static Comment getNewComment() {
        return new Comment("id_7", "new_comment", BOOKS.get(2));
    }

    public static Comment getChangeComment() {
        return new Comment("id_1", "change_comment", BOOKS.get(2));
    }
}
